package calendar;

import static org.junit.Assert.*;

import java.io.InvalidClassException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import hkust.cse.calendar.apptstorage.ApptStorageSQLImpl;
import hkust.cse.calendar.unit.Appt;
import hkust.cse.calendar.unit.Event;
import hkust.cse.calendar.unit.GroupEvent;
import hkust.cse.calendar.unit.Location;
import hkust.cse.calendar.unit.TimeSpan;
import hkust.cse.calendar.unit.User;
import hkust.cse.calendar.unit.Event.Frequency;

import org.junit.Test;

public class ApptSQLTests {
	
	private ApptStorageSQLImpl db;
	//TODO: eventually switch out dummyUser with the default User 
	private User dummyUser = new User(1, "pizzapi", "1234", true);
	
	public ApptSQLTests(){
		db = new ApptStorageSQLImpl();
	}
	
	@Test
	public void testGetAppt() {
		
		Timestamp t1 = new Timestamp(1600);
		Timestamp t2 = new Timestamp(600);
		Timestamp tFail = new Timestamp(700);
		
		Event good = (Event) db.getAppt(t1);
		Event badUser = (Event) db.getAppt(t2);
		
		Event badEvent = (Event) db.getAppt(tFail);
		
		assertNull(badUser);
		assertNull(badEvent);		
		
	}
	
	@Test
	public void testGetLocation(){
		Location good = db.getLocation(1);
		Location bad = db.getLocation(100);
		
		assert(good != null);
		assertNull(bad);
	}
	
	@Test
	public void testSaveAppt(){
		
		Timestamp t1 = new Timestamp(84400);
		Timestamp t2 = new Timestamp(89600);
		TimeSpan eventTime = new TimeSpan(t1, t2);
		
		Timestamp r1 = new Timestamp(81400);
		Timestamp r2 = new Timestamp(83400);
		TimeSpan eventReminder = new TimeSpan(r1, r2);
		
		String title = "New Private Event";
		String description = " Private vetn 1 description test adf adf adf ";
		String addDescription = "additional goes here . . ";
		int eventLocationID = 2; 
		Frequency f = Frequency.ONETIME;
		
		Event testEvent = new Event(eventTime, title, description, eventLocationID,
				eventReminder, addDescription, f);
		
		testEvent.setIsPublic(true);
		
//		 db.SaveAppt(testEvent);
		
		// WORKS !
		
	}
	
	@Test
	public void testGetAppts() {
		
		Timestamp t1 = new Timestamp(0);
		Timestamp t2 = new Timestamp(8600);
		TimeSpan time = new TimeSpan(t1,t2);
		
		Event[] appts = (Event[]) db.RetrieveAppts(time);
		
		System.out.println("RETRIEVED");
		System.out.println(appts.length);
		
		for(int i = 0; i < appts.length; ++i){
			System.out.println("RETRIEVED");
			System.out.println(appts[i].toString());
		}
		
		assertTrue(appts.length == 1);
		
	}
	
	@Test
	public void testGetApptsWithID() {
		
		Timestamp t1 = new Timestamp(0);
		Timestamp t2 = new Timestamp(8600);
		TimeSpan time = new TimeSpan(t1,t2);
		
		Event[] appts = (Event[]) db.RetrieveAppts(dummyUser, time);
		
		System.out.println("RETRIEVED");
		System.out.println(appts.length);
		
		for(int i = 0; i < appts.length; ++i){
			System.out.println("RETRIEVED");
			System.out.println(appts[i].toString());
		}
		
		assertTrue(appts.length == 1);
		
	}
	
	@Test
	public void testUpdateEvent() {
		
		Timestamp t1 = new Timestamp(14400);
		Timestamp t2 = new Timestamp(19600);
		TimeSpan eventTime = new TimeSpan(t1, t2);
		
		Timestamp r1 = new Timestamp(11400);
		Timestamp r2 = new Timestamp(13400);
		TimeSpan eventReminder = new TimeSpan(r1, r2);
		
		String title = "Brand NEW TITLE";
		String description = "ello this is a description test";
		String addDescription = "additional goes here . . ";
		int eventLocationID = 1; 
		Frequency f = Frequency.WEEKLY;
		
		Event testEvent = new Event(eventTime, title, description, eventLocationID,
				eventReminder, addDescription, f);
		
		testEvent.setID(13);
		
//		db.UpdateAppt(testEvent);
		
		// WORKS ! 
		
	}
	
