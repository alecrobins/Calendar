package hkust.cse.calendar.apptstorage;//

import hkust.cse.calendar.unit.Appt;
import hkust.cse.calendar.unit.TimeSpan;
import hkust.cse.calendar.unit.User;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;


public abstract class ApptStorage {

	public HashMap<Integer, Appt> mAppts;		//a hashmap to save every thing to it, write to memory by the memory based storage implementation	
	public ArrayList<TimeSpan> mNotifications;	//an arraylist to store notifications	
	public User defaultUser;	//a user object, now is single user mode without login
	public int mAssignedApptID;	//a global appointment ID for each appointment record

	public HashMap<Integer, Appt> getApptsMap(){
		return mAppts;
	}
	
	public ApptStorage() {	//default constructor
		mAppts = new HashMap<Integer,Appt>();
		mNotifications = new ArrayList<TimeSpan>();
	}

	public abstract Appt getAppt(Timestamp t);	//abstract method to get an appointment by its starting time
	
	public abstract int SaveAppt(Appt appt);	//abstract method to save an appointment record

	public abstract Appt[] RetrieveAppts(TimeSpan d);	//abstract method to retrieve an appointment record by a given timespan

	public abstract Appt[] RetrieveAppts(User entity, TimeSpan time);	//overloading abstract method to retrieve an appointment record by a given user object and timespan
	
	public abstract Appt RetrieveAppts(int joinApptID);					// overload method to retrieve appointment with the given joint appointment id

	public abstract void UpdateAppt(Appt appt);	//abstract method to update an appointment record

	public abstract void RemoveAppt(Appt appt);	//abstract method to remove an appointment record
	
	public abstract User getDefaultUser();		//abstract method to return the current user object
	
	public abstract void LoadApptFromXml();		//abstract method to load appointment from xml reocrd into hash map
	
	public abstract boolean isApptValid(Appt appt);
	
	public abstract boolean findNotification(TimeSpan ts);	//abstract method to check the existence of a notification
	
	public abstract void addNotification(TimeSpan ts);	//abstract method to add a new notification
	
	public abstract void deleteNotification(TimeSpan ts);	//abstract method to delete a notification
	
	public abstract void mergeNotification();	//abstract method to merge notification list
	
	public abstract TimeSpan getNotification();	//abstract method to get notification

	public abstract boolean isNotificationEmpty();	//abstract method to check whether there is a reminder
	
	public abstract Appt getAppt(int id);	//abstract method to get an appointment by its id
	
	/*
	 * Add other methods if necessary
	 */



}
