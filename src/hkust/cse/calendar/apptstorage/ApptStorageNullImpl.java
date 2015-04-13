package hkust.cse.calendar.apptstorage;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import hkust.cse.calendar.unit.Appt;
import hkust.cse.calendar.unit.Event;
import hkust.cse.calendar.unit.Event.Frequency;
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

	@Override
	public boolean IsApptValid(Appt appt, List<Appt> applicable) {
		for (Appt value : applicable){ // need to specify for just a group of appts
		int startH = value.TimeSpan().StartTime().getHours();
		int endH = value.TimeSpan().EndTime().getHours();
		int startM = value.TimeSpan().StartTime().getMinutes();
		int endM = value.TimeSpan().EndTime().getMinutes();
		int currStartH = appt.TimeSpan().StartTime().getHours();
		int currEndH = appt.TimeSpan().StartTime().getHours();
		int currStartM = appt.TimeSpan().StartTime().getMinutes();
		int currEndM = appt.TimeSpan().StartTime().getMinutes();
		
		System.out.println("start hour for birthday: "+startH);
		System.out.println("end hour: "+endH);
		
		System.out.println("start hour for yolo: "+currStartH);
		System.out.println("end hour: "+currEndH);
		
		if (currStartH >= endH && currStartH < startH || currStartH >= startH && currEndH <= startH
				|| currStartH < startH && currEndH > endH){
			if (currStartM >= endM && currStartM < startM || currStartM >= startM && currEndM <= startM
					|| currStartM < startM && currEndM > endM){
				return false;
				}
			}
		}

		return true;
	}
	
//	public boolean isApptValid(Appt appt, List<Appt> applicable){
//		for (Appt value : applicable){ // need to specify for just a group of appts
//			int startH = value.TimeSpan().StartTime().getHours();
//			int endH = value.TimeSpan().EndTime().getHours();
//			int startM = value.TimeSpan().StartTime().getMinutes();
//			int endM = value.TimeSpan().EndTime().getMinutes();
//			int currStartH = appt.TimeSpan().StartTime().getHours();
//			int currEndH = appt.TimeSpan().StartTime().getHours();
//			int currStartM = appt.TimeSpan().StartTime().getMinutes();
//			int currEndM = appt.TimeSpan().StartTime().getMinutes();
//			
//			System.out.println("start hour for birthday: "+startH);
//			System.out.println("end hour: "+endH);
//			
//			System.out.println("start hour for yolo: "+currStartH);
//			System.out.println("end hour: "+currEndH);
//			
//			if (currStartH >= endH && currStartH < startH || currStartH >= startH && currEndH <= startH
//					|| currStartH < startH && currEndH > endH){
//				if (currStartM >= endM && currStartM < startM || currStartM >= startM && currEndM <= startM
//						|| currStartM < startM && currEndM > endM){
//					return false;
//				}
//				}
//		}
//
//		return true;
//	
//	}
}