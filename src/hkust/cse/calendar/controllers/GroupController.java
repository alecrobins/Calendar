package hkust.cse.calendar.controllers;

import hkust.cse.calendar.apptstorage.ApptStorageSQLImpl;
import hkust.cse.calendar.controllers.EventController.EventReturnMessage;
import hkust.cse.calendar.gui.CalGrid;
import hkust.cse.calendar.gui.MultipleUserSchedule;
import hkust.cse.calendar.unit.Appt;
import hkust.cse.calendar.unit.Event;
import hkust.cse.calendar.unit.Appt.Frequency;
import hkust.cse.calendar.unit.GroupEvent;
import hkust.cse.calendar.unit.Location;
import hkust.cse.calendar.unit.TimeSpan;
import hkust.cse.calendar.unit.User;

import java.io.InvalidClassException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JComboBox;
import javax.swing.JLabel;


public class GroupController {

	private CalGrid cal;
	private ApptStorageSQLImpl db; 

	public GroupController(CalGrid _cal) {
		cal = _cal;
		db = new ApptStorageSQLImpl(cal.getCurrUser());
	}

	//	public HashMap<User, List<Appt>> getUserMap(){
	//		
	//	}

	public EventReturnMessage createGroupEvent(
			String _detailArea, String _titleField,
			String _reminderTimeH, String _reminderTimeM,
			String _reminderYear, String _reminderMonth, String _reminderDay,
			String _frequency, String _location, CalGrid parentGrid, List<User> userList,
			List<TimeSpan> tList){

		// check if required fields were met
		if( _frequency == null)
			return EventReturnMessage.ERROR_UNFILLED_REQUIRED_FIELDS;

		TimeSpan reminder = null;
		// Create the reminder if reminder isn't null
		if(_reminderYear != null && _reminderMonth != null && _reminderDay != null
				&& _reminderTimeH != null && _reminderTimeM != null){
			Date noteTime = formatTime(_reminderYear, _reminderMonth, _reminderDay, _reminderTimeH, _reminderTimeM);
			System.out.println("Y:" + _reminderYear + " M:" +_reminderMonth + " D:" +_reminderDay + " H:" +_reminderTimeH + " Min:" +_reminderTimeM);
			// check that reminder is before the start time

		}

		// create the frequency
		Frequency frequency = Frequency.valueOf(_frequency);

		Location location = null;
		// create the location if not null
		if(_location != null)
			location = new Location(_location);

		GroupEvent event = new GroupEvent(_titleField, _detailArea, location.getLocationID(),
				reminder, "", frequency, tList);
		
		List<Integer> userIDs = new ArrayList<Integer>();
		for(int j = 0; j < userList.size(); ++j){
			userIDs.add(userList.get(j).getID());
		}
		
		int groupID;
		
		try {
			event.setIsGroup(true);
			groupID = db.createGroupEvent(userIDs, event);
			event.setID(groupID);
			db.createPurposedGroupEvent(event, tList, userList);
		
		} catch (InvalidClassException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if (event.getID() != -1){
			return EventReturnMessage.SUCCESS;
		}
		
		// save event to database

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
