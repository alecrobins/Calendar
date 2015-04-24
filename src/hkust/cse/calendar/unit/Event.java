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
		ONETIME, DAILY, WEEKLY, MONTHLY
	}
	
	// Private data members
	private TimeSpan eventTime;
	private String eventDescription;
	private Location eventLocation;
	private Date eventReminder;
	private String additionalEventDescription;
	private Frequency eventFrequency;
	
	// Constructors - set empty parameters
	public Event() {
		super();
		eventTime = null;
		eventDescription = null;
		eventLocation = null;
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
		eventLocation = null;
		eventReminder = null;
		additionalEventDescription = null;
		eventFrequency = _eventFrequency;
	}
	
	// Complete Constructor
	public Event(TimeSpan _eventTime, String _title, String _eventDescription, Location _eventLocation,
			Date _eventReminder, String _additionalEventDescription, Frequency _eventFrequency)
	{
		super();
		eventTime = _eventTime;
		eventDescription = _title;
		eventLocation = _eventLocation;
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
	public Location getEventLocation() {
		return eventLocation;
	}
	public Date getEventReminder() {
		return eventReminder;
	}
	public String getAdditionalEventDescription() {
		return additionalEventDescription;
	}
	public Frequency getEventFrequency() {
		return eventFrequency;
	}
	
	// Setters
	public void setEventDescription(String s) {
		eventDescription = s; 
	}
	public void setEventLocation(Location l) {
		eventLocation = l;
	}
	public void setEventReminder(Date r) {
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

	public String toString() {
			
		String event = eventTime.toString() + " " +
		eventDescription + " " +
		eventLocation+ " " +
		eventReminder + " " +
		additionalEventDescription + " " +
		eventFrequency + " " +
		
		// update the apt values
		mTimeSpan.toString() + " " +
		mTitle + " " +
		mInfo;
		
		return event;
	}
	

}
