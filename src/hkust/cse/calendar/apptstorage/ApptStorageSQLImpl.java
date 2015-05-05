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
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class ApptStorageSQLImpl extends ApptStorage {
	
	private int interval = 60;
	private User defaultUser = null;
	
	//TODO: eventually switch out dummyUser with the default User 
	private User dummyUser = new User(1, "pizzapi", "1234", true);

	public ApptStorageSQLImpl( User user )
	{
		defaultUser = user;
	}
	
	public ApptStorageSQLImpl(){
		super();
	}

	@Override
	public Appt getAppt(Timestamp t) {
				
		long time = t.getTime();
		Connection c = null;
	    Statement stmt = null;
	    
	    // return the event
	    Event event = null;
	    
	    try {
	      Class.forName("org.sqlite.JDBC");
	      c = DriverManager.getConnection("jdbc:sqlite:calendar.db");
	      c.setAutoCommit(false);
	      System.out.println("Opened database successfully");
	      
	      stmt = c.createStatement();
	      String sql = 
	    		  "select distinct e.id as 'id', e.startTime as 'startTime', e.endTime as 'endTime', " +
	    		  "e.eventTitle as 'title', e.eventDescription as 'description', " +
	    		  "e.frequency as 'frequency', e.eventReminderStart as 'reminderStart', " +
	    		  "e.eventReminderEnd as 'reminderEnd', e.locationID as 'locationID', " +
	    		  "e.isGroup as 'isGroup', e.isPublic as 'isPublic' " +
	    		  "from event e, userEvent ue " +
	    		  "where ue.userID = "+ dummyUser.getID() +
	    		  " and e.id = ue.eventID and e.startTime = " +
	    		  time + ";";
	      
	      ResultSet rs = stmt.executeQuery( sql );
	      
	      // go through results
	      while ( rs.next() ) {
	    	 event = formatEvent(rs);
	         System.out.println(event.toString());
	      }
	      
	      rs.close();
	      stmt.close();
	      c.close();
		    
	    } catch ( Exception e1 ) {
	      System.err.println( e1.getClass().getName() + ": " + e1.getMessage() );
	      System.exit(0);
	    }
	    	    
		return event;
	}


	public Location getLocation(int locationID) {
		
		Connection c = null;
	    Statement stmt = null;
	    
	    // return the event
	    Location location = null;
	    
	    try {
	      Class.forName("org.sqlite.JDBC");
	      c = DriverManager.getConnection("jdbc:sqlite:calendar.db");
	      c.setAutoCommit(false);
	      System.out.println("Opened database successfully");
	      
	      stmt = c.createStatement();
	      String sql = "select id, name, isGroupFacility from location where id = " + locationID;
	      
	      ResultSet rs = stmt.executeQuery( sql );
	      
	      // go through results
	      while ( rs.next() ) {
	    	  location = formatLocation(rs);
	         System.out.println(location.toString());
	      }
	      
	      rs.close();
	      stmt.close();
	      c.close();
		    
	    } catch ( Exception e1 ) {
	      System.err.println( e1.getClass().getName() + ": " + e1.getMessage() );
	      System.exit(0);
	    }
	    	    
		return location;
	}
	
	@Override
	public void SaveAppt(Appt appt) {
		SaveAppt((Event) appt);
	}
	
	public void SaveAppt(Event _event) {
		Connection c = null;
	    Statement stmt = null;

	    try {
	      Class.forName("org.sqlite.JDBC");
	      c = DriverManager.getConnection("jdbc:sqlite:calendar.db");
	      c.setAutoCommit(false);
	      System.out.println("Opened database successfully");
	      
	      stmt = c.createStatement();
	      PreparedStatement query;
	      
	      query = c.prepareStatement("insert into event (id, startTime, endTime, eventTitle, eventDescription, eventReminderStart, eventReminderEnd, frequency, locationID, isGroup, isPublic) " +
		    		"values (null, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);");
	      
	      // assign variables
	      query.setTimestamp(1, _event.getEventTime().StartTime());
	      query.setTimestamp(2, _event.getEventTime().EndTime());
	      query.setString(3, _event.getTitle());
	      query.setString(4, _event.getEventDescription());
	      query.setTimestamp(5, _event.getEventReminder().StartTime());
	      query.setTimestamp(6, _event.getEventReminder().EndTime());
	      query.setInt(7, _event.getEventFrequency().getValue());
	      query.setInt(8, _event.getEventLocationID());
	      query.setBoolean(9, _event.getIsGroup());
	      query.setBoolean(10, _event.getIsPublic());
	      
	      boolean done = query.execute();
	      
	      // commit
	      c.commit();
	      
	      query.close();
	      stmt.close();
	      c.close();
		    
	    } catch ( Exception e1 ) {
	      System.err.println( e1.getClass().getName() + ": " + e1.getMessage() );
	      System.exit(0);
	    }
	}
	
	@Override
	public Appt[] RetrieveAppts(TimeSpan d) {
		return RetrieveAppts(dummyUser, d);
	}

	@Override
	public Appt[] RetrieveAppts(User entity, TimeSpan time) {
		Connection c = null;
	    Statement stmt = null;
	    
	    // Start and end time of time span
	    Timestamp start = time.StartTime();
	    Timestamp end = time.EndTime();
	    
	    List<Appt> events = new ArrayList<Appt>();
	    
	    try {
	      Class.forName("org.sqlite.JDBC");
	      c = DriverManager.getConnection("jdbc:sqlite:calendar.db");
	      c.setAutoCommit(false);
	      System.out.println("Opened database successfully");
	      
	      stmt = c.createStatement();
	      PreparedStatement query;
	      
	      query = c.prepareStatement( 
	    		  "select distinct e.id as 'id', e.startTime as 'startTime', e.endTime as 'endTime', " +
	    		  "e.eventTitle as 'title', e.eventDescription as 'description', " +
	    		  "e.frequency as 'frequency', e.eventReminderStart as 'reminderStart', " +
	    		  "e.eventReminderEnd as 'reminderEnd', e.locationID as 'locationID', " +
	    		  "e.isGroup as 'isGroup', e.isPublic as 'isPublic' " +
	    		  "from event e, userEvent ue " +
	    		  "where ue.userID = ? "+
	    		  "and e.id = ue.eventID and e.startTime >= ? and " +
	    		  "e.endTime <= ? ;");
	      
//	      ResultSet rs = stmt.executeQuery( sql );
	      query.setInt(1, entity.getID()); 
	      query.setTimestamp(2, start);
	      query.setTimestamp(3, end);
	      
	      ResultSet rs = query.executeQuery();
	      
	      // go through results
	      while ( rs.next() ) {
	    	  // return the event
	  	     Event event = null;
	    	 event = formatEvent(rs);
	    	 // put onto events
	    	 events.add(event);
	         System.out.println(event.toString());
	      }
	      
	      rs.close();
	      stmt.close();
	      c.close();
		    
	    } catch ( Exception e1 ) {
	      System.err.println( e1.getClass().getName() + ": " + e1.getMessage() );
	      System.exit(0);
	    }
	    	    
		return (Appt[]) events.toArray();
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
	public HashMap<User,List<Appt>> getUsersAppts(List<User> users){
		return null;
	}
	
	// return the user calendars for all listed users within a period
	public HashMap<User,List<Appt>> getUsersAppts(List<User> users, Timestamp start, Timestamp end){
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
	private User createUser(String username, String password, boolean isAdmin){
		return null;
	}
	
	// checks the username / password of a user
	private boolean isUserValid(String username, String password){
		return false;
	}
	
	// helper functions
	// =====================
	private Event formatEvent(ResultSet rs) throws SQLException {
		 // Gather data
  	  
   	 	int id = rs.getInt("id");
        Timestamp startTime = new Timestamp(rs.getInt("startTime"));
        Timestamp endTime = new Timestamp(rs.getInt("endTime") );
        String eventTitle = rs.getString("title");
        String  eventDescription = rs.getString("description");
        int frequencyNum = rs.getInt("frequency");
        Timestamp eventReminderStart = new Timestamp(rs.getInt("reminderStart"));
        Timestamp eventReminderEnd = new Timestamp(rs.getInt("reminderEnd") );
        int locationID = rs.getInt("locationID");
        boolean isGroup = rs.getBoolean("isGroup");
        boolean isPublic = rs.getBoolean("isPublic");
        
        // set the event
        TimeSpan eventTime = new TimeSpan(startTime, endTime);
        TimeSpan reminder = new TimeSpan(eventReminderStart, eventReminderEnd);
        Frequency frequency = Frequency.values()[frequencyNum];
        
        System.out.println(id);
        
        Event newEvent = new Event(eventTime, eventTitle, eventDescription, locationID, reminder, "", frequency);
        newEvent.setEventID(id);
        return newEvent;
	}
	
	private Location formatLocation(ResultSet rs) throws SQLException {
		int id = rs.getInt("id");
		String name = rs.getString("name");
		boolean isGroup = rs.getBoolean("isGroupFacility");
		return new Location(id, name, isGroup);
	}



}
