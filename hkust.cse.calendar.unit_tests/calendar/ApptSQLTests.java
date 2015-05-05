package calendar;

import static org.junit.Assert.*;

import java.io.InvalidClassException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import hkust.cse.calendar.apptstorage.ApptStorageSQLImpl;
import hkust.cse.calendar.unit.Appt;
import hkust.cse.calendar.unit.Event;
import hkust.cse.calendar.unit.GroupEvent;
import hkust.cse.calendar.unit.Location;
import hkust.cse.calendar.unit.TimeSpan;
import hkust.cse.calendar.unit.User;
import hkust.cse.calendar.unit.Appt.Frequency;

import org.junit.Test;

public class ApptSQLTests {
	
	private ApptStorageSQLImpl db;
	//TODO: eventually switch out dummyUser with the default User 
	private User dummyUser = new User(4, "alecrobins", "1", true);
	
	public ApptSQLTests(){
		db = new ApptStorageSQLImpl();
	}
	
//	@Test
//	public void testGetAppt() {
//		
//		Timestamp t1 = new Timestamp(1600);
//		Timestamp t2 = new Timestamp(600);
//		Timestamp tFail = new Timestamp(700);
//		
//		Event good = (Event) db.getAppt(t1);
//		Event badUser = (Event) db.getAppt(t2);
//		
//		Event badEvent = (Event) db.getAppt(tFail);
//		
//		assertNull(badUser);
//		assertNull(badEvent);		
//		
//	}
//	
//	@Test
//	public void testGetLocation(){
//		Location good = db.getLocation(1);
//		Location bad = db.getLocation(100);
//		
//		assert(good != null);
//		assertNull(bad);
//	}
//	
//	@Test
//	public void testSaveAppt(){
//		
//		Timestamp t1 = new Timestamp(84400);
//		Timestamp t2 = new Timestamp(89600);
//		TimeSpan eventTime = new TimeSpan(t1, t2);
//		
//		Timestamp r1 = new Timestamp(81400);
//		Timestamp r2 = new Timestamp(83400);
//		TimeSpan eventReminder = new TimeSpan(r1, r2);
//		
//		String title = "New Private Event";
//		String description = " Private vetn 1 description test adf adf adf ";
//		String addDescription = "additional goes here . . ";
//		int eventLocationID = 2; 
//		Frequency f = Frequency.ONETIME;
//		
//		Event testEvent = new Event(eventTime, title, description, eventLocationID,
//				eventReminder, addDescription, f);
//		
//		testEvent.setIsPublic(true);
//		
////		db.SaveAppt(testEvent);
//		
//		// WORKS !
//		
//	}
//	
//	@Test
//	public void testGetAppts() {
//		
//		Timestamp t1 = new Timestamp(0);
//		Timestamp t2 = new Timestamp(8600);
//		TimeSpan time = new TimeSpan(t1,t2);
//		
//		Event[] appts = (Event[]) db.RetrieveAppts(time);
//		
//		System.out.println("RETRIEVED");
//		System.out.println(appts.length);
//		
//		for(int i = 0; i < appts.length; ++i){
//			System.out.println("RETRIEVED");
//			System.out.println(appts[i].toString());
//		}
//		
//		assertTrue(appts.length == 1);
//		
//	}
//	
//	@Test
//	public void testGetApptsWithID() {
//		
//		Timestamp t1 = new Timestamp(0);
//		Timestamp t2 = new Timestamp(8600);
//		TimeSpan time = new TimeSpan(t1,t2);
//		
//		Appt[] appts = db.RetrieveAppts(dummyUser, time);
//		
//		System.out.println("RETRIEVED");
//		System.out.println(appts.length);
//		
//		for(int i = 0; i < appts.length; ++i){
//			System.out.println("RETRIEVED");
//			System.out.println(appts[i].toString());
//		}
//		
//		assertTrue(appts.length == 1);
//		
//	}
//	
//	@Test
//	public void testUpdateEvent() {
//		
//		Timestamp t1 = new Timestamp(14400);
//		Timestamp t2 = new Timestamp(19600);
//		TimeSpan eventTime = new TimeSpan(t1, t2);
//		
//		Timestamp r1 = new Timestamp(11400);
//		Timestamp r2 = new Timestamp(13400);
//		TimeSpan eventReminder = new TimeSpan(r1, r2);
//		
//		String title = "Brand NEW TITLE";
//		String description = "ello this is a description test";
//		String addDescription = "additional goes here . . ";
//		int eventLocationID = 1; 
//		Frequency f = Frequency.WEEKLY;
//		
//		Event testEvent = new Event(eventTime, title, description, eventLocationID,
//				eventReminder, addDescription, f);
//		
//		testEvent.setID(13);
//		
////		db.UpdateAppt(testEvent);
//		
//		// WORKS ! 
//		
//	}
//	
//	@Test
//	public void testDeleteEvent() {
//		Timestamp t1 = new Timestamp(14400);
//		Timestamp t2 = new Timestamp(19600);
//		TimeSpan eventTime = new TimeSpan(t1, t2);
//		
//		Timestamp r1 = new Timestamp(11400);
//		Timestamp r2 = new Timestamp(13400);
//		TimeSpan eventReminder = new TimeSpan(r1, r2);
//		
//		String title = "Brand NEW TITLE";
//		String description = "ello this is a description test";
//		String addDescription = "additional goes here . . ";
//		int eventLocationID = 1; 
//		Frequency f = Frequency.WEEKLY;
//		
//		Event testEvent = new Event(eventTime, title, description, eventLocationID,
//				eventReminder, addDescription, f);
//		
//		testEvent.setID(13);
//		
////		db.RemoveAppt(testEvent);
//		
//		// WORKS ! 
//	}
//	
//	@Test 
//	public void testGetEventWithID(){
//		
//		Appt good = db.getAppt(14);
//		Appt bad1 = db.getAppt(1);
//		Appt bad2 = db.getAppt(2);
//		
//		System.out.println("TEST EVENT W/ ID");
//		good.toString();
//		
//		assertNull(bad1);
//		assertNull(bad2);
//	}
//	
//	@Test
//	public void testGetPublicEventsFromUser() {
//		
//		List<Appt> appts = db.getUserPublicEvents(dummyUser);
//		
//		System.out.println("RETRIEVED Public");
//		System.out.println(appts.size());
//		
//		for(int i = 0; i < appts.size(); ++i){
//			System.out.println("RETRIEVED");
//			System.out.println(appts.get(i).toString());
//		}
//		
//	}
//	
//	@Test
//	public void testCreateGroupEvent() {
//		Timestamp t1 = new Timestamp(4284400);
//		Timestamp t2 = new Timestamp(4289600);
//		TimeSpan eventTime = new TimeSpan(t1, t2);
//		
//		Timestamp r1 = new Timestamp(4281400);
//		Timestamp r2 = new Timestamp(4283400);
//		TimeSpan eventReminder = new TimeSpan(r1, r2);
//		
//		String title = "Third new Group Event";
//		String description = " Second Group event Description ";
//		String addDescription = "additional goes here . . ";
//		int eventLocationID = 3; 
//		Frequency f = Frequency.ONETIME;
//		
//		GroupEvent testGroupEvent = new GroupEvent(eventTime, title, description, eventLocationID,
//				eventReminder, addDescription, f);
//		
//		// set the group specific parameters
//		testGroupEvent.setIsPublic(true);
//		testGroupEvent.setIsGroup(true);
//		testGroupEvent.setConfirmed(false);
//		testGroupEvent.setApproved(false);
//		
//		List<Integer> testUsers = new ArrayList<Integer>();
//		testUsers.add(5);
//		testUsers.add(6);
//		testUsers.add(7);
////		
//		// save the dummy group event
//		try {
//			db.createGroupEvent(testUsers, testGroupEvent);
//			System.out.println("GROUP EVENT SAVED");
//		} catch (InvalidClassException e) {
//			e.printStackTrace();
//		}
//		
//		// WORKED ! 
//		
//	}
//	
//	@Test
//	public void testGetGroupEventsForUser() {
//		List<GroupEvent> groupEvents = db.getGroupEvents(6);
//		
//		for(int i = 0; i < groupEvents.size(); ++i){
//			System.out.println("GOT THE GROUP EVENT !!!");
//			System.out.println(groupEvents.get(i).toString());
//		}
//		
//		// WORKED !
//	}
//	
//	@Test
//	public void testGetGroupEvent() {
//		GroupEvent testEvent = db.getGroupEvent(31);
//		GroupEvent testBadEvent = db.getGroupEvent(1);
//		
//		System.out.println("RECIEVED GROUP ITEM 25");
//		testEvent.toString();
//		
//		assertTrue(testEvent.getEventID() == 31);
//		assertNull(testBadEvent);
//		
//		// WORKS !
//	}
//	
//	@Test
//	public void testUserLogin() {
//		assertTrue(db.logInUser("alecrobins", "1"));
//		assertFalse(db.logInUser("alecrobins", "12"));
//		assertFalse(db.logInUser("notauser", "aa"));
//	}
//	
//	@Test
//	public void testIsGroupEventValidated() {
//		assertTrue(db.isGroupEventValidated(25));
//	}
//	
//	@Test
//	public void testApproveGroupEvent(){
//		
//		db.approveGroupEvent(25, 4);
//		db.approveGroupEvent(25, 5);
//		db.approveGroupEvent(25, 6);
//		
//		// Confirmed worked
//	}
//	
//	@Test
//	public void testCreateUser(){
//		User testUser = new User("alekslars", "pass123", "Aleks", "Larsen", "alars@yahoo.com", false);
//		
////		int userID = db.createUser(testUser);
////		testUser.setID(userID);
//		
////		assertTrue(testUser.getID() != -1);
//		
//		// WORKS !
//	}
//	
//	@Test
//	public void testDeleteUser() {
////		db.deleteUser(8);
//		
//		// WORKS !
//	}
//	
//	@Test
//	public void testGetAllUsers () {
//		List<User> users = db.getListOfAllUsers();
//		System.out.println("GOT ALL THE USERS . . . . ");
//		
//		for(int i = 0; i < users.size(); ++i){
//			System.out.println(users.get(i).toString());
//		}
//		
//		// WORKS ! 
//	}
//	
//	@Test
//	public void testUserNameAvailibilty() {
//		assertFalse(db.isUserNameAvailable("alecrobins"));
//		assertTrue(db.isUserNameAvailable("brannewusername"));
//		
//		// WORKS ! 
//	}
//	
//	@Test
//	public void testGetAllUserEvents() {
//		List<Appt> events = db.getAllEvents(dummyUser.getID());
//		System.out.println();
//		System.out.println("Successfully got all the user events --------");
//		for(int i = 0; i < events.size(); ++i){
//			events.get(i).toString();
//		}
//		// WORKS ! 
//	}
//	
//	@Test
//	public void testGettingAllTheEventsForAListOfUsers() {
//		HashMap<Integer,List<Appt>> userToEvents = db.getUsersAppts();
//		
//		for (HashMap.Entry<Integer, List<Appt>> entry : userToEvents.entrySet()) {
//			int key = entry.getKey();
//			List<Appt> value = entry.getValue();
//			System.out.println("------------------------");
//			System.out.println(key);
//			System.out.println("~~~");
//			// print all the events
//			for(Appt event : value){
//				event.toString();
//			}
//			
//		}
//		
//		// WORKS ! 
//		
//	}
//	
//	@Test
//	public void testDeleteGroupEvent(){
////		db.deleteGroupEvent(32);
//		
//		// WORKED !
//	}
//	
//	@Test
//	public void testModifyUser(){
////		User testUser = new User("newUser3", "pass123", "user3first", "user3last", "user@3.com", false);
////		testUser.setID(3);
////		db.modifyUser(testUser);
//		
//		// WORKED !
//	}
//	
//	@Test
//	public void testCreateLocatoin() {
//		Location dummyLocation = new Location("a another new location", false);
////		int locationID = db.createLocation(dummyLocation);
////		System.out.println("location id: " + locationID);
////		dummyLocation.setLocationID(locationID);
//		
//		// WORKS !
//		
//	}
	
	@Test
	public void testGetAllLocation(){
		List<Location> locations = db.getAllLocations();
		
		for(int i = 0; i < locations.size(); ++i){
			locations.get(i).toString();
		}
		
	}

	
}
