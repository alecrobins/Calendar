package hkust.cse.calendar.controllers;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import hkust.cse.calendar.gui.CalGrid;
import hkust.cse.calendar.unit.Appt;
import hkust.cse.calendar.unit.Event;
import hkust.cse.calendar.unit.Location;
import hkust.cse.calendar.unit.TimeSpan;
import hkust.cse.calendar.unit.Event.Frequency;

//
// Class responsible for communication between calendar view and
// the event creation/validation process
//

public class EventController {

	// Error messages returned to view so that view knows how 
	// To deal with the return message
	public enum EventReturnMessage {
		SUCCESS, ERROR_TIME_FORMAT, ERROR_PAST_DATE, ERROR_UNFILLED_REQUIRED_FIELDS,
		ERROR_REMINDER, ERROR_EVENT_OVERLAP, ERROR_SECOND_DATE_PAST, ERROR
	}

	// used to set reminders for events
	private CalGrid cal;

	public EventController(CalGrid _cal) {
		cal = _cal;
	}

	// update the view with the newly created event
	public void updateViewWithEvent(Event e, CalGrid parentGrid){
		// send the appt to the list of events
		parentGrid.getAppList().addAppt(e);
		parentGrid.updateAppList();
		System.out.println("updating the view . . . ");
	}

	// Create the event 
	public EventReturnMessage createEvent(
			String _year, String _month, String _day,
			String _sTimeH, String _sTimeM, String _eTimeH, String _eTimeM,
			String _detailArea, String _titleField,
			String _reminderYear, String _reminderMonth, String _reminderDay, String _reminderTimeH, String _reminderTimeM,
			String _frequency, String _location, CalGrid parentGrid){

		// check if required fields were met
		if(_year == null || _month == null || _day == null || _sTimeH == null
				|| _sTimeM == null || _eTimeH == null || _eTimeM ==null || _frequency == null)
			return EventReturnMessage.ERROR_UNFILLED_REQUIRED_FIELDS;

		// format the start / endtime
		Date startTimeDate = formatTime(_year, _month, _day, _sTimeH, _sTimeM);
		Timestamp startTime = new java.sql.Timestamp(startTimeDate.getTime());

		// check if startTiem correct
		if(!checkStartTime(startTime))
			return EventReturnMessage.ERROR_PAST_DATE;

		// format the end time
		Date endTimeDate = formatTime(_year, _month, _day, _eTimeH, _eTimeM);
		Timestamp endTime = new java.sql.Timestamp(endTimeDate.getTime());


		// check for end time that is before start time
		if(!endTime.after(startTime))
			return EventReturnMessage.ERROR_SECOND_DATE_PAST;

		TimeSpan eventTime = new TimeSpan(startTime, endTime);

		Date reminder = null;
		
		// Create the reminder if reminder isn't null
		if(_reminderYear != null && _reminderMonth != null && _reminderDay != null
				&& _reminderTimeH != null && _reminderTimeM != null){
			reminder = formatTime(_reminderYear, _reminderMonth, _reminderDay, _reminderTimeH, _reminderTimeM);
			// check that reminder is before the start time
			if(startTime.before(reminder))
				return EventReturnMessage.ERROR_REMINDER;
		}

		// create the frequency
		Frequency frequency = Frequency.valueOf(_frequency);

		Location location = null;
		// create the location if not null
		if(_location != null)
			location = new Location(_location);


		// MAKE THE EVENT
		Event newEvent = new Event(eventTime, _titleField, _titleField, location, reminder, _detailArea, frequency);

		//TODO: need to check if an event is valid 
		// Check for overlap
		if(eventOverlap(newEvent, cal.controller.mApptStorage.getApptsMap()))
			return EventReturnMessage.ERROR_EVENT_OVERLAP;

		// save the event in apptStorage
		saveEvent(newEvent);

		cal.updateAppList();

		System.out.println(newEvent.toString());

		return EventReturnMessage.SUCCESS;
	}

