package hkust.cse.calendar.apptstorage;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import hkust.cse.calendar.unit.Appt;
import hkust.cse.calendar.unit.Event;
import hkust.cse.calendar.unit.TimeSpan;
import hkust.cse.calendar.unit.User;

public class ApptStorageNullImpl extends ApptStorage {
	
	private int interval = 60;
	private User defaultUser = null;
	
	public ApptStorageNullImpl( User user )
	{
		defaultUser = user;
	}
	
	public ApptStorageNullImpl(){
		super();
	}
	
	@Override
	public Appt getAppt(Timestamp t) {
		return mAppts.get((int) t.getTime());
	}
	
	@Override
	public void SaveAppt(Appt appt) {
		appt.generateID();
		this.mAppts.put(appt.getID(), appt);
	}

	@Override
	public Appt[] RetrieveAppts(TimeSpan d) {
		ArrayList<Appt> applist = new ArrayList<Appt>();
		
		long start = d.StartTime().getTime();
		long end = d.EndTime().getTime();
		
		for (int i = (int) start; i < (int) end; i+=interval){
			if (mAppts.containsKey(i)){
				System.out.println("key found");
				applist.add((Appt)this.mAppts.get(i)); 
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
	public void RemoveAppt(Appt appt) {
		//mAppts.put(appt.getID(), null);
		Event event = (Event) appt;
		int apptId = appt.getID();
		// try to remove the appt from the db
		try{
			deleteNotification(event.getNotification());
			if (event.getEventFrequency() != Event.Frequency.DAILY) deleteNotification(event.getNextNotification());
			mAppts.remove(apptId);
		}catch(Exception e){
			System.out.println("ERROR");
			System.out.println(e.getMessage());
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
		if(isNotificationEmpty())return null;
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