package hkust.cse.calendar.unit;

import java.util.Date;

// Basic Nofitication model

public class Notification {

	private String title;
	private String information;
	private Date date; 
	
	public Notification(String _title, String _information, Date _date){
		title = _title;
		information = _information;
		date = _date; 
	}

	public String getTitle() {
		return title;
	}

	public String getInformation() {
		return information;
	}

	public Date getDate() {
		return date;
	}

}
