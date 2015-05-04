package hkust.cse.calendar.unit;

import java.sql.Timestamp;
import java.util.Date;

//
// Basic Event model
//

public class Event extends Appt {
	
	// control the frequency of the event
	//The event frequency can be one-time, daily, weekly, or monthly
	public enum Frequency {
		ONETIME(0), DAILY(1), WEEKLY(2), MONTHLY(3);
		 
		private final int value;

	    private Frequency(int value) {
	        this.value = value;
	    }

	    public int getValue() {
	        return value;
	    }
	}
	
	// Private data members
	private TimeSpan eventTime;
	private String eventDescription;
//	private Location eventLocation;
	private int eventLocationID;
	private TimeSpan eventReminder;
	private String additionalEventDescription;
	private Frequency eventFrequency;
	
	// determines if this event is a group event
	private boolean isGroup;
	// determine if this event is public
	private boolean isPublic;
	
	// the id of the event
	private int id;
	
	// Constructors - set empty parameters
	public Event() {
		super();
		eventTime = null;
		eventDescription = null;
		eventLocationID = 0;
		eventReminder = null;
		additionalEventDescription = null;
		eventFrequency = null;
	}
	
	// Partial Constructor (the minimally required information)
	public Event(TimeSpan _eventTime, Frequency _eventFrequency)
	{
		super();
		eventTime = _eventTime;
		mTimeSpan = _eventTime;
		eventDescription = null;
		eventLocationID = 0;
		eventReminder = null;
		additionalEventDescription = null;
		eventFrequency = _eventFrequency;
	}
	
	// Complete Constructor
	public Event(TimeSpan _eventTime, String _title, String _eventDescription, int _eventLocationID,
			TimeSpan _eventReminder, String _additionalEventDescription, Frequency _eventFrequency)
	{
		super();
		eventTime = _eventTime;
		eventDescription = _title;
//		eventLocation = _eventLocation;
		eventLocationID = _eventLocationID;
		eventReminder = _eventReminder;
		additionalEventDescription = _additionalEventDescription;
		eventFrequency = _eventFrequency;
		
		// update the apt values
		mTimeSpan = _eventTime; 
		mTitle = _title;
		mInfo = _eventDescription;
	}
	
	// Getters
	public String getEventDescription() {
		return eventDescription;
	}
	public int getEventLocationID() {
		return eventLocationID;
	}
	public TimeSpan getEventReminder() {
		return eventReminder;
	}
	public String getAdditionalEventDescription() {
		return additionalEventDescription;
	}
	public Frequency getEventFrequency() {
		return eventFrequency;
	}
	public int getEventID(){
		return id;
	}
	
	
	// Setters
	public void setEventDescription(String s) {
		eventDescription = s; 
	}
	public void setEventLocation(int l) {
		eventLocationID = l;
	}
	public void setEventReminder(TimeSpan r) {
		eventReminder = r;
	}
	public void setAdditionalEventDescription(String s) {
		additionalEventDescription = s;
	}
	public void setEventFrequency(Frequency f) {
		eventFrequency = f;
	}
	public TimeSpan getEventTime() {
		return eventTime;
	}
	public void setEventTime(TimeSpan eventTime) {
		this.eventTime = eventTime;
		this.mTimeSpan = eventTime;
	}
	public void setEventID(int i){
		id = i;
	}

	public String toString() {
			
		String event =
		"ID: " + id + "  " +
		eventTime.toString() + " " +
		eventDescription + " " +
		eventLocationID+ " " +
		eventReminder + " " +
		additionalEventDescription + " " +
		eventFrequency + " " +
		
		// update the apt values
		mTimeSpan.toString() + " " +
		mTitle + " " +
		mInfo;
		
		return event;
	}
	
	public TimeSpan getNotification() {
		return eventReminder;
	}
	
	public void setNotification(TimeSpan ts) {
		eventReminder = ts;
	}

	public boolean getIsGroup() {
		return isGroup;
	}

	public void setIsGroup(boolean isGroup) {
		this.isGroup = isGroup;
	}

	public boolean getIsPublic() {
		return isPublic;
	}

	public void setIsPublic(boolean isPublic) {
		this.isPublic = isPublic;
	}
	

}
