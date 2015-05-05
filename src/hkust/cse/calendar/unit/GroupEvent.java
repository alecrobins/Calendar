package hkust.cse.calendar.unit;

import java.util.List;

import hkust.cse.calendar.unit.Event.Frequency;

public class GroupEvent extends Event {
	
	// which user initiaed the group event
	private int initiatorID;
	// is the group evetn confirmed by everyone
	private boolean confirmed;
	// is the group event confirmed by me (the current user)
	private boolean approved;
	// contains the list of users that are in the group
	private List<Integer> users;
	
	// Constructors - set empty parameters
	public GroupEvent() {
		super();
		setConfirmed(false);
		setApproved(false);
	}
	
	// Partial Constructor (the minimally required information)
	public GroupEvent(TimeSpan _eventTime, Frequency _eventFrequency)
	{
		super(_eventTime, _eventFrequency);
		setConfirmed(false);
		setApproved(false);
	}
	
	// Complete Constructor
	public GroupEvent(TimeSpan _eventTime, String _title, String _eventDescription, int _eventLocationID,
			TimeSpan _eventReminder, String _additionalEventDescription, Frequency _eventFrequency)
	{
		super(_eventTime, _title, _eventDescription, _eventLocationID, _eventReminder, _additionalEventDescription, _eventFrequency);
		setConfirmed(false);
		setApproved(false);
	}

	public boolean isConfirmed() {
		return confirmed;
	}

	public void setConfirmed(boolean confirmed) {
		this.confirmed = confirmed;
	}

	public boolean isApproved() {
		return approved;
	}

	public void setApproved(boolean approved) {
		this.approved = approved;
	}

	public List<Integer> getUsers() {
		return users;
	}

	public void setUsers(List<Integer> users) {
		this.users = users;
	}

	public int getInitiatorID() {
		return initiatorID;
	}

	public void setInitiatorID(int initiatorID) {
		this.initiatorID = initiatorID;
	}
	
}
