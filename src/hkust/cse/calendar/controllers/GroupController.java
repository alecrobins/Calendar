package hkust.cse.calendar.controllers;

import hkust.cse.calendar.apptstorage.ApptStorageSQLImpl;
import hkust.cse.calendar.controllers.EventController.EventReturnMessage;
import hkust.cse.calendar.gui.CalGrid;
import hkust.cse.calendar.gui.MultipleUserSchedule;
import hkust.cse.calendar.unit.Appt;
import hkust.cse.calendar.unit.Event;
import hkust.cse.calendar.unit.Appt.Frequency;
import hkust.cse.calendar.unit.Location;
import hkust.cse.calendar.unit.TimeSpan;
import hkust.cse.calendar.unit.User;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JComboBox;
import javax.swing.JLabel;


public class GroupController {
	private MultipleUserSchedule mus;
	
	private CalGrid cal;

	public GroupController(CalGrid _cal) {
		cal = _cal;
	}
	
//	public HashMap<User, List<Appt>> getUserMap(){
//		
//	}
	
	public TimeSpan suggestedGroupEventTime(List<TimeSpan> list){
		List<TimeSpan> suggested = new LinkedList<TimeSpan>();
		for (TimeSpan t: list){
			
		}
	}
	
	public EventReturnMessage createGroupEvent(
			String _year, String _month, String _day,
			String _sTimeH, String _sTimeM, String _eTimeH, String _eTimeM,
			String _detailArea, String _titleField,
			String _reminderTimeH, String _reminderTimeM,
			String _reminderYear, String _reminderMonth, String _reminderDay,
			String _frequency, String _location, CalGrid parentGrid, List<User> userList){
		
		
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
				if(_location != null)
					location = new Location(_location);
				
				
				return EventReturnMessage.ERROR;


	}
	public HashMap<User, List<Appt>> getUserMap(List<User> userList){
		HashMap<User, List<Appt>> userMap = new HashMap<User, List<Appt>>();
		for (User u: userList){
			ApptStorageSQLImpl asql = new ApptStorageSQLImpl(u);
			userMap.put(u, asql.getUserPublicEvents(u));
		}
		return userMap;
	}
	
	public void sendConfirmation(){
	}
	
	public boolean isConfirmed(){
		return false;
	}
	
	public boolean isGroupEventValid(Event e){
		return false;
	}
	
	//helper methods from event controller (update these)
	
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
