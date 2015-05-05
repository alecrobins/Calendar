package hkust.cse.calendar.unit;


import java.io.Serializable;
import java.util.LinkedList;

public class Appt implements Serializable {

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
	
	protected TimeSpan mTimeSpan;					// Include day, start time and end time of the appointments

	protected String mTitle;						// The Title of the appointments

	protected String mInfo;						// Store the content of the appointments description

	private int eventLocationID;
	
	private TimeSpan eventReminder;
	
	protected int mApptID;						// The appointment id
	
	protected int joinApptID;						// The join appointment id

	protected boolean isjoint;					// The appointment is a joint appointment
	
	protected LinkedList<String> attend;			// The Attendant list
	
	protected LinkedList<String> reject;			// The reject list
	
	protected LinkedList<String> waiting;			// The waiting list
	
	private Frequency eventFrequency;
	
	// determines if this event is a group event
	private boolean isGroup;
	// determine if this event is public
	private boolean isPublic;
	
	// the id of the event
	protected int id;
	
	public Appt() {								// A default constructor used to set all the attribute to default values
		mApptID = 0; 
		mTimeSpan = null;
		mTitle = "Untitled";
		mInfo = "";
		isjoint = false;
		attend = new LinkedList<String>();
		reject = new LinkedList<String>();
		waiting = new LinkedList<String>();
		joinApptID = -1;
	}
	
	public void generateID(){
		this.mApptID = (int) this.mTimeSpan.StartTime().getTime();
	}

	// Getter of the mTimeSpan
	public TimeSpan TimeSpan() {
		return mTimeSpan;
	}
	
	// Getter of the appointment title
	public String getTitle() {
		return mTitle;
	}

	// Getter of appointment description
	public String getInfo() {
		return mInfo;
	}

	// Getter of the appointment id
	public int getID() {
		return mApptID;
	}
	
	// Getter of the join appointment id
	public int getJoinID(){
		return joinApptID;
	}

	public void setJoinID(int joinID){
		this.joinApptID = joinID;
	}
	// Getter of the attend LinkedList<String>
	public LinkedList<String> getAttendList(){
		return attend;
	}
	
	// Getter of the reject LinkedList<String>
	public LinkedList<String> getRejectList(){
		return reject;
	}
	
	// Getter of the waiting LinkedList<String>
	public LinkedList<String> getWaitingList(){
		return waiting;
	}
	
	public LinkedList<String> getAllPeople(){
		LinkedList<String> allList = new LinkedList<String>();
		allList.addAll(attend);
		allList.addAll(reject);
		allList.addAll(waiting);
		return allList;
	}
	
	public void addAttendant(String addID){
		if (attend == null)
			attend = new LinkedList<String>();
		attend.add(addID);
	}
	
	public void addReject(String addID){
		if (reject == null)
			reject = new LinkedList<String>();
		reject.add(addID);
	}
	
	public void addWaiting(String addID){
		if (waiting == null)
			waiting = new LinkedList<String>();
		waiting.add(addID);
	}
	
	public void setWaitingList(LinkedList<String> waitingList){
		waiting = waitingList;
	}
	
	public void setWaitingList(String[] waitingList){
		LinkedList<String> tempLinkedList = new LinkedList<String>();
		if (waitingList !=null){
			for (int a=0; a<waitingList.length; a++){
				tempLinkedList.add(waitingList[a].trim());
			}
		}
		waiting = tempLinkedList;
	}
	
	public void setRejectList(LinkedList<String> rejectLinkedList) {
		reject = rejectLinkedList;
	}
	
	public void setRejectList(String[] rejectList){
		LinkedList<String> tempLinkedList = new LinkedList<String>();
		if (rejectList !=null){
			for (int a=0; a<rejectList.length; a++){
				tempLinkedList.add(rejectList[a].trim());
			}
		}
		reject = tempLinkedList;
	}
	
	public void setAttendList(LinkedList<String> attendLinkedList) {
		attend = attendLinkedList;
	}
	
	public void setAttendList(String[] attendList){
		LinkedList<String> tempLinkedList = new LinkedList<String>();
		if (attendList !=null){
			for (int a=0; a<attendList.length; a++){
				tempLinkedList.add(attendList[a].trim());
			}
		}
		attend = tempLinkedList;
	}
	
	// Getter of the appointment title
	public String toString() {
		return mTitle;
	}

	// Setter of the appointment title
	public void setTitle(String t) {
		mTitle = t;
	}

	// Setter of the appointment description
	public void setInfo(String in) {
		mInfo = in;
	}

	// Setter of the mTimeSpan
	public void setTimeSpan(TimeSpan d) {
		mTimeSpan = d;
	}

	// Setter if the appointment id
	public void setID(int id) {
		mApptID = id;
	}
	
	// check whether this is a joint appointment
	public boolean isJoint(){
		return isjoint;
	}

	// setter of the isJoint
	public void setJoint(boolean isjoint){
		this.isjoint = isjoint;
	}


	// Getters
	public String getEventDescription() {
		return mInfo;
	}
	public int getEventLocationID() {
		return eventLocationID;
	}
	public TimeSpan getEventReminder() {
		return eventReminder;
	}

	public Frequency getEventFrequency() {
		return eventFrequency;
	}
	public int getEventID(){
		return id;
	}
	
	
	// Setters
	public void setEventDescription(String s) {
		mInfo = s; 
	}
	public void setEventLocation(int l) {
		eventLocationID = l;
	}
	public void setEventReminder(TimeSpan r) {
		eventReminder = r;
	}
	public void setEventFrequency(Frequency f) {
		eventFrequency = f;
	}
	public TimeSpan getEventTime() {
		return mTimeSpan;
	}
	public void setEventTime(TimeSpan eventTime) {
		this.mTimeSpan = eventTime;
	}
	public void setEventID(int i){
		id = i;
	}

	
	public TimeSpan getNotification() {
		return eventReminder;
	}
	
	public TimeSpan getNextNotification() {
		switch (eventFrequency) {
		case ONETIME:
			return null;
		case DAILY:
			return eventReminder.daysAfter(1);
		case WEEKLY:
			return eventReminder.daysAfter(7);
		case MONTHLY:
			return eventReminder.monthsAfter(1);
		}
		return null;
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