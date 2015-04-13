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

	
	public static Clock getInstance(){
		return new Clock();
	}
	
	/** One day in advance of the actual time. NOTE: TIME not DATE*/
	public static long currentTimeMillis() {
		Date d = new Date();
		long timeDiff = d.getTime() + ClockConstants.TIME_CHANGE;
	    return timeDiff;
	  }
	  	
	// this will return a new date with regard to time_change 
	public Date newDate() {
		Date d = new Date(currentTimeMillis());
		return d;
	}
	
	// set the calendar to the current date
	public Calendar newCalendar() {
		Calendar c = Calendar.getInstance();
		Date d = newDate();
		c.set(d.getYear() + 1900, d.getMonth(), d.getDay(), d.getHours(), d.getMinutes(), d.getSeconds());
		return c;
	}
	
	// set the calendar to the current date
	public GregorianCalendar newGregorianCalendar() {
		GregorianCalendar c = new GregorianCalendar();
		Date d = newDate();
		c.set(d.getYear() + 1900, d.getMonth(), d.getDay(), d.getHours(), d.getMinutes(), d.getSeconds());
		c.setTime(newDate());
		return c;
	}
	
	// return what ever the time is now
	public long now() {
		return currentTimeMillis();
	}
	
	// given a date it will change the clock so 
	// that whatever the passed in date is the TIME_CHANGE
	// will take effect
	public void setDate(Date d){
		ClockConstants.SET_DATE = d;
		
		Date now = new Date();
		// find the difference between the set date and today
		// then set the differnce of the dates to TIME_CHANGE
		long difference = d.getTime() - now.getTime();
		ClockConstants.TIME_CHANGE = difference;
	}

}

