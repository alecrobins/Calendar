

import static org.junit.Assert.*;

import java.io.InvalidClassException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import hkust.cse.calendar.apptstorage.ApptStorageSQLImpl;
import hkust.cse.calendar.unit.GroupEvent;
import hkust.cse.calendar.unit.GroupResponse;
import hkust.cse.calendar.unit.TimeSpan;
import hkust.cse.calendar.unit.User;
import hkust.cse.calendar.unit.Appt.Frequency;

import org.junit.Test;

public class GroupEventCreationTests {
	private ApptStorageSQLImpl db;

	private User dummyUser = new User(5, "sallySue", "123", true);
	
	public GroupEventCreationTests(){
		db = new ApptStorageSQLImpl(dummyUser);
	}
	
	
//	// EVERYTHING PASSED
//	
//	@Test
//	public void testCreatePurposedGroupEvent(){
//		
//		TimeSpan eventReminder = null;
//		
//		String title = "Testing the group event";
//		String description = " Second Group event Description ";
//		String addDescription = "additional goes here . . ";
//		int eventLocationID = 3; 
//		Frequency f = Frequency.ONETIME;
//		
//		GroupEvent testGroupEvent = new GroupEvent(title, description, eventLocationID,
//				eventReminder, addDescription, f);
//		
//		// set the group specific parameters
//		testGroupEvent.setIsPublic(true);
//		testGroupEvent.setIsGroup(true);
//		testGroupEvent.setConfirmed(false);
//		testGroupEvent.setApproved(false);
//		
//		User user1 = new User(5, "joesmith", "123", false);
//		User user2 = new User(6, "sallySue", "123", false);
//		User user3 = new User(7, "alekslars", "pass123", false);
//		
//		List<User> _users = new ArrayList<User>();
//		_users.add(user1);
//		_users.add(user2);
//		_users.add(user3);
//		
//		List<Integer> _usersIDs = new ArrayList<Integer>();
//		_usersIDs.add(user1.getID());
//		_usersIDs.add(user2.getID());
//		_usersIDs.add(user3.getID());
//
//		
//		List<TimeSpan> _timeSlots = new ArrayList<TimeSpan>();
//		
//		Timestamp r1 = new Timestamp(4281400);
//		Timestamp r2 = new Timestamp(4283400);
//		TimeSpan slot1 = new TimeSpan(r1, r2);
//		
//		Timestamp r3 = new Timestamp(3181400);
//		Timestamp r4 = new Timestamp(3183400);
//		TimeSpan slot2 = new TimeSpan(r3, r4);
//		
//		Timestamp r5 = new Timestamp(2081400);
//		Timestamp r6 = new Timestamp(2083400);
//		TimeSpan slot3 = new TimeSpan(r5, r6);
//		
//		_timeSlots.add(slot1);
//		_timeSlots.add(slot2);
//		_timeSlots.add(slot3);
//		
//		assert(testGroupEvent != null);
//		assert(testGroupEvent.getTitle() == "Testing the group event");
//		assert(_users != null);
//		assert(_timeSlots != null);
//		
//		System.out.println("Here we go");
////		
////		try {
////			int groupID = db.createGroupEvent(_usersIDs, testGroupEvent);
////			testGroupEvent.setID(groupID);
////			db.createPurposedGroupEvent(testGroupEvent, _timeSlots, _users);
////
////		} catch (InvalidClassException e) {
////			// TODO Auto-generated catch block
////			e.printStackTrace();
////		}
//		
//		System.out.println("Success");
//	}
//	
//	@Test
//	public void testGetPurposedGroupEventTimeSlots(){
//		User user2 = new User(6, "sallySue", "123", false);
//		System.out.println("Starting retrival . . .");
//		List<GroupResponse> responses = db.getPurposedGroupEventTimeSlots(user2);
//		System.out.println("RETRIEVED");
//		
//	}
	
//	@Test
//	public void testRespondToPurposedGroupEventTimeSlots(){
//		int groupID = 4;
//		int intiatorID = 4;
//		List<TimeSpan> _timeSlots = new ArrayList<TimeSpan>();
//		
//		Timestamp r1 = new Timestamp(4281400);
//		Timestamp r2 = new Timestamp(4283400);
//		TimeSpan slot1 = new TimeSpan(r1, r2);
//		
//		Timestamp r3 = new Timestamp(3181400);
//		Timestamp r4 = new Timestamp(3183400);
//		TimeSpan slot2 = new TimeSpan(r3, r4);
//		
//		
//		_timeSlots.add(slot1);
//		_timeSlots.add(slot2);
//		
//		// approve the first two slots
//		
//		db.respondToPurposedGroupEventTimeSlots(groupID, intiatorID, _timeSlots);
//		
//	}
	
	@Test
	public void testCancelPurposedGroupEventTimeSlots(){
		
		db.cancelPurposedGroupEventTimeSlots(4);
		
	}

}
