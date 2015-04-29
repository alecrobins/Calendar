package hkust.cse.calendar.apptstorage;

import hkust.cse.calendar.unit.Appt;
import hkust.cse.calendar.unit.Event;
import hkust.cse.calendar.unit.Location;
import hkust.cse.calendar.unit.TimeSpan;
import hkust.cse.calendar.unit.User;
import hkust.cse.calendar.unit.Event.Frequency;

import java.lang.reflect.Array;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;

public class ApptStorageSQLImpl extends ApptStorage {
	
	private int interval = 60;
	private User defaultUser = null;
	
	public ApptStorageSQLImpl( User user )
	{
		defaultUser = user;
	}
	
	public ApptStorageSQLImpl(){
		super();
	}
	

	@Override
	public Appt getAppt(Timestamp t) {
		
		Connection c = null;
	    Statement stmt = null;
	    try {
	      Class.forName("org.sqlite.JDBC");
	      c = DriverManager.getConnection("jdbc:sqlite:calendar.db");
	      c.setAutoCommit(false);
	      System.out.println("Opened database successfully");
	      
	      stmt = c.createStatement();
	      String sql = "UPDATE USER set USERNAME = 'pizzapi', EMAIL = 'pizza@pi.com' where ID=1;";
	      stmt.executeUpdate(sql);
	      
	      sql = "INSERT INTO USER (ID,USERNAME,PASSWORD,FIRST_NAME,LAST_NAME,EMAIL,ISADMIN) " +
                  "VALUES (null, 'thereal', 'passtheowrld', 'real', 'name', 'a@example.com', 'true' );"; 
	      stmt.executeUpdate(sql);
	      
	      c.commit();

	      
	      ResultSet rs = stmt.executeQuery( "SELECT * FROM USER;" );
	      while ( rs.next() ) {
	         int id = rs.getInt("id");
	         String  username = rs.getString("username");
	         String  email = rs.getString("email");
	         System.out.println( "ID = " + id );
	         System.out.println( "USERNAME = " + username );
	         System.out.println( "EMAIL = " + email );
	         System.out.println();
	      }
	      rs.close();
	      stmt.close();
	      c.close();
		    
	    } catch ( Exception e ) {
	      System.err.println( e.getClass().getName() + ": " + e.getMessage() );
	      System.exit(0);
	    }
	    System.out.println("Opened database successfully");
	    
		return null;
	}

	@Override
	public void SaveAppt(Appt appt) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Appt[] RetrieveAppts(TimeSpan d) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Appt[] RetrieveAppts(User entity, TimeSpan time) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Appt RetrieveAppts(int joinApptID) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void UpdateAppt(Appt appt) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void RemoveAppt(Appt appt) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public User getDefaultUser() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void LoadApptFromXml() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isApptValid(Appt appt) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void addNotification(TimeSpan ts) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void deleteNotification(TimeSpan ts) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mergeNotification() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public TimeSpan getNotification() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isNotificationEmpty() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Appt getAppt(int id) {
		// TODO Auto-generated method stub
		return null;
	}
	
	// get all the events for a user give teh userID
	public List<Appt> getUserPublicEvents(User u){
		return null;
	}
	
	// TODO: make a GroupEvent that implements event to include
	// who is in the group
	// get the group event informatin
	public Appt getGroupEvent(int groupID){
		return null;
	}
	
	// create a group event with the given users and event
	public boolean createGroupEvent(List<User> users, Appt event){
		return false;
	}
	
	//check to see if every user validated the group event
	public boolean isGroupEventValidated(int groupID){
		return false;
	}
	
	// validate the group event for the given user
	public boolean validateGroupEvent(int groupID){
		return false;
	}
	
	// checks the validity of an event against the db
	public boolean isEventValid(Appt event){
		return false;
	}
	
	//checks the validity of the event against all users schedules
	public boolean isGroupEventValid(List<User> users, int groupID){
		return false;
	}
	
	// return the user calendars for all listed users
	public HashMap<User,Appt> getUsersAppts(List<User> users){
		return null;
	}
	
	// modify the event with a new event
	public boolean modifyEvent (Appt newEvent, int eventID){
		return false;
	}
	
	// modify the group with a new group event and users
	public boolean modifyEvent (Appt newGroupEvent, List<User> users, int groupID){
		return false; 
	}
	
	//delete an event
	public boolean deleteEvent(int eventID){
		return false;
	}
	
	// delete a group event
	public boolean deleteGroupEvent (int groupID){
		return false;
	}
	
	// delete a user (need to check if the current user is an admin)
	public boolean deleteUser(User user){
		return false;
	}
	
	// modify the user settings by replacing the user stored
	// return newly created user
	public User modifyUser(User newUser){
		return null;
	}
	
	// modify other user setting as an admin
	// return newly created user
	public User modifyOtherUser(User newUser){
		return null;
	}
	
	// create a new location
	// return the new location
	public Location createLocation(Location location){
		return null;
	}
	
	// modify the locatoin (only can be done by the admin)
	// return the newly modified location
	public Location modifyLocation(Location location){
		return null;
	}
		
	// create an event given the information
	private Event createEvent(TimeSpan _eventTime, String _title, String _eventDescription, Location _eventLocation,
			TimeSpan _eventReminder, String _additionalEventDescription, Frequency _eventFrequency){
		
		return null;
	}
	
	// create a location
	private Location createLocation(String name){
		return null;
	}
	
	// create user
	private User createUser(String id, String pass){
		return null;
	}
	
	// checks the username / password of a user
	private boolean isUserValid(String username, String password){
		return false;
	}

}
