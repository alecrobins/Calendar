package hkust.cse.calendar.unit;

import java.util.List;

import hkust.cse.calendar.unit.Appt.Frequency;

public class GroupEvent extends Appt {

	// which user initiaed the group event
	private int initiatorID;
	// is the group evetn confirmed by everyone
	private boolean confirmed;
	// is the group event confirmed by me (the current user)
	private boolean approved;
	// contains the list of users that are in the group
	private List<Integer> users;
	
	private List<TimeSpan> timeOptions;

	// Constructors - set empty parameters
	public GroupEvent() {
		super();
		setConfirmed(false);
		setApproved(false);
	}

	// Partial Constructor (the minimally required information)
	//	public GroupEvent(TimeSpan _eventTime, Frequency _eventFrequency)
	//	{
	//		super(_eventTime, _eventFrequency);
	//		setConfirmed(false);
	//		setApproved(false);
	//	}

	public GroupEvent(Appt e){
		super(e.getEventTime(), e.getTitle(), e.getEventDescription(), e.getEventLocation(), e.getEventReminder(), 
				"", e.getEventFrequency());
		this.id = e.id;
<<<<<<< HEAD

		setConfirmed(false);
		setApproved(false);

	}

=======
		setConfirmed(false);
		setApproved(false);
		this.setIsGroup(true);
		this.setIsPublic(true);
		
	}
	
	// Complete Constructor - without eventTime
	public GroupEvent(String _title, String _eventDescription, int _eventLocationID,
			TimeSpan _eventReminder, String _additionalEventDescription, Frequency _eventFrequency)
	{
		super(_title, _eventDescription, _eventLocationID, _eventReminder, _additionalEventDescription, _eventFrequency);
		setConfirmed(false);
		setApproved(false);
		this.setIsGroup(true);
		this.setIsPublic(true);
	}
	
>>>>>>> ca12a07270399638ef206978eb552ece9efe87f8
	// Complete Constructor
	public GroupEvent(TimeSpan _eventTime, String _title, String _eventDescription, int _eventLocationID,
			TimeSpan _eventReminder, String _additionalEventDescription, Frequency _eventFrequency, 
			List<TimeSpan> timeOption){

		super(_eventTime, _title, _eventDescription, _eventLocationID, _eventReminder, _additionalEventDescription, _eventFrequency);
		this.timeOptions = timeOption;
		setConfirmed(false);
		setApproved(false);
		this.setIsGroup(true);
		this.setIsPublic(true);
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
