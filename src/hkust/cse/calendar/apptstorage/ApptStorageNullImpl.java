package hkust.cse.calendar.apptstorage;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import hkust.cse.calendar.unit.Appt;
import hkust.cse.calendar.unit.Event;
import hkust.cse.calendar.unit.TimeSpan;
import hkust.cse.calendar.unit.User;

public class ApptStorageNullImpl extends ApptStorage {
	
	private int interval = 100;
	private User defaultUser = null;
	
	public ApptStorageNullImpl( User user )
	{
		defaultUser = user;
	}
	
	public ApptStorageNullImpl(ApptStorageSQLImpl stor){
		defaultUser = stor.getDefaultUser();
		mAppts = new HashMap<Integer, Appt>();
		mNotifications = new ArrayList<TimeSpan>();
		pullFromDatabase(stor);
		
	}
	
	public void pullFromDatabase(ApptStorageSQLImpl stor){
		List<Appt> defaultUserApptList = new LinkedList<Appt>();
		defaultUserApptList = stor.getAllEvents(defaultUser.getID());
		
		for(int i = 0; i < defaultUserApptList.size(); ++i){
			Appt curr = defaultUserApptList.get(i);
			generateEvents(curr);
		}
	}
	
	public void generateEvents(Appt e){
		// Add notification in to notification array
		int time = -1;
		Appt pastEvent = null;
		switch (e.getEventFrequency()){
		case ONETIME:
			// save to db
			time = (int) e.getEventTime().StartTime().getTime();
			this.mAppts.put(time, e);
			
			// add a notification for the reminder if it exists
			if(e.getEventReminder() != null){
				// Add notification in to notification array
				addNotification(e.getNotification());
				System.out.println("Notification added successfully");
			}
			
			break;
		case WEEKLY:
			
			// save the first event
			time = (int) e.getEventTime().StartTime().getTime();
			this.mAppts.put(time, e);
			
			pastEvent = e;
			
			for (int i = 0; i < 52; i++)   { //1 years in weeks
				
				// Set teh current tim 
				TimeSpan curr = pastEvent.getEventTime();
				Timestamp start = new Timestamp(curr.StartTime().getTime()+604800000);
				Timestamp fin = new Timestamp(curr.EndTime().getTime()+604800000);
				
				// generate the new event
				Appt eNew = formatEvent(start, fin, e);
				time = (int) eNew.getEventTime().StartTime().getTime();
				// put into hashmap
				this.mAppts.put(time, eNew);
				
				// add a notification for the reminder if it exists
				if(eNew.getEventReminder() != null){
					// Add notification in to notification array
					addNotification(eNew.getNotification());
					System.out.println("Notification added successfully");
				}
				
				// set past event
				pastEvent = eNew;
				
			}
			break;
		case MONTHLY:
			// save the first event
			time = (int) e.getEventTime().StartTime().getTime();
			this.mAppts.put(time, e);
			
			pastEvent = e;

			for (int i = 0; i < 13; i++){   //1 years in groups of 4 weeks
				TimeSpan curr = pastEvent.getEventTime();
				
				Timestamp start = curr.StartTime(); 
				Timestamp end = curr.EndTime();
				
				start.setMonth(curr.StartTime().getMonth()+1);
				end.setMonth(curr.EndTime().getMonth()+1);
				
				// generate the new event
				Appt eNew = formatEvent(start, end, e);
				time = (int) eNew.getEventTime().StartTime().getTime();
				// put into hashmap
				this.mAppts.put(time, eNew);
				
				// add a notification for the reminder if it exists
				if(eNew.getEventReminder() != null){
					// Add notification in to notification array
					addNotification(eNew.getNotification());
					System.out.println("Notification added successfully");
				}
				
				// set past event
				pastEvent = eNew;
			}
			break;
		case DAILY:
			// save the first event
			time = (int) e.getEventTime().StartTime().getTime();
			this.mAppts.put(time, e);
			
			pastEvent = e;
			
			for (int i = 0; i < 365; i++){
				TimeSpan curr = pastEvent.getEventTime();
				Timestamp start = new Timestamp(curr.StartTime().getTime()+86400000);
				Timestamp end = new Timestamp(curr.EndTime().getTime()+86400000);
				// save to db
				
				// generate the new event
				Appt eNew = formatEvent(start, end, e);
				time = (int) eNew.getEventTime().StartTime().getTime();
				// put into hashmap
				this.mAppts.put(time, eNew);
				
				// add a notification for the reminder if it exists
				if(eNew.getEventReminder() != null){
					// Add notification in to notification array
					addNotification(eNew.getNotification());
					System.out.println("Notification added successfully");
				}
				
				// set past event
				pastEvent = eNew;
			}
			break;
		}
		
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
	public ApptStorageNullImpl(){
		super();
	}
	
	@Override
	public Appt getAppt(Timestamp t) {
		int time = (int) t.getTime();
		Appt newAppt = mAppts.get(time);
		return newAppt;
	}
	
	@Override
	public int SaveAppt(Appt appt) {
//		appt.generateID();
//		this.mAppts.put(appt.getID(), appt);
		generateEvents(appt);
		return appt.getID();
	}

	@Override
	public Appt[] RetrieveAppts(TimeSpan d) {
		ArrayList<Appt> applist = new ArrayList<Appt>();
		
		long start = d.StartTime().getTime();
		long end = d.EndTime().getTime();
		
		for (HashMap.Entry<Integer, Appt> entry : this.mAppts.entrySet())
		{
			Appt currentAppt = entry.getValue();
			long startTime = currentAppt.getEventTime().StartTime().getTime();
			long endTime = currentAppt.getEventTime().EndTime().getTime();
			if (startTime >= start && endTime <= end){
				System.out.println("key found");
				applist.add(currentAppt); 
				System.out.println("Yes . . ");
			}
		}
		
		System.out.println(applist.toString());
		
		if(applist.isEmpty())
			return new Appt[0];
		else{
			System.out.println("Returning now . . .");
			Appt[] newArray = new Appt[applist.size()];
			for(int j = 0; j < applist.size(); j++){
				newArray[j] = applist.get(j);
			}
			return newArray;
		}

	}

	@Override
	public Appt[] RetrieveAppts(User entity, TimeSpan time) {
		// TODO Auto-generated method stub
		return RetrieveAppts(time);
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
	public void RemoveAppt(Appt e) {

		int time = -1;
		Appt pastEvent = null;
		switch (e.getEventFrequency()){
		case ONETIME:
			// save to db
			time = (int) e.getEventTime().StartTime().getTime();
			this.mAppts.remove(time);
			break;
		case WEEKLY:
			
			// save the first event
			time = (int) e.getEventTime().StartTime().getTime();
			this.mAppts.remove(time);
			
			pastEvent = e;
			
			for (int i = 0; i < 52; i++)   { //1 years in weeks
				
				// Set teh current tim 
				TimeSpan curr = pastEvent.getEventTime();
				Timestamp start = new Timestamp(curr.StartTime().getTime()+604800000);
				Timestamp fin = new Timestamp(curr.EndTime().getTime()+604800000);
				
				// generate the new event
				Appt eNew = formatEvent(start, fin, e);
				time = (int) eNew.getEventTime().StartTime().getTime();
				// put into hashmap
				this.mAppts.remove(time);
				
				// set past event
				pastEvent = eNew;
				
			}
			break;
		case MONTHLY:
			// save the first event
			time = (int) e.getEventTime().StartTime().getTime();
			this.mAppts.remove(time);
			
			pastEvent = e;

			for (int i = 0; i < 13; i++){   //1 years in groups of 4 weeks
				TimeSpan curr = pastEvent.getEventTime();
				
				Timestamp start = curr.StartTime(); 
				Timestamp end = curr.EndTime();
				
				start.setMonth(curr.StartTime().getMonth()+1);
				end.setMonth(curr.EndTime().getMonth()+1);
				
				// generate the new event
				Appt eNew = formatEvent(start, end, e);
				time = (int) eNew.getEventTime().StartTime().getTime();
				// put into hashmap
				this.mAppts.remove(time);
				
				// set past event
				pastEvent = eNew;
			}
			break;
		case DAILY:
			// save the first event
			time = (int) e.getEventTime().StartTime().getTime();
			this.mAppts.remove(time);
			
			pastEvent = e;
			
			for (int i = 0; i < 365; i++){
				TimeSpan curr = pastEvent.getEventTime();
				Timestamp start = new Timestamp(curr.StartTime().getTime()+86400000);
				Timestamp end = new Timestamp(curr.EndTime().getTime()+86400000);
				// save to db
				
				// generate the new event
				Appt eNew = formatEvent(start, end, e);
				time = (int) eNew.getEventTime().StartTime().getTime();
				// put into hashmap
				this.mAppts.remove(time);
				
				// set past event
				pastEvent = eNew;
			}
			break;
		}
	}

	@Override
	public User getDefaultUser() {
		// TODO Auto-generated method stub
		return defaultUser;
	}

	@Override
	public void LoadApptFromXml() {
		// TODO Auto-generated method stub

	}

	@SuppressWarnings("deprecation")
	@Override
	public boolean isApptValid(Appt appt){ 
		for (Appt value: this.mAppts.values()){
			if (appt.TimeSpan().EndTime().getHours() < value.TimeSpan().StartTime().getHours()
					|| appt.TimeSpan().StartTime().getHours() > value.TimeSpan().EndTime().getHours()){
				return true;  //no overlap in hours
			}
			else if (appt.TimeSpan().EndTime().getHours() == value.TimeSpan().StartTime().getHours()){
				if (appt.TimeSpan().EndTime().getMinutes() < value.TimeSpan().StartTime().getMinutes()){
					return true;  //no overlap in mins
				}
			}
			else if (appt.TimeSpan().StartTime().getHours() == value.TimeSpan().EndTime().getHours()){
				if (appt.TimeSpan().StartTime().getMinutes() > value.TimeSpan().EndTime().getMinutes()){
					return true;   //no overlap in mins
				}
			}
		}
		return false;
	}
	
	@Override
	public boolean findNotification(TimeSpan ts){
		return mNotifications.contains(ts);
	}
	@Override
	public void addNotification(TimeSpan ts) {
		// TODO Auto-generated method stub
		if (findNotification(ts)) return;
		mNotifications.add(ts);
		mergeNotification();
	}

	@Override
	public void deleteNotification(TimeSpan ts) {
		// TODO Auto-generated method stub
		mNotifications.remove(ts);
	}

	@Override
	public void mergeNotification() {
		// TODO Auto-generated method stub
		Collections.sort(mNotifications, new Comparator<TimeSpan>(){
			public int compare(TimeSpan t1, TimeSpan t2){
				return (int) ((t1.StartTime().getTime() - t2.StartTime().getTime())/60000);
			}
		});
	}

	@Override
	public TimeSpan getNotification() {
		// TODO Auto-generated method stub
		if(isNotificationEmpty())
			return null;
		
		return mNotifications.get(0);
	}

	@Override
	public boolean isNotificationEmpty() {
		// TODO Auto-generated method stub
		if(mNotifications.size() == 0)return true;
		return false;
	}

	@Override
	public Appt getAppt(int id) {
		// TODO Auto-generated method stub
		return mAppts.get(id);
	}
	

}
