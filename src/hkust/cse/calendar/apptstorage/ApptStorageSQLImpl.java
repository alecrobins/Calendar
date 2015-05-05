package hkust.cse.calendar.apptstorage;

import hkust.cse.calendar.unit.Appt;
import hkust.cse.calendar.unit.Event;
import hkust.cse.calendar.unit.GroupEvent;
import hkust.cse.calendar.unit.Location;
import hkust.cse.calendar.unit.TimeSpan;
import hkust.cse.calendar.unit.User;
import hkust.cse.calendar.unit.Event.Frequency;

import java.io.InvalidClassException;
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
	private User dummyUser = new User(4, "alecrobins", "1", true);

	public ApptStorageSQLImpl( User user )
	{
		defaultUser = user;
	}
	
	public ApptStorageSQLImpl(){
		super();
	}
	
	// Functions to set the default User
	public void setDefaultUser(User u){
		defaultUser = u;
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
	
	// returns the appt id
	public int SaveAppt(Event _event) {
		Connection c = null;
	    Statement stmt = null;
	    
	    // set teh eventID
	    int eventID = -1;

	    try {
	      Class.forName("org.sqlite.JDBC");
	      c = DriverManager.getConnection("jdbc:sqlite:calendar.db");
	      c.setAutoCommit(false);
	      System.out.println("Opened database successfully");
	      
	      stmt = c.createStatement();
	      PreparedStatement query;
	      
	      query = c.prepareStatement("insert into event (id, startTime, endTime, eventTitle, eventDescription, eventReminderStart, eventReminderEnd, frequency, locationID, isGroup, isPublic) " +
		    		"values (null, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);", Statement.RETURN_GENERATED_KEYS);
	      
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
	      
		  // Get the generated key from the event creation
	      ResultSet rs = stmt.getGeneratedKeys();
		  rs.next();
		  eventID = rs.getInt(1);
		  
		  // create a connection between the user and event
		  query = c.prepareStatement("insert into userEvent (userID, eventID) values (?, ?)");
		  query.setInt(1, dummyUser.getID());
		  query.setInt(2, eventID);
		  
		  done = query.execute();
    	   
	      // commit
	      c.commit();
	      
	      query.close();
	      stmt.close();
	      c.close();
		    
	    } catch ( Exception e1 ) {
	      System.err.println( e1.getClass().getName() + ": " + e1.getMessage() );
	      System.exit(0);
	    }
	    
	    return eventID;
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

	// N/A
	@Override
	public Appt RetrieveAppts(int joinApptID) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void UpdateAppt(Appt appt) {
		Connection c = null;
	    Statement stmt = null;
	    
	    // cast the appt to an event
	    Event _event = (Event) appt;
	    
	    try {
	      Class.forName("org.sqlite.JDBC");
	      c = DriverManager.getConnection("jdbc:sqlite:calendar.db");
	      c.setAutoCommit(false);
	      System.out.println("Opened database successfully");
	      
	      stmt = c.createStatement();
	      PreparedStatement query;
	      
	      query = c.prepareStatement("update event set startTime = ?, endTime = ?, eventTitle = ?, "+
	    		  					 "eventDescription = ?, eventReminderStart = ?, eventReminderEnd = ?, " +
	    		  					 "frequency = ?, locationID = ?, isGroup = ?, isPublic = ? " +
	    		  					 "where id = ?");
	      
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
	      query.setInt(11, _event.getID());
	      
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
	public void RemoveAppt(Appt appt) {
		Connection c = null;
	    Statement stmt = null;
	    
	    // cast the appt to an event
	    Event _event = (Event) appt;
	    
	    try {
	      Class.forName("org.sqlite.JDBC");
	      c = DriverManager.getConnection("jdbc:sqlite:calendar.db");
	      c.setAutoCommit(false);
	      System.out.println("Opened database successfully");
	      
	      stmt = c.createStatement();
	      PreparedStatement query;
	      
	      query = c.prepareStatement("delete from event " +
	    		  					 "where id = ?");
	      
	      // assign variables
	      query.setInt(1, _event.getID());
	      
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
	public User getDefaultUser() {
		return defaultUser;
	}

	// N/A
	@Override
	public void LoadApptFromXml() {
		// TODO Auto-generated method stub
		
	}

	// N/A this is done in controller
	@Override
	public boolean isApptValid(Appt appt) {
		// TODO Auto-generated method stub
		return false;
	}
	
	// NOTIFICATIONS 
	// ===================
	
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

	// Get all the events for a user (this includes regular events & group events)
	public List<Appt> getAllEvents(int userID){
		List<GroupEvent> groupEvents = getGroupEvents(userID);
		List<Appt> regularEvents = getAllRegularEvents(userID);
		
		// combine the lists
		regularEvents.addAll(groupEvents);
		
		return regularEvents;
	}
	
	@Override
	public Appt getAppt(int id) {
		
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
	    		  " and e.id = ue.eventID and e.id = " +
	    		  id + ";";
	      
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
	
	public List<Appt> getAllRegularEvents(int userID){
		Connection c = null;
	    Statement stmt = null;
	    
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
	    		  "and e.id = ue.eventID;");
	      
	      query.setInt(1, userID); 
	      
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
	    
	    return events;
	    
	}
	
	// get all the events for a user give the userID
	public List<Appt> getUserPublicEvents(User u){
		Connection c = null;
	    Statement stmt = null;
	    
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
	    		  "and e.id = ue.eventID and e.isPublic = ?;");
	      
//	      ResultSet rs = stmt.executeQuery( sql );
	      query.setInt(1, u.getID()); 
	      query.setBoolean(2, true);
	      
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
	    
	    return events;
	    
	}
	
	// get all group event ids for the passed in user
	public List<Integer> getGroupEventIDs(int userID){
		Connection c = null;
	    Statement stmt = null;
	
	    List<Integer> groupIDs = new ArrayList<Integer>();
	    
	    try {
	      Class.forName("org.sqlite.JDBC");
	      c = DriverManager.getConnection("jdbc:sqlite:calendar.db");
	      c.setAutoCommit(false);
	      System.out.println("Opened database successfully");
	      
	      stmt = c.createStatement();
	      PreparedStatement query;
	      
	      query = c.prepareStatement( "select eventID from groupUser where userID = ?");
	      query.setInt(1, userID);
	      
	      ResultSet rs = query.executeQuery();
	      
	      // go through results
	      while ( rs.next() ) {
	    	 // put onto the event ids inot the list
	    	 groupIDs.add(rs.getInt("eventID"));
	      }
	      
	      rs.close();
	      stmt.close();
	      c.close();
		    
	    } catch ( Exception e1 ) {
	      System.err.println( e1.getClass().getName() + ": " + e1.getMessage() );
	      System.exit(0);
	    }
	    
	    return groupIDs;
	}
	
	// get the group event information for the given user
	public GroupEvent getGroupEvent(int groupID){
		Connection c = null;
	    Statement stmt = null;
	    
	    GroupEvent event = null;

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
	    		  "e.isGroup as 'isGroup', e.isPublic as 'isPublic', " +
	    		  "ge.userInitiator as 'initiator', ge.confirmed as 'confirmed' " +
	    		  "from event e, groupEvent ge " +
	    		  "where ge.eventID = e.id "+
	    		  "and ge.eventID = ?;");
	      
//	      ResultSet rs = stmt.executeQuery( sql );
	      query.setInt(1, groupID); 
	      
	      ResultSet rs = query.executeQuery();
	      
	      // go through results
	      while ( rs.next() ) {
	    	  // return the event
	    	 event = formatGroupEvent(rs);
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
	
	// gather all the group events for passed in user
	public List<GroupEvent> getGroupEvents(int user){
		List<GroupEvent> groupEvents = new ArrayList<GroupEvent>();
		// get the group ids of events that are related to the user
		List<Integer> groupIDs = getGroupEventIDs(user);
		
		// add all the events to the list
		for(int i = 0; i < groupIDs.size(); ++i){
			int groupID = groupIDs.get(i);
			GroupEvent groupEvent = getGroupEvent(groupID);
			// push the group event into the group
			groupEvents.add(groupEvent);
		}
		
		return groupEvents;
	}
	
	public List<Integer> getGroupUserIDs(int groupID){
		Connection c = null;
	    Statement stmt = null;
	    
	    List<Integer> userIDs = new ArrayList<Integer>();

	    try {
	      Class.forName("org.sqlite.JDBC");
	      c = DriverManager.getConnection("jdbc:sqlite:calendar.db");
	      c.setAutoCommit(false);
	      System.out.println("Opened database successfully");
	      
	      stmt = c.createStatement();
	      PreparedStatement query;
	      
	      query = c.prepareStatement( 
	    		  "select userID " +
	    		  "from groupUser ge " +
	    		  "where ge.eventID = ? ");
	      
	      query.setInt(1, groupID); 
	      
	      ResultSet rs = query.executeQuery();
	      
	      // go through results
	      while ( rs.next() ) {
	    	  // add the user IDS to teh list
	    	  userIDs.add(rs.getInt("userID"));
	      }
	      
	      rs.close();
	      stmt.close();
	      c.close();
		    
	    } catch ( Exception e1 ) {
	      System.err.println( e1.getClass().getName() + ": " + e1.getMessage() );
	      System.exit(0);
	    }
	    
	    return userIDs;
	};
	
	// create a group event with the given users and event
	public void createGroupEvent(List<Integer> users, Appt event) throws InvalidClassException{
		// assert that it is a group a event that is being created
		if(!(event instanceof GroupEvent))
			throw new InvalidClassException("You can only save GroupEvents not Regular events");
		
		GroupEvent groupEvent = new GroupEvent((Event) event );
		
		// save the event
		int groupID = SaveAppt(groupEvent);
		
		Connection c = null;
	    Statement stmt = null;

	    try {
	      Class.forName("org.sqlite.JDBC");
	      c = DriverManager.getConnection("jdbc:sqlite:calendar.db");
	      c.setAutoCommit(false);
	      System.out.println("Opened database successfully");
	      
	      stmt = c.createStatement();
	      PreparedStatement query;
	      
	      query = c.prepareStatement("insert into groupEvent (eventID, userInitiator, confirmed) " +
		    		"values (?, ?, ?);", Statement.RETURN_GENERATED_KEYS);
	      
	      // assign variables
	      // set the initiator to who ever the current user is
	      // the group is initially set to false
	      query.setInt(1, groupID);
	      query.setInt(2, dummyUser.getID());
	      query.setBoolean(3, groupEvent.isConfirmed());
	      
	      boolean done = query.execute();
	      
		  // create the connections for all the other users
	      for(int i = 0; i < users.size(); ++i){
	    	  query = c.prepareStatement("insert into groupUser (eventID, userID, approved) " +
	    			  					 "values (?, ?, ?);");

	    	  // set the groupID to the id of the event that was created
	    	  query.setInt(1, groupID);
			  // set the user id to the current index of users
	    	  query.setInt(2, users.get(i));
	    	  // default the approval to false -> not approved
	    	  query.setBoolean(3, false);
			  
			  done = query.execute();
	      }
		  
	      // commit
	      c.commit();
	      
	      query.close();
	      stmt.close();
	      c.close();
		    
	    } catch ( Exception e1 ) {
	      System.err.println( e1.getClass().getName() + ": " + e1.getMessage() );
	      System.exit(0);
	    }
		
		// create the group event part
	}
	
	// checks the username / password of a user
	// returns true if the user log in is correct
	public boolean logInUser(String username, String password){

		Connection c = null;
	    Statement stmt = null;
	    boolean success = false;
	    String userPassword = "";
	    try {
	      Class.forName("org.sqlite.JDBC");
	      c = DriverManager.getConnection("jdbc:sqlite:calendar.db");
	      c.setAutoCommit(false);
	      System.out.println("Opened database successfully");
	      
	      stmt = c.createStatement();
	      PreparedStatement query;
	      
	      query = c.prepareStatement("select password from user where username = ?;");
	      query.setString(1, username);
	      
	      
	      ResultSet rs = query.executeQuery();
	   // go through results
	      while ( rs.next() ) {
	    	  // add the user IDS to teh list
	    	  userPassword = rs.getString("password");
	      }
	      
	      // check that the password matches
	      if( !(userPassword == null || userPassword == "") && userPassword.equals(password) )
	    	  	success = true;
	      
	      query.close();
	      stmt.close();
	      c.close();
		    
	    } catch ( Exception e1 ) {
	      System.err.println( e1.getClass().getName() + ": " + e1.getMessage() );
	      System.exit(0);
	    }
	    
		return success;
	}
	
	// return a list of all the user
	public List<User> getListOfAllUsers() {
		Connection c = null;
	    Statement stmt = null;
	    
	    List<User> users = new ArrayList<User>();

	    try {
	      Class.forName("org.sqlite.JDBC");
	      c = DriverManager.getConnection("jdbc:sqlite:calendar.db");
	      c.setAutoCommit(false);
	      System.out.println("Opened database successfully");
	      
	      stmt = c.createStatement();
	      PreparedStatement query;
	      
	      query = c.prepareStatement( 
	    		  "select id, username, password, first_name, last_name, email, isAdmin " +
	    		  "from user");
	      
	      ResultSet rs = query.executeQuery();
	      
	      // go through results
	      while ( rs.next() ) {
	    	  // add the user
	    	  users.add(formatUser(rs));
	      }
	      
	      rs.close();
	      stmt.close();
	      c.close();
		    
	    } catch ( Exception e1 ) {
	      System.err.println( e1.getClass().getName() + ": " + e1.getMessage() );
	      System.exit(0);
	    }
	    
	    return users;
	}
	
	// this returns whether the user name is availiable
	public boolean isUserNameAvailable(String username){
		Connection c = null;
	    Statement stmt = null;
	    
	    boolean available = true;

	    try {
	      Class.forName("org.sqlite.JDBC");
	      c = DriverManager.getConnection("jdbc:sqlite:calendar.db");
	      c.setAutoCommit(false);
	      System.out.println("Opened database successfully");
	      
	      stmt = c.createStatement();
	      PreparedStatement query;
	      
	      query = c.prepareStatement( 
	    		  "select username " +
	    		  "from user where username = ?");
	      query.setString(1, username);
	      
	      ResultSet rs = query.executeQuery();
	      
	      // go through results
	      while ( rs.next() ) {
	    	  // add the user
	    	  String returnedName = rs.getString("username");
	    	  if(username != null || username != "")
	    		  available = false;
	      }
	      
	      rs.close();
	      stmt.close();
	      c.close();
		    
	    } catch ( Exception e1 ) {
	      System.err.println( e1.getClass().getName() + ": " + e1.getMessage() );
	      System.exit(0);
	    }
	    
	    return available;
	}
	
	// save a user
	// This function will return the userID
	public int createUser(User u){
		Connection c = null;
	    Statement stmt = null;
	    
	    // set teh eventID
	    int userID = -1;

	    try {
	      Class.forName("org.sqlite.JDBC");
	      c = DriverManager.getConnection("jdbc:sqlite:calendar.db");
	      c.setAutoCommit(false);
	      System.out.println("Opened database successfully");
	      
	      stmt = c.createStatement();
	      PreparedStatement query;
	      
	      query = c.prepareStatement("insert into user (id, username, password, first_name, last_name, email, isAdmin) " +
		    		"values (null, ?, ?, ?, ?, ?, ?);", Statement.RETURN_GENERATED_KEYS);
	      
	      // assign variables
	      query.setString(1, u.getUsername());
	      query.setString(2, u.Password());
	      query.setString(3, u.getFirstname());
	      query.setString(4, u.getLastname());
	      query.setString(5, u.getEmail());
	      query.setBoolean(6, u.isAdmin());
	      
	      boolean done = query.execute();
	      
		  // Get the generated key from the event creation
	      ResultSet rs = stmt.getGeneratedKeys();
		  rs.next();
		  userID = rs.getInt(1);
		 
		  done = query.execute();
    	   
	      // commit
	      c.commit();
	      
	      query.close();
	      stmt.close();
	      c.close();
		    
	    } catch ( Exception e1 ) {
	      System.err.println( e1.getClass().getName() + ": " + e1.getMessage() );
	      System.exit(0);
	    }
	    
	    return userID;
	}
	
	// delete the user with the given userID
	public void deleteUser(int userID){
		
		Connection c = null;
	    Statement stmt = null;

	    try {
	      Class.forName("org.sqlite.JDBC");
	      c = DriverManager.getConnection("jdbc:sqlite:calendar.db");
	      c.setAutoCommit(false);
	      System.out.println("Opened database successfully");
	      
	      stmt = c.createStatement();
	      PreparedStatement query;
	      
	      query = c.prepareStatement("delete from user " +
		    		"where id = ?");
	      
	      query.setInt(1, userID);
	      
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
	
	//check to see if every user validated the group event
	// NOTE IF the group is validated then group will be noted as validated in teh DB
	public boolean isGroupEventValidated(int groupID){

		List<Integer> users = getGroupUserIDs(groupID);
		boolean confirmed = true;
		
		Connection c = null;
	    Statement stmt = null;
	    
		for(int i = 0; i < users.size(); ++i){
			
			try {
			      Class.forName("org.sqlite.JDBC");
			      c = DriverManager.getConnection("jdbc:sqlite:calendar.db");
			      c.setAutoCommit(false);
			      System.out.println("Opened database successfully");
			      
			      stmt = c.createStatement();
			      PreparedStatement query;
			      
			      query = c.prepareStatement("select approved from groupUser where userID = ? and eventID = ?;");
			      query.setInt(1, users.get(i));
			      query.setInt(2, groupID);
			      
			      
			      ResultSet rs = query.executeQuery();
			      // go through results
			      while ( rs.next() ) {
			    	  // add the user IDS to teh list
			    	  boolean isApproved = rs.getBoolean("approved");
			    	  // if the user(i) did not approve then confirmed is false
			    	  // then break out of loop.
			    	  if(!isApproved){
			    		  confirmed = false;
			    		  break;
			    	  }
			      }
			      
			      query.close();
			      stmt.close();
			      c.close();
				    
			    } catch ( Exception e1 ) {
			      System.err.println( e1.getClass().getName() + ": " + e1.getMessage() );
			      System.exit(0);
			    }
			      
		}
		// If the user exits the loop w/o changing the cofirmed value then the event is confirmed
		
		if(confirmed)
			confirmGroupEvent(groupID);
		
		return confirmed;
	}
	
	// confirm the given group event
	public void confirmGroupEvent(int groupID){
		Connection c = null;
	    Statement stmt = null;
		
		try {
		      Class.forName("org.sqlite.JDBC");
		      c = DriverManager.getConnection("jdbc:sqlite:calendar.db");
		      c.setAutoCommit(false);
		      System.out.println("Opened database successfully");
		      
		      stmt = c.createStatement();
		      PreparedStatement query;
		      
		      query = c.prepareStatement("update groupEvent set confirmed = ? "+
		    		  					 "where eventID = ?");
		      // approve the appropiate event
		      query.setBoolean(1, true);
		      query.setInt(2, groupID);
		      
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
	
	// validate the group event for the passed in user
	public void approveGroupEvent(int groupID, int userID){
		Connection c = null;
	    Statement stmt = null;
		
		try {
		      Class.forName("org.sqlite.JDBC");
		      c = DriverManager.getConnection("jdbc:sqlite:calendar.db");
		      c.setAutoCommit(false);
		      System.out.println("Opened database successfully");
		      
		      stmt = c.createStatement();
		      PreparedStatement query;
		      
		      query = c.prepareStatement("update groupUser set approved = ? "+
		    		  					 "where eventID = ? and userID = ?");
		      // approve the appropiate event
		      query.setBoolean(1, true);
		      query.setInt(2, groupID);
		      query.setInt(3, userID);
		      
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
	
	// N/A -> needs to be done in the controller
	// checks the validity of an event against the db
	public boolean isEventValid(Appt event){
		return false;
	}
	
	// N/A -> need to be done in the controller
	//checks the validity of the event against all users schedules
	public boolean isGroupEventValid(List<User> users, int groupID){
		return false;
	}
	
	
	// return the user calendars for all listed users
	// NOTE: This will not include private events from other users
	// (userId) -> List<Appt>
	public HashMap<Integer,List<Appt>> getUsersAppts(){
		// get all the events for all the users
		return getUsersAppts(getListOfAllUsers());
	}
	
	public HashMap<Integer,List<Appt>> getUsersAppts(List<User> _users){
		HashMap<Integer, List<Appt>> userToEvents = new HashMap<Integer, List<Appt>>();
		List<User> users = _users;
		
		// iterate through all the users and gather all their events
		for(int i = 0; i < users.size(); ++i){
			int userID = users.get(i).getID();
			userToEvents.put(userID, getAllRegularEvents(userID));
		}
			
		return userToEvents;
	}
	
	// N/A - to be controlled by the controller
	// return the user calendars for all listed users within a period
	public HashMap<User,List<Appt>> getUsersAppts(List<User> users, Timestamp start, Timestamp end){
		return null;
	}
	
	// modify the group with a new group event and users
	// NOTE: that only the intiator of admin
	public void modifyGroupEvent (Appt newGroupEvent, int groupID){
		Connection c = null;
	    Statement stmt = null;
	    
	    // cast the appt to an event
	    GroupEvent _event = (GroupEvent) newGroupEvent;
	    
	    // update the event part of the group event
	    UpdateAppt(newGroupEvent);
	    
	    try {
	      
	      Class.forName("org.sqlite.JDBC");
	      c = DriverManager.getConnection("jdbc:sqlite:calendar.db");
	      c.setAutoCommit(false);
	      System.out.println("Opened database successfully");
	      
	      stmt = c.createStatement();
	      PreparedStatement query;
	      
	      query = c.prepareStatement("update event set startTime = ?, endTime = ?, eventTitle = ?, "+
	    		  					 "eventDescription = ?, eventReminderStart = ?, eventReminderEnd = ?, " +
	    		  					 "frequency = ?, locationID = ?, isGroup = ?, isPublic = ? " +
	    		  					 "where id = ?");
	      
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
	      query.setInt(11, _event.getID());
	      
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
	
	// delete a group event
	public boolean deleteGroupEvent (int groupID){
		return false;
	}
	
	// modify the user settings by replacing the user stored
	// return newly created user
	public void modifyUser(User newUser){
	}
	
	// modify other user setting as an admin
	// return newly created user
	public void modifyOtherUser(User newUser){
	}
	
	// create a new location
	// return the new location's id
	public int createLocation(Location location){
		return -1;
	}
	
	// modify the locatoin (only can be done by the admin)
	// return the newly modified location
	public void modifyLocation(Location location){
	}
	
	// delete the location given the location id
	public void deleteLocation(int locationID){
		
	}
	
	// helper functions
	// =====================

	// create user
	private User formatUser(ResultSet rs) throws SQLException{

		// Gather data
   	 	int id = rs.getInt("id");
   	 	String username = rs.getString("username");
   	 	String password = rs.getString("password");
	 	String firstname = rs.getString("first_name");
	 	String lastname = rs.getString("last_name");
	 	String email = rs.getString("email");
	 	boolean isAdmin = rs.getBoolean("isAdmin");

	 	User newUser = new User(username, password, firstname, lastname, email, isAdmin);
	 	newUser.setID(id);
	 	
   	 	return newUser;
	}
	
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
	
	private GroupEvent formatGroupEvent(ResultSet rs) throws SQLException {
		
		int initiatorID = rs.getInt("initiator");
		boolean confirmed = rs.getBoolean("confirmed");
		
		Event returnedEvent = formatEvent(rs);
		GroupEvent ge = new GroupEvent(returnedEvent);
		
		ge.setInitiatorID(initiatorID);
		ge.setConfirmed(confirmed);
		
		// get the list of users of the group
		List<Integer> userIDs = getGroupUserIDs(ge.getID());
		// set the users
		ge.setUsers(userIDs);
		
		return ge;
		
	}
	
	private Location formatLocation(ResultSet rs) throws SQLException {
		int id = rs.getInt("id");
		String name = rs.getString("name");
		boolean isGroup = rs.getBoolean("isGroupFacility");
		return new Location(id, name, isGroup);
	}



}