	public void saveEvent(Event e){
		switch (e.getEventFrequency()){
		case ONETIME:
			cal.controller.mApptStorage.SaveAppt(e);
			break;
		case WEEKLY:
			Event eNew = e;
			for (int i = 0; i < 260; i++)   { //5 years in weeks
				Event eNew1 = new Event(eNew.getEventTime(), eNew.getEventFrequency()) ;
				cal.controller.mApptStorage.SaveAppt(eNew1);
				TimeSpan curr = eNew.getEventTime();
				Timestamp start = new Timestamp(curr.StartTime().getTime()+604800000);
				Timestamp fin = new Timestamp(curr.EndTime().getTime()+604800000);
				eNew.setEventTime(new TimeSpan(start, fin));
			}
			break;
		case MONTHLY:
			Event eNew1 = e;
			for (int i = 0; i < 65; i++){   //5 years in groups of 4 weeks
				Event eNew2 = new Event(eNew1.getEventTime(), eNew1.getEventFrequency()) ;
				cal.controller.mApptStorage.SaveAppt(eNew2);
				TimeSpan curr = eNew1.getEventTime();
				Timestamp start = new Timestamp(curr.StartTime().getTime()+604800000*4);
				Timestamp fin = new Timestamp(curr.EndTime().getTime()+604800000*4);
				eNew1.setEventTime(new TimeSpan(start, fin));
			}
			break;
		case DAILY:
			Event eNew2 = e;
			for (int i = 0; i < 1825; i++){
				Event eNew3 = new Event(eNew2.getEventTime(), eNew2.getEventFrequency()) ;
				cal.controller.mApptStorage.SaveAppt(eNew3);
				TimeSpan curr = eNew2.getEventTime();
				Timestamp start1 = new Timestamp(curr.StartTime().getTime()+86400000);
				Timestamp fin = new Timestamp(curr.EndTime().getTime()+86400000);
				eNew2.setEventTime(new TimeSpan(start1, fin));
			}
			break;
		}
	}

	//
	// Helper functions
	//

	

	// Returns true if there is overlap with other appts
	public boolean eventOverlap(Event e, HashMap<Integer, Appt> as) {
		for (Appt existing: as.values()){
			Event exist = (Event) existing;
			if (existing.TimeSpan().EndTime().getTime() > e.TimeSpan().StartTime().getTime()){
			switch(e.getEventFrequency()){
			case ONETIME:
				if (!isEventValid(e, exist, "absTime")){
					return true;
				}
				break;
			case MONTHLY:
				if (!isEventValid(e, exist, "date")){
					if (!isEventValid(e, exist, "time")){
						return true;
					}
				}
				break;
			case WEEKLY:
				if (!isEventValid(e, exist, "day")){
					if (!isEventValid(e, exist, "time")){
						return true;
					}
				}
				break;
			case DAILY:
				if (!isEventValid(e, exist, "time")){
					return true;
				}
			}
			break;
		}
		}
		return false;
	}
	@SuppressWarnings("deprecation")
	public boolean isEventValid(Event given, Event existing, String compare){
//switch(compare){
//		case "absTime":
//			if (given.TimeSpan().EndTime().getTime() < existing.TimeSpan().StartTime().getTime()
//					|| given.TimeSpan().StartTime().getTime() > existing.TimeSpan().EndTime().getTime()){
//				return true;  //no overlap in absolute time
//			}
//			break;
//		case "date":
//			if (given.TimeSpan().EndTime().getDate() < existing.TimeSpan().StartTime().getDate()
//					|| given.TimeSpan().StartTime().getDate() > existing.TimeSpan().EndTime().getDate()){
//				return true;  //no overlap in date
//			}
//			break;
//		case "day":
//			if (given.TimeSpan().EndTime().getDay() < existing.TimeSpan().StartTime().getDay()
//					|| given.TimeSpan().StartTime().getDay() > existing.TimeSpan().EndTime().getDay()){
//				return true;  //no overlap in day
//			}
//			break;
//		case "time":
//			if (given.TimeSpan().EndTime().getHours() < existing.TimeSpan().StartTime().getHours()
//					|| given.TimeSpan().StartTime().getHours() > existing.TimeSpan().EndTime().getHours()){
//				return true;  //no overlap in hours
//			}
//
//			else if (given.TimeSpan().EndTime().getHours() == existing.TimeSpan().StartTime().getHours()){
//				if (given.TimeSpan().EndTime().getMinutes() < existing.TimeSpan().StartTime().getMinutes()){
//					return true;  //no overlap in mins
//				}
//			}
//			else if (given.TimeSpan().StartTime().getHours() == existing.TimeSpan().EndTime().getHours()){
//				if (given.TimeSpan().StartTime().getMinutes() > existing.TimeSpan().EndTime().getMinutes()){
//					return true;   //no overlap in mins
//				}
//			}
//			break;
//		default :
//			return false;
//		}
		return false;
	}


	//	private boolean checkEventOverlap(TimeSpan eventTime, Frequency _frequency) {
	//		// TODO Auto-generated method stub
	//		return true;
	//	}

	private boolean checkStartTime(Timestamp startTime) {
		// current date
		Date currentDate = cal.mClock.getChangedTimeDate();
		Timestamp currentTime = new Timestamp(currentDate.getTime());
		return startTime.after(currentTime);
	}

	private Date formatTime(String year, String month, String day, String hour, String minute){

		String time = year + "-" + month + "-" + day + " " + hour + ":" + minute + ":00.0";

		Timestamp timestamp = null;

		try {
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS");
			return dateFormat.parse(time);

		}
		catch(Exception e){
			System.out.println(e.getMessage());
		} 

		return timestamp;

	}
}
