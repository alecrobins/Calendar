package hkust.cse.calendar.apptstorage;


import java.sql.Timestamp;

import hkust.cse.calendar.unit.Appt;
import hkust.cse.calendar.unit.TimeSpan;
import hkust.cse.calendar.unit.User;

/* This class is for managing the Appt Storage according to different actions */
public class ApptStorageControllerImpl {

	/* Remove the Appt from the storage */
	public final static int REMOVE = 1;

	/* Modify the Appt the storage */
	public final static int MODIFY = 2;

	/* Add a new Appt into the storage */
	public final static int NEW = 3;
	
	/*
	 * Add additional flags which you feel necessary
	 */
	
	/* The Appt storage */
	public ApptStorage mApptStorage;
	public ApptStorageSQLImpl stor;

	/* Create a new object of ApptStorageControllerImpl from an existing storage of Appt */
	
//	public ApptStorageControllerImpl(ApptStorage storage) {
//		mApptStorage = storage;
//	}
	public ApptStorageSQLImpl getDatabase(){
		return stor;
	}
	
	public ApptStorageControllerImpl(ApptStorageSQLImpl storage){
		stor = storage;
		mApptStorage = new ApptStorageNullImpl(storage);
	}
	

	/* Retrieve the Appt's in the storage for a specific user within the specific time span */
	public Appt[] RetrieveAppts(User entity, TimeSpan time) {
		return mApptStorage.RetrieveAppts(entity, time);
	}

	// overload method to retrieve appointment with the given joint appointment id
	public Appt RetrieveAppts(int joinApptID) {
		return mApptStorage.RetrieveAppts(joinApptID);
	}
	
	// Get appointment by its start time
	public Appt getAppt(Timestamp t) {
		return mApptStorage.getAppt(t);
	}
	
	/* Manage the Appt in the storage
	 * parameters: the Appt involved, the action to take on the Appt */
	public void ManageAppt(Appt appt, int action) {

		if (action == NEW) {				// Save the Appt into the storage if it is new and non-null
			if (appt == null)
				return;
			mApptStorage.SaveAppt(appt);
		} else if (action == MODIFY) {		// Update the Appt in the storage if it is modified and non-null
			if (appt == null)
				return;
			mApptStorage.UpdateAppt(appt);
		} else if (action == REMOVE) {		// Remove the Appt from the storage if it should be removed
			mApptStorage.RemoveAppt(appt);
		} 
	}

	/* Get the defaultUser of mApptStorage */
	public User getDefaultUser() {
		return mApptStorage.getDefaultUser();
	}

	// method used to load appointment from xml record into hash map
	public void LoadApptFromXml(){
		mApptStorage.LoadApptFromXml();
	}
	
	// method used to see if appt is valid
	public boolean isApptValid(Appt appt){
		return mApptStorage.isApptValid(appt);
	}
	
	// method used to check the existence of a notification
	public boolean findNotification(TimeSpan ts){
		return mApptStorage.findNotification(ts);
	}
	// method used to store notification time
	public void addNotification(TimeSpan ts){
		mApptStorage.addNotification(ts);
	}
	
	// method used to delete notification time
	public void deleteNotification(TimeSpan ts){
		mApptStorage.deleteNotification(ts);
	}
	
	// method used to get notification time
	public TimeSpan getNotification(){
		return mApptStorage.getNotification();
	}
	
	// method used to check whether there is a notification
	public boolean isNotificationEmpty() {
		return mApptStorage.isNotificationEmpty();
	}
	
	// method used to get an appointment by its id
	public Appt getAppt(int id){
		return mApptStorage.getAppt(id);
	}
}