	@Test
	public void testDeleteEvent() {
		Timestamp t1 = new Timestamp(14400);
		Timestamp t2 = new Timestamp(19600);
		TimeSpan eventTime = new TimeSpan(t1, t2);
		
		Timestamp r1 = new Timestamp(11400);
		Timestamp r2 = new Timestamp(13400);
		TimeSpan eventReminder = new TimeSpan(r1, r2);
		
		String title = "Brand NEW TITLE";
		String description = "ello this is a description test";
		String addDescription = "additional goes here . . ";
		int eventLocationID = 1; 
		Frequency f = Frequency.WEEKLY;
		
		Event testEvent = new Event(eventTime, title, description, eventLocationID,
				eventReminder, addDescription, f);
		
		testEvent.setID(13);
		
//		db.RemoveAppt(testEvent);
		
		// WORKS ! 
	}
	
	@Test 
	public void testGetEventWithID(){
		
		Event good = (Event) db.getAppt(14);
		Event bad1 = (Event) db.getAppt(1);
		Event bad2 = (Event) db.getAppt(2);
		
		System.out.println("TEST EVENT W/ ID");
		good.toString();
		
		assertNull(bad1);
		assertNull(bad2);
	}
	
	@Test
	public void testGetPublicEventsFromUser() {
		
		List<Appt> appts = db.getUserPublicEvents(dummyUser);
		
		System.out.println("RETRIEVED Public");
		System.out.println(appts.size());
		
		for(int i = 0; i < appts.size(); ++i){
			System.out.println("RETRIEVED");
			System.out.println(appts.get(i).toString());
		}
		
	}
	
	@Test
	public void testCreateGroupEvent() {
		Timestamp t1 = new Timestamp(184400);
		Timestamp t2 = new Timestamp(189600);
		TimeSpan eventTime = new TimeSpan(t1, t2);
		
		Timestamp r1 = new Timestamp(181400);
		Timestamp r2 = new Timestamp(183400);
		TimeSpan eventReminder = new TimeSpan(r1, r2);
		
		String title = "First Group Event";
		String description = " Group event Description ";
		String addDescription = "additional goes here . . ";
		int eventLocationID = 2; 
		Frequency f = Frequency.ONETIME;
		
		GroupEvent testGroupEvent = new GroupEvent(eventTime, title, description, eventLocationID,
				eventReminder, addDescription, f);
		
		// set the group specific parameters
		testGroupEvent.setIsPublic(true);
		testGroupEvent.setIsGroup(true);
		testGroupEvent.setConfirmed(false);
		testGroupEvent.setApproved(false);
		
		List<Integer> testUsers = new ArrayList<Integer>();
		testUsers.add(4);
		testUsers.add(5);
		testUsers.add(6);
		
//		// save the dummy group event
//		try {
//			db.createGroupEvent(testUsers, testGroupEvent);
//			System.out.println("GROUP EVENT SAVED");
//		} catch (InvalidClassException e) {
//			e.printStackTrace();
//		}
		
		// WORKED ! 
		
	}
	
	@Test
	public void testGetGroupEventsForUser() {
		List<GroupEvent> groupEvents = db.getGroupEvents(6);
		
		for(int i = 0; i < groupEvents.size(); ++i){
			System.out.println("GOT THE GROUP EVENT !!!");
			System.out.println(groupEvents.get(i).toString());
		}
		
		// WORKED !
	}
	
	@Test
	public void testGetGroupEvent() {
		GroupEvent testEvent = db.getGroupEvent(25);
		GroupEvent testBadEvent = db.getGroupEvent(1);
		
		System.out.println("RECIEVED GROUP ITEM 25");
		testEvent.toString();
		
		assertTrue(testEvent.getEventID() == 25);
		assertNull(testBadEvent);
		
		// WORKS !
	}
	
	@Test
	public void testUserLogin() {
		assertTrue(db.logInUser("alecrobins", "1"));
		assertFalse(db.logInUser("alecrobins", "12"));
		assertFalse(db.logInUser("notauser", "aa"));
	}

}
