package hkust.cse.calendar.unit;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;


// This class will replace all the Date/Calendar functions
// The purpose of this class is to make it easy to test the calendar

// This class uses the singleton pattern to initialize a clock

// keep track of the amount of time that you want to change in miliseconds 
// default to the regular system clock  time 

public class Clock {
	
	// Stored the time differences
	private long timeDiff;

	// Constructor of Clock
	public Clock(){
		
		timeDiff = 0;
		
	}
	
	// Return the time after added the time difference
	public GregorianCalendar getChangedTime(){
		Date d = new Date();
		d.setTime(d.getTime() + timeDiff);
		GregorianCalendar c = new GregorianCalendar(d.getYear() + 1900, d.getMonth(), d.getDate(), d.getHours(), d.getMinutes(), d.getSeconds());
		System.out.println(d.getYear());
		System.out.println(d.getMonth());
		System.out.println(d.getDate());
		return c;
	}
	
	// Return the time after added the time difference in Date type
	public Date getChangedTimeDate(){
		Date d = new Date();
		d.setTime(d.getTime() + timeDiff);
		return d;
	};
	
	// Return the unchanged time
	public GregorianCalendar getUnchangedTime(){
		GregorianCalendar c = new GregorianCalendar();
		return c;
	};
	
	// Change time, store the time difference in timeDiff
	public void changeTimeTo(int Y, int Mo, int D, int H, int Mi, int S){
		Date d = new Date(Y, Mo, D, H, Mi, S);
		Date currD = new Date();
		timeDiff = d.getTime() - currD.getTime();
	};
	


}

