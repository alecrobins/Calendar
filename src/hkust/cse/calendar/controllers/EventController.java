package hkust.cse.calendar.controllers;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import hkust.cse.calendar.apptstorage.ApptStorageSQLImpl;
import hkust.cse.calendar.gui.CalGrid;
import hkust.cse.calendar.unit.Appt;
import hkust.cse.calendar.unit.Event;
import hkust.cse.calendar.unit.Location;
import hkust.cse.calendar.unit.TimeSpan;
import hkust.cse.calendar.unit.Appt.Frequency;

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
	private ApptStorageSQLImpl db;

	public EventController(CalGrid _cal) {
		cal = _cal;
		db = new ApptStorageSQLImpl(_cal.getCurrUser());
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
			String _reminderTimeH, String _reminderTimeM,
			String _reminderYear, String _reminderMonth, String _reminderDay,
			String _frequency, String _location, boolean isPub, CalGrid parentGrid){

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
		if (endTimeDate.getHours() == 0){
			endTimeDate.setTime(endTimeDate.getTime()+43200000);
		}
		Timestamp endTime = new java.sql.Timestamp(endTimeDate.getTime());


		// check for end time that is before start time
		if(!endTime.after(startTime))
			return EventReturnMessage.ERROR_SECOND_DATE_PAST;

		TimeSpan eventTime = new TimeSpan(startTime, endTime);

		TimeSpan reminder = null;
		// Create the reminder if reminder isn't null
		if(_reminderYear != null && _reminderMonth != null && _reminderDay != null
				&& _reminderTimeH != null && _reminderTimeM != null){
			Date noteTime = formatTime(_reminderYear, _reminderMonth, _reminderDay, _reminderTimeH, _reminderTimeM);
			System.out.println("Y:" + _reminderYear + " M:" +_reminderMonth + " D:" +_reminderDay + " H:" +_reminderTimeH + " Min:" +_reminderTimeM);
			// check that reminder is before the start time
			if(startTime.before(noteTime))
				return EventReturnMessage.ERROR_REMINDER;
			reminder = new TimeSpan(new Timestamp(noteTime.getTime()), startTime);
		}

		// create the frequency
		Frequency frequency = Frequency.valueOf(_frequency);

		Location location = null;
		// create the location if not null
		if(_location != null){
			location = db.getLocationByName(_location);
		}else{
			return EventReturnMessage.ERROR_UNFILLED_REQUIRED_FIELDS;
		}

		// MAKE THE EVENT
		// delay the saving of the id to the creation
		Appt newEvent = new Appt(eventTime, _titleField, _titleField, location.getLocationID(), reminder, _detailArea, frequency);
		newEvent.setIsPublic(isPub);
		
		//TODO: need to check if an event is valid 
		// Check for overlap
		if(eventOverlap(newEvent, cal.controller.mApptStorage.getApptsMap()))
			return EventReturnMessage.ERROR_EVENT_OVERLAP;

		// save the event in apptStorage
		saveEvent(newEvent);
		
		
		cal.UpdateCal();
		cal.updateAppList();

		System.out.println(newEvent.toString());

		return EventReturnMessage.SUCCESS;
	}

	public void saveEvent(Appt e){
		
		int eventID = -1;
		Appt pastEvent = null;
		TimeSpan curr = null;
		long time = -1;
		
		
		switch (e.getEventFrequency()){
		case ONETIME:
			// save to db
			eventID = db.SaveAppt(e);
			e.setEventID(eventID);
		
			// save to hash map
			cal.controller.mApptStorage.SaveAppt(e);
			
			if(e.getEventReminder() != null){
				// Add notification in to notification array
				cal.controller.addNotification(e.getNotification());
				System.out.println("Notification added successfully");
			}
			
					
			break;
		case WEEKLY:
			eventID = db.SaveAppt(e);
			e.setEventID(eventID);
			
			// save to hash map
			cal.controller.mApptStorage.SaveAppt(e);
			
			if(e.getEventReminder() != null){
				// Add notification in to notification array
				cal.controller.addNotification(e.getNotification());
				System.out.println("Notification added successfully");
			}			
			pastEvent = e;
			
			for (int i = 0; i < 52; i++)   { //1 years in weeks
				
				// Set teh current tim 
				curr = pastEvent.getEventTime();
				Timestamp start = new Timestamp(curr.StartTime().getTime()+604800000);
				Timestamp fin = new Timestamp(curr.EndTime().getTime()+604800000);
				
				// generate the new event
				Appt eNew = formatEvent(start, fin, e);
				// save to db
				eNew.setEventID(eventID);
				
				cal.controller.mApptStorage.SaveAppt(eNew);
				// save to db
				
				pastEvent = eNew;
				
			}
			break;
		case MONTHLY:
			eventID = db.SaveAppt(e);
			e.setEventID(eventID);
			
			cal.controller.mApptStorage.SaveAppt(e);
			
			if(e.getEventReminder() != null){
				// Add notification in to notification array
				cal.controller.addNotification(e.getNotification());
				System.out.println("Notification added successfully");
			}
			
			pastEvent = e;
			
			for (int i = 0; i < 13; i++){   //1 years in groups of 4 weeks
				
				curr = pastEvent.getEventTime();
				
				Timestamp start = curr.StartTime(); 
				Timestamp end = curr.EndTime();
				
				start.setMonth(curr.StartTime().getMonth()+1);
				end.setMonth(curr.EndTime().getMonth()+1);
				
				// generate the new event
				Appt eNew = formatEvent(start, end, e);
				// save to db
				eNew.setEventID(eventID);
				cal.controller.mApptStorage.SaveAppt(eNew);
				
				// set past event
				pastEvent = eNew;
			}
			break;
		case DAILY:
			eventID = db.SaveAppt(e);
			e.setEventID(eventID);
			
			cal.controller.mApptStorage.SaveAppt(e);

			if(e.getEventReminder() != null){
				// Add notification in to notification array
				cal.controller.addNotification(e.getNotification());
				System.out.println("Notification added successfully");
			}
			
			pastEvent = e;
			
			for (int i = 0; i < 365; i++){

				curr = pastEvent.getEventTime();
				Timestamp start = new Timestamp(curr.StartTime().getTime()+86400000);
				Timestamp end = new Timestamp(curr.EndTime().getTime()+86400000);
				// save to db
				
				// generate the new event
				Appt eNew = formatEvent(start, end, e);
				// save to db
				eNew.setEventID(eventID);
				cal.controller.mApptStorage.SaveAppt(eNew);
				// set past event
				pastEvent = eNew;
			}
			break;
		}
		
	}
	
	private Appt formatEvent(Timestamp start, Timestamp end, Appt e){
		Appt eNew = new Appt() ;
		
		// get information for new event
		eNew.setEventFrequency(e.getEventFrequency());
		eNew.setEventTime(new TimeSpan(start, end));
		eNew.setTitle(e.getTitle());
		eNew.setInfo(e.getInfo());
		eNew.setEventLocation(e.getEventLocationID());
		eNew.setIsGroup(e.getIsGroup());
		eNew.setIsPublic(e.getIsPublic());
		
		return eNew;
	}

	//
	// Helper functions
	//

	// Returns true if there is overlap with other appts
	public boolean eventOverlap(Appt e, HashMap<Integer, Appt> appts) {
		
		boolean overlap = false;
		
		TimeSpan purposedTime = null;
		switch (e.getEventFrequency()){
		case ONETIME:
			// purposed time for the event
			purposedTime = e.getEventTime();
			
			// go through each event and check for overlapp with the purposed tiem
			for (Map.Entry<Integer, Appt> entry : appts.entrySet())
			{
				Appt currentAppt = entry.getValue();
				if(isHourOverlap(currentAppt, purposedTime)){
					overlap = true;
					break;
				}
			}
			
			break;
		case WEEKLY:
			
			// purposed time for the event
			purposedTime = e.getEventTime();
			
			for (int i = 0; i < 53; i++)   { //1 years in weeks
				
				// go through each event and check for overlapp with the purposed tiem
				for (Map.Entry<Integer, Appt> entry : appts.entrySet())
				{
					Appt currentAppt = entry.getValue();
					if(isHourOverlap(currentAppt, purposedTime)){
						overlap = true;
						break;
					}
				}
				
				// break if overlap
				if(overlap) break;
				
				// Set teh current tim 
				Timestamp start = new Timestamp(purposedTime.StartTime().getTime()+604800000);
				Timestamp end = new Timestamp(purposedTime.EndTime().getTime()+604800000);
				// reset the purposed time
				purposedTime = new TimeSpan(start, end);
			}
			break;
		case MONTHLY:
			// purposed time for the event
			purposedTime = e.getEventTime();

			for (int i = 0; i < 13; i++){   //1 years in groups of 4 weeks
				
				// go through each event and check for overlapp with the purposed tiem
				for (Map.Entry<Integer, Appt> entry : appts.entrySet())
				{
					Appt currentAppt = entry.getValue();
					if(isHourOverlap(currentAppt, purposedTime)){
						overlap = true;
						break;
					}
				}
				
				// break if overlap
				if(overlap) break;
				
				// reset purposed time
				Timestamp start = purposedTime.StartTime(); 
				Timestamp end = purposedTime.EndTime();
				
				start.setMonth(purposedTime.StartTime().getMonth()+1);
				end.setMonth(purposedTime.EndTime().getMonth()+1);
				
				purposedTime = new TimeSpan(start, end);

			}
			break;
		case DAILY:
			// purposed time for the event
			purposedTime = e.getEventTime();
			
			for (int i = 0; i < 365; i++){

				// go through each event and check for overlapp with the purposed tiem
				for (Map.Entry<Integer, Appt> entry : appts.entrySet())
				{
					Appt currentAppt = entry.getValue();
					if(isHourOverlap(currentAppt, purposedTime)){
						overlap = true;
						break;
					}
				}
				
				// break if overlap
				if(overlap) break;
				
				// reset time
				Timestamp start = new Timestamp(purposedTime.StartTime().getTime()+86400000);
				Timestamp end = new Timestamp(purposedTime.EndTime().getTime()+86400000);
				purposedTime = new TimeSpan(start, end);

			}
			break;
		}
		
		return overlap;
	}
	
	// determine if there is overlap with the purposed time and in the passed in event
	private boolean isHourOverlap(Appt appt, TimeSpan purposed){
		TimeSpan eventTime = appt.getEventTime();
//		
		// check if starttime or end time of propossed is in between start or end time of event
		if( inBetween(purposed.StartTime(), eventTime) || inBetween(purposed.EndTime(), eventTime))
			return true;
		else if( beforeAndAfter(purposed, eventTime))
			return true;
		
		return false;
	}
	
	// check if t1 is in between t1 or tw
	private boolean inBetween(Timestamp t1, TimeSpan t2){
		return t1.getTime() > t2.StartTime().getTime() && t1.getTime() < t2.EndTime().getTime();
	}
	
	// check if the start time AND end of t1 start at before AND after t2
	private boolean beforeAndAfter(TimeSpan t1, TimeSpan t2){
		return t1.StartTime().getTime() <= t2.StartTime().getTime() && t1.EndTime().getTime() >= t2.EndTime().getTime();
	}

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
