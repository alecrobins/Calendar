package hkust.cse.calendar.apptstorage;

import java.util.ArrayList;

import hkust.cse.calendar.unit.Appt;
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
		int apptId = appt.getID();
		// try to remove the appt from the db
		try{
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
	

}