 package hkust.cse.calendar.apptstorage;//

import hkust.cse.calendar.apptstorage.ApptStorage;
import hkust.cse.calendar.unit.Appt;
import hkust.cse.calendar.unit.Clock;
import hkust.cse.calendar.unit.Event;
import hkust.cse.calendar.unit.GroupEvent;
import hkust.cse.calendar.unit.GroupResponse;
import hkust.cse.calendar.unit.Invite;
import hkust.cse.calendar.unit.Location;
import hkust.cse.calendar.unit.TimeSpan;
import hkust.cse.calendar.unit.User;
import hkust.cse.calendar.unit.Appt.Frequency;

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

public class ApptStorageSQLImplLocal extends ApptStorage {
	
	private int interval = 60;
	private User defaultUser = null;
	
	public ApptStorageSQLImplLocal( User user )
	{
		defaultUser = user;
	}
	
	public ApptStorageSQLImplLocal(){
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
	    Appt event = null;
	    
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
	    		  "where ue.userID = "+ defaultUser.getID() +
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
	
	// return a list of all the possible locations
	public List<Location> getAllLocations(){
		Connection c = null;
	    Statement stmt = null;
	    
	    // return the event
	    List<Location> locations = new ArrayList<Location>();
	    
	    try {
	      Class.forName("org.sqlite.JDBC");
	      c = DriverManager.getConnection("jdbc:sqlite:calendar.db");
	      c.setAutoCommit(false);
	      System.out.println("Opened database successfully");
	      
	      stmt = c.createStatement();
	      String sql = "select id, name, isGroupFacility from location;";
	      
	      ResultSet rs = stmt.executeQuery( sql );
	      
	      // go through results
	      while ( rs.next() ) {
	    	 locations.add(formatLocation(rs));
	      }
	      
	      rs.close();
	      stmt.close();
	      c.close();
		    
	    } catch ( Exception e1 ) {
	      System.err.println( e1.getClass().getName() + ": " + e1.getMessage() );
	      System.exit(0);
	    }
	    	    
		return locations;
	}
	
	
	@Override
	// returns the appt id
	public int SaveAppt(Appt _event) {
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
	      
	      Timestamp reminderStartTime = null;
	      Timestamp reminderEndTime = null;
	      
	      Timestamp eventStartTime = null ;
	      Timestamp eventEndTime = null;
	      
	      // check if the event reminder is null
	      if(_event.getEventReminder() != null){
	    	 reminderStartTime = _event.getEventReminder().StartTime();
	    	 reminderEndTime = _event.getEventReminder().EndTime();
	      }
	      
	      if(_event.getEventTime() != null){
	    	  eventStartTime = _event.getEventTime().StartTime();
	    	  eventEndTime = _event.getEventTime().EndTime();
	      }
	      
	      int locationID = _event.getEventLocationID();
	      
	      // assign variables
	      query.setTimestamp(1, eventStartTime);
	      query.setTimestamp(2, eventEndTime);
	      query.setString(3, _event.getTitle());
	      query.setString(4, _event.getEventDescription());
	      query.setTimestamp(5, reminderStartTime);
	      query.setTimestamp(6, reminderEndTime);
	      query.setInt(7, _event.getEventFrequency().getValue());
	      query.setInt(8, locationID);
	      query.setBoolean(9, _event.getIsGroup());
	      query.setBoolean(10, _event.getIsPublic());
	      
	      boolean done = query.execute();
	      
		  // Get the generated key from the event creation
	      ResultSet rs = stmt.getGeneratedKeys();
		  rs.next();
		  eventID = rs.getInt(1);

		  // create a connection between the user and event
		  query = c.prepareStatement("insert into userEvent (userID, eventID) values (?, ?)");
		  query.setInt(1, defaultUser.getID());
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
		return RetrieveAppts(defaultUser, d);
	}
	@Override
	public Appt[] RetrieveAppts(User entity, TimeSpan time) {
		return null;
	}

	public List<Appt> RetrieveApptsList(User entity, TimeSpan time) {
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
	  	     Appt event = null;
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
	
	// generate a list so that all appts get created based on teh frequency
	public List<Appt> generateList(List<Appt> appts){
		List<Appt> generatedList = new ArrayList<Appt>();
		
		// add all generated appts
		for(int i = 0; i < appts.size(); ++i){
			generatedList.addAll(generateEvents(appts.get(i)));
		}
		
		return generatedList;
		
	}
	

	public List<Appt> generateEvents(Appt e){
		List<Appt> appts = new ArrayList<Appt>();
		
		// Add notification in to notification array
		int time = -1;
		Appt pastEvent = null;
		switch (e.getEventFrequency()){
		case ONETIME:		
			appts.add(e);
			break;
		case WEEKLY:
			appts.add(e);
			
			pastEvent = e;
			
			for (int i = 0; i < 52; i++)   { //1 years in weeks
				
				// Set teh current tim 
				TimeSpan curr = pastEvent.getEventTime();
				Timestamp start = new Timestamp(curr.StartTime().getTime()+604800000);
				Timestamp fin = new Timestamp(curr.EndTime().getTime()+604800000);
				
				// generate the new event
				Appt eNew = formatEvent(start, fin, e);
				appts.add(eNew);

				// set past event
				pastEvent = eNew;
				
			}
			break;
		case MONTHLY:
			// save the first event
			appts.add(e);

			pastEvent = e;

			for (int i = 0; i < 13; i++){   //1 years in groups of 4 weeks
				TimeSpan curr = pastEvent.getEventTime();
				
				Timestamp start = new Timestamp( curr.StartTime().getTime() ); 
				Timestamp end = new Timestamp ( curr.EndTime().getTime() );
				
				start.setMonth(curr.StartTime().getMonth()+1);
				end.setMonth(curr.EndTime().getMonth()+1);
				
				// generate the new event
				Appt eNew = formatEvent(start, end, e);
				appts.add(eNew);
						
				// set past event
				pastEvent = eNew;
			}
			break;
		case DAILY:
			// save the first event
			appts.add(e);
	
			pastEvent = e;
			
			for (int i = 0; i < 365; i++){
				TimeSpan curr = pastEvent.getEventTime();
				Timestamp start = new Timestamp(curr.StartTime().getTime()+86400000);
				Timestamp end = new Timestamp(curr.EndTime().getTime()+86400000);
				// save to db
				
				// generate the new event
				Appt eNew = formatEvent(start, end, e);
				appts.add(eNew);
								
				// set past event
				pastEvent = eNew;
			}
			break;
		}
		
		return appts;
		
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
		eNew.setID(e.getEventID());
		
		return eNew;
	}

	@Override
	public void UpdateAppt(Appt appt) {
		Connection c = null;
	    Statement stmt = null;
	    
	    // cast the appt to an event
	    Appt _event = appt;
	    
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
	      query.setInt(8, _event.getEventLocation());
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
	    Appt _event = appt;
	    
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
	      
	      // delete from userEvent
	      query = c.prepareStatement("delete from userEvent " +
					 "where eventID = ? AND userID = ?");
	      int id = _event.getEventID();
	      int userId = defaultUser.getID();
	      // assign variables
	      query.setInt(1, id);
	      query.setInt(2, userId);
		
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
	}

	@Override
	public User getDefaultUser() {
		return defaultUser;
	}
	
	
	// NOTIFICATIONS 
	// ===================
	
	@Override
	public void addNotification(TimeSpan ts) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public boolean findNotification(TimeSpan ts) {
		// TODO Auto-generated method stub
		return false;
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
	    Appt event = null;
	    
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
	    		  "where ue.userID = "+ defaultUser.getID() +
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
	
	// NOTE: this class will return all events past the current date (what ever is set in clock)
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
	      
	      Clock clock = new Clock();
	      long currentTime = clock.getChangedTime().getTime().getTime();
	      
	      query = c.prepareStatement( 
	    		  "select distinct e.id as 'id', e.startTime as 'startTime', e.endTime as 'endTime', " +
	    		  "e.eventTitle as 'title', e.eventDescription as 'description', " +
	    		  "e.frequency as 'frequency', e.eventReminderStart as 'reminderStart', " +
	    		  "e.eventReminderEnd as 'reminderEnd', e.locationID as 'locationID', " +
	    		  "e.isGroup as 'isGroup', e.isPublic as 'isPublic' " +
	    		  "from event e, userEvent ue " +
	    		  "where ue.userID = ? "+
	    		  "and e.id = ue.eventID and e.startTime >= ?;");
	      
	      query.setInt(1, userID); 
	      query.setInt(2, (int) currentTime);
	      
	      ResultSet rs = query.executeQuery();
	      
	      // go through results
	      while ( rs.next() ) {
	    	  // return the event
	  	     Appt event = null;
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
	  	     Appt event = null;
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
			
			// NOTE: only add confimred events
			if(groupEvent.isConfirmed()){
				// push the group event into the group
				groupEvents.add(groupEvent);	
			}
			
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
	public int createGroupEvent(List<Integer> users, Appt event) throws InvalidClassException{
		// assert that it is a group a event that is being created
		if(!(event instanceof GroupEvent))
			throw new InvalidClassException("You can only save GroupEvents not Regular events");
		
		GroupEvent groupEvent = new GroupEvent( event );
		
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
	      query.setInt(2, defaultUser.getID());
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
	    return groupID;
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
	
	// retrieve the user from db with username
	public User getUser(String username){
		Connection c = null;
	    Statement stmt = null;
	    
	    User user = null;

	    try {
	      Class.forName("org.sqlite.JDBC");
	      c = DriverManager.getConnection("jdbc:sqlite:calendar.db");
	      c.setAutoCommit(false);
	      System.out.println("Opened database successfully");
	      
	      stmt = c.createStatement();
	      PreparedStatement query;
	      
	      query = c.prepareStatement( 
	    		  "select id, username, password, first_name, last_name, email, isAdmin " +
	    		  "from user where username = ?");
	      
	      query.setString(1, username);
	      
	      ResultSet rs = query.executeQuery();
	      
	      // go through results
	      while ( rs.next() ) {
	    	  // add the user
	    	  user = formatUser(rs);
	      }
	      
	      rs.close();
	      stmt.close();
	      c.close();
		    
	    } catch ( Exception e1 ) {
	      System.err.println( e1.getClass().getName() + ": " + e1.getMessage() );
	      System.exit(0);
	    }
	    
	    return user;
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
	
	// modify the group with a new group event and users
	// NOTE: that only the intiator of admin
	public void modifyGroupEvent (Appt newGroupEvent, int groupID){
		
	    // update the event part of the group event
	    UpdateAppt(newGroupEvent);
	    
	}
	
	// delete a group event
	public void deleteGroupEvent (int groupID){
		Connection c = null;
	    Statement stmt = null;

	    try {
	      Class.forName("org.sqlite.JDBC");
	      c = DriverManager.getConnection("jdbc:sqlite:calendar.db");
	      c.setAutoCommit(false);
	      System.out.println("Opened database successfully");
	      
	      stmt = c.createStatement();
	      PreparedStatement query;
	      
	      query = c.prepareStatement("delete from event " +
		    		"where id = ?");
	      
	      query.setInt(1, groupID);
	      
	      boolean done = query.execute();
   	   
	      // remove from group event
	      query = c.prepareStatement("delete from groupEvent " +
		    		"where eventID = ?");

	      query.setInt(1, groupID);
	      
	      done = query.execute();
	      
	      // remove from group event
	      query = c.prepareStatement("delete from groupUser " +
		    		"where eventID = ?");

	      query.setInt(1, groupID);
	      
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
	    
	}
	
	// modify the user settings by replacing the user stored
	// return newly created user
	public void modifyUser(User newUser){
		Connection c = null;
	    Statement stmt = null;
		
		try {
		      Class.forName("org.sqlite.JDBC");
		      c = DriverManager.getConnection("jdbc:sqlite:calendar.db");
		      c.setAutoCommit(false);
		      System.out.println("Opened database successfully");
		      
		      stmt = c.createStatement();
		      PreparedStatement query;
		      
		      query = c.prepareStatement("update user set username = ?, password = ?, first_name = ?, last_name = ?, email = ?, isAdmin = ? "+
		    		  					 "where id = ?;");
		      // approve the appropiate event
		      query.setString(1, newUser.getUsername());
		      query.setString(2, newUser.Password());
		      query.setString(3, newUser.getFirstname());
		      query.setString(4, newUser.getLastname());
		      query.setString(5, newUser.getEmail());
		      query.setBoolean(6, newUser.isAdmin());
		      query.setInt(7, newUser.getID());
		      
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
	
	// create a new location
	// return the new location's id
	public int createLocation(Location location){
		Connection c = null;
	    Statement stmt = null;
		
	    int locationID = -1;
	    
		try {
		      Class.forName("org.sqlite.JDBC");
		      c = DriverManager.getConnection("jdbc:sqlite:calendar.db");
		      c.setAutoCommit(false);
		      System.out.println("Opened database successfully");
		      
		      stmt = c.createStatement();
		      PreparedStatement query;
		      
		      query = c.prepareStatement("insert into location (id, name, isGroupFacility) "+
		    		  					 "values (null, ?, ?);", Statement.RETURN_GENERATED_KEYS);
		      
		      query.setString(1, location.getName());
		      query.setBoolean(2, location.getIsGroupFacility());

		      boolean done = query.execute();

		   // Get the generated key from the event creation
		      ResultSet rs = stmt.getGeneratedKeys();
			  rs.next();
			  locationID = rs.getInt(1);
			  
		   // commit
		      c.commit();
		      
		      query.close();
		      stmt.close();
		      c.close();
			    
		    } catch ( Exception e1 ) {
		      System.err.println( e1.getClass().getName() + ": " + e1.getMessage() );
		      System.exit(0);
		    }
		
		return locationID;
	}
	
	public Location getLocationByName (String name){
		Connection c = null;
	    Statement stmt = null;
		Location l = null;
		try {
		      Class.forName("org.sqlite.JDBC");
		      c = DriverManager.getConnection("jdbc:sqlite:calendar.db");
		      c.setAutoCommit(false);
		      System.out.println("Opened database successfully");
		      
		      stmt = c.createStatement();
		      PreparedStatement query;
		      
		      query = c.prepareStatement("select id, isGroupFacility from location where name = ?");
		      
		      query.setString(1, name);
		      ResultSet rs = query.executeQuery();
		      // go through results
		      while ( rs.next() ) {
	    		int id = rs.getInt("id");
	    		boolean isGroup = rs.getBoolean("isGroupFacility");
	    		
	    		Location newLocation = new Location(name, isGroup);
	    		newLocation.setLocationID(id);
	    		
	    		l = newLocation;  
		      }			  
		  
		      query.close();
		      stmt.close();
		      c.close();
			    
		    } catch ( Exception e1 ) {
		      System.err.println( e1.getClass().getName() + ": " + e1.getMessage() );
		      System.exit(0);
		    }
		return l;
	}
	
	// modify the locatoin (only can be done by the admin)
	// return the newly modified location
	public void modifyLocation(Location location){
		
		Connection c = null;
	    Statement stmt = null;
		
		try {
		      Class.forName("org.sqlite.JDBC");
		      c = DriverManager.getConnection("jdbc:sqlite:calendar.db");
		      c.setAutoCommit(false);
		      System.out.println("Opened database successfully");
		      
		      stmt = c.createStatement();
		      PreparedStatement query;
		      
		      query = c.prepareStatement("update location set name = ? , isGroupFacility = ? where id = ?; ");
		      
		      query.setString(1, location.getName());
		      query.setBoolean(2, location.getIsGroupFacility());
		      query.setInt(3, location.getLocationID());

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
	
	// delete the location given the location id
	public void deleteLocation(int locationID){
		Connection c = null;
	    Statement stmt = null;
		
		try {
		      Class.forName("org.sqlite.JDBC");
		      c = DriverManager.getConnection("jdbc:sqlite:calendar.db");
		      c.setAutoCommit(false);
		      System.out.println("Opened database successfully");
		      
		      stmt = c.createStatement();
		      PreparedStatement query;
		      
		      query = c.prepareStatement("delete from location where id = ?; ");
		      
		      query.setInt(1, locationID);

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
	
	// delete a location by the name
	public void deleteLocationByName(String name){
		Connection c = null;
	    Statement stmt = null;
		
		try {
		      Class.forName("org.sqlite.JDBC");
		      c = DriverManager.getConnection("jdbc:sqlite:calendar.db");
		      c.setAutoCommit(false);
		      System.out.println("Opened database successfully");
		      
		      stmt = c.createStatement();
		      PreparedStatement query;
		      
		      query = c.prepareStatement("delete from location where name = ?; ");
		      
		      query.setString(1, name);

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
	
	private Appt formatEvent(ResultSet rs) throws SQLException {
		 // Gather data
  	  
   	 	int id = rs.getInt("id");
   	 	Timestamp startTime = rs.getTimestamp("startTime");
   	 	Timestamp endTime = rs.getTimestamp("endTime");
        String eventTitle = rs.getString("title");
        String  eventDescription = rs.getString("description");
        int frequencyNum = rs.getInt("frequency");
        Timestamp eventReminderStart = rs.getTimestamp("reminderStart");
        Timestamp eventReminderEnd = rs.getTimestamp("reminderEnd");
        int locationID = rs.getInt("locationID");
        boolean isGroup = rs.getBoolean("isGroup");
        boolean isPublic = rs.getBoolean("isPublic");
        
        TimeSpan eventTime = null;
        
        if(startTime != null && endTime != null){
        	eventTime = new TimeSpan(startTime, endTime);
        }
        
        // set the reminder
        TimeSpan reminder = null;
        if(!(eventReminderStart == null || eventReminderEnd == null))
        	reminder = new TimeSpan(eventReminderStart, eventReminderEnd);
        
        Frequency frequency = Frequency.values()[frequencyNum];
        
        System.out.println(id);
        
        Appt newEvent = new Appt(eventTime, eventTitle, eventDescription, locationID, reminder, "", frequency);
        newEvent.setEventID(id);
        return newEvent;
	}
	
	private GroupEvent formatGroupEvent(ResultSet rs) throws SQLException {
		
		int initiatorID = rs.getInt("initiator");
		boolean confirmed = rs.getBoolean("confirmed");
		
		Appt returnedEvent = formatEvent(rs);
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
		
		Location newLocation = new Location(name, isGroup);
		newLocation.setLocationID(id);
		
		return newLocation;
	}
	
	private GroupResponse formatGroupResponse(ResultSet rs) throws SQLException {
	
		// TODO: need to iterate all in teh group
		ResultSet r = rs;
		
		int intiatorID = rs.getInt("initiatorID");
		int eventID = rs.getInt("eventID");
		Timestamp startTime = rs.getTimestamp("startTime");
		Timestamp endTime = rs.getTimestamp("endTime");
		
		TimeSpan newTimeSlot = new TimeSpan(startTime, endTime);
		
		List<TimeSpan> tempTimeSlots = new ArrayList<TimeSpan>();
		tempTimeSlots.add(newTimeSlot);
		

		GroupResponse groupResponse = new GroupResponse(intiatorID, tempTimeSlots, eventID);
		
		return groupResponse;
	}
	
	
	// N/A
	@Override
	public Appt RetrieveAppts(int joinApptID) {
		return null;
	}

	@Override
	public void LoadApptFromXml() {
	}

	@Override
	public boolean isApptValid(Appt appt) {
		return false;
	}
	
	// Group Event Information
	// create the purposed group event
		// assume that the default user is the initiator of the group
		// assume that GroupEvent was saved to DB before as not confirmed
	public void createPurposedGroupEvent(GroupEvent _event, List<TimeSpan> _timeSlots, List<User> _users){
		int groupID = _event.getEventID();
		
		Connection c = null;
	    Statement stmt = null;
		
		try {
		      Class.forName("org.sqlite.JDBC");
		      c = DriverManager.getConnection("jdbc:sqlite:calendar.db");
		      c.setAutoCommit(false);
		      System.out.println("Opened database successfully");
		      
		      stmt = c.createStatement();
		      PreparedStatement query;
		      // save all the proposed time slots
		      for(int i = 0; i < _timeSlots.size(); ++i){
		    	  // connect the proposed time slot with the users
				  for(int j = 0; j < _users.size(); ++j){
		    	  
		    	  TimeSpan currentTimeSlot = _timeSlots.get(i);
		    	  
		    	  // save the groupUserTimeSlot
			      query = c.prepareStatement("insert into groupUserTimeSlots (initiatorID, userID, eventID, startTime, endTime) " +
			    		  					 "values (?, ?, ?, ?, ?)");
			      // save the time slot
			      query.setInt(1, defaultUser.getID());
				  query.setInt(2, _users.get(j).getID());
			      query.setInt(3, groupID);
			      query.setTimestamp(4, currentTimeSlot.StartTime());
			      query.setTimestamp(5, currentTimeSlot.EndTime());
			      
			      query.execute();
			      
			      // commit
			      c.commit();
			      query.close();
				 }
		      }
		      // connect the proposed time slot with the users
			  for(int k = 0; k < _users.size(); ++k){
				  // save all the users as a groupUser
				  query = c.prepareStatement("insert into groupUser (eventID, userID, approved) " +
		    		  					 "values (?, ?, ?)");
				  query.setInt(1, groupID);
				  query.setInt(2, _users.get(k).getID());
				  query.setBoolean(3, false);
			  }
				      
			     
		    } catch ( Exception e1 ) {
		      System.err.println( e1.getClass().getName() + ": " + e1.getMessage() );
		      System.exit(0);
		    }
		
	}
	
	// Retrieve a list of groups you need to approve
	public List<GroupResponse> getPurposedGroupEventTimeSlots(User u){

		Connection c = null;
	    Statement stmt = null;
	    
	    List<GroupResponse> responses = new ArrayList<GroupResponse>();
		
		try {

		  Class.forName("org.sqlite.JDBC");
	      c = DriverManager.getConnection("jdbc:sqlite:calendar.db");
	      c.setAutoCommit(false);
	      System.out.println("Opened database successfully");
	      
	      stmt = c.createStatement();
	      PreparedStatement query;
	      
	      query = c.prepareStatement("select initiatorID, eventID, startTime, endTime " +
	    		  					 "from groupUserTimeSlots " +
	    		  					 "where userID = ? ");
	      
	      query.setInt(1, u.getID());
	      
	      ResultSet rs = query.executeQuery();
	      
  		  List<TimeSpan> tempTimeSlots = new ArrayList<TimeSpan>();
  		  int intiatorID = -1;
  		  int eventID = -1;
	      // go through results
	      while ( rs.next() ) {
//	    	 GroupResponse groupResponse = formatGroupResponse(rs);
	    		// TODO: need to iterate all in teh group
	  		
	    	intiatorID = rs.getInt("initiatorID");
	  		eventID = rs.getInt("eventID");
	  		Timestamp startTime = rs.getTimestamp("startTime");
	  		Timestamp endTime = rs.getTimestamp("endTime");
	  		
	  		TimeSpan newTimeSlot = new TimeSpan(startTime, endTime);
	  		
	  		tempTimeSlots.add(newTimeSlot);
	      }
	      
	  		GroupResponse groupResponse = new GroupResponse(intiatorID, tempTimeSlots, eventID);
	  		// TODO: need to filter the responses before creating them through a hash
	  		responses.add(groupResponse);
	      
	      stmt.close();
	      c.close();
		    
	    } catch ( Exception e1 ) {
	      System.err.println( e1.getClass().getName() + ": " + e1.getMessage() );
	      System.exit(0);
	    }
		
		return responses;
	}
	
	// Respone to the retrival of events but 
	// NOTE: by sending this response the user is approving to the group
	public void respondToPurposedGroupEventTimeSlots(int groupID, int intiatorID, List<TimeSpan> _timeSlots){
		Connection c = null;
	    Statement stmt = null;
	    
		try {

		  Class.forName("org.sqlite.JDBC");
	      c = DriverManager.getConnection("jdbc:sqlite:calendar.db");
	      c.setAutoCommit(false);
	      System.out.println("Opened database successfully");
	      
	      stmt = c.createStatement();
	      PreparedStatement query;
	      
	      for(int i = 0; i < _timeSlots.size(); ++i){
	    	  query = c.prepareStatement("insert into groupUserTimeSlotsSelected (initiatorID, userID, eventID, startTime, endTime) " +
			    		  					 "values (?, ?, ?, ?, ?)");	    	  
	    	// save the time slot
		      query.setInt(1, intiatorID);
			  query.setInt(2, defaultUser.getID());
		      query.setInt(3, groupID);
		      query.setTimestamp(4, _timeSlots.get(i).StartTime());
		      query.setTimestamp(5, _timeSlots.get(i).EndTime());
	    	  
		      query.execute();
		      
		      // commit
		      c.commit();
		      query.close();
	  
	      }
	      
	      // approve the group event
	      approveGroupEvent(groupID, defaultUser.getID());
		    
	    } catch ( Exception e1 ) {
	      System.err.println( e1.getClass().getName() + ": " + e1.getMessage() );
	      System.exit(0);
	    }
		
		// need to check if event is confirmed
		checkIfEventConfirmed(groupID);
		
	}
	
	// check if group event is confirmed and if it is then make event confirmed
	public void checkIfEventConfirmed(int _groupID){

		Connection c = null;
	    Statement stmt = null;
	    
	    boolean confirmed = true;
	    
		try {

		  Class.forName("org.sqlite.JDBC");
	      c = DriverManager.getConnection("jdbc:sqlite:calendar.db");
	      c.setAutoCommit(false);
	      System.out.println("Opened database successfully");
	      
	      stmt = c.createStatement();
	      PreparedStatement query;
	      
	      query = c.prepareStatement("select approved " +
	    		  					 "from groupUser " +
	    		  					 "where eventID = ? ");
	      
	      query.setInt(1, _groupID);
	      
	      ResultSet rs = query.executeQuery();

	      // go through results
	      while ( rs.next() ) {
	    	 boolean isApproved = rs.getBoolean("approved");
	    	 if(!isApproved){
	    		 confirmed = false;
	    		 break;
	    	 }
	      }
	      
	      // update the approval / time of the event
	      // and select the minimum approved time
	      if(confirmed){

	    	  // get the minimum timespan
		      query = c.prepareStatement("select distinct g.startTime, g.endTime from groupUserTimeSlotsSelected g where startTime = ( select min(startTime) from groupUserTimeSlotsSelected where eventID = ?);");
		      
		      query.setInt(1, _groupID);
		      
		      rs = query.executeQuery();
		      
		      Timestamp startTime = null;
		      Timestamp endTime = null;
		      // go through results
		      while ( rs.next() ) {
		    	  startTime = rs.getTimestamp("startTime");
		    	  endTime = rs.getTimestamp("endTime");
		      }
		      
	    	  // set teh groupEvent to confirmed
	    	  query = c.prepareStatement("update groupEvent set confirmed = 1 " +
	  					 "where eventID = ? ");
	    	  query.setInt(1, _groupID);
	    	  
	    	  query.execute();
	    	  
	    	  // set the time of the event
	    	  query = c.prepareStatement("update event set startTime = ?, endTime = ? " +
	  					 "where id = ? ");
	    	  query.setTimestamp(1, startTime);
	    	  query.setTimestamp(2, endTime);
	    	  query.setInt(3, _groupID);
	    	  
	    	  query.execute();
			
	    	  // commit
	    	  c.commit();
	    	  
	    	  deleteGroupProposedEvent(_groupID);
	    	  
	      }
	      
	      query.close();
	      stmt.close();
	      c.close();
		    
	    } catch ( Exception e1 ) {
	      System.err.println( e1.getClass().getName() + ": " + e1.getMessage() );
	      System.exit(0);
	    }
		
	}
	
	// cancel the group proposal and clean up data
	public void cancelPurposedGroupEventTimeSlots(int groupID){
	
		deleteGroupEvent(groupID);
		deleteGroupProposedEvent(groupID);
		
	}
	
	public void deleteGroupProposedEvent(int groupID){

		Connection c = null;
	    Statement stmt = null;

	    try {
	      Class.forName("org.sqlite.JDBC");
	      c = DriverManager.getConnection("jdbc:sqlite:calendar.db");
	      c.setAutoCommit(false);
	      System.out.println("Opened database successfully");
	      
	      stmt = c.createStatement();
	      PreparedStatement query;
	      
	      query = c.prepareStatement("delete from groupUserTimeSlots " +
		    		"where eventID = ?;");
	      
	      query.setInt(1, groupID);
	      
	      boolean done = query.execute();
   	   
	      // remove from group event
	      query = c.prepareStatement("delete from groupUserTimeSlotsSelected " +
		    		"where eventID = ?");

	      query.setInt(1, groupID);
	      
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
	}
	
	
}
