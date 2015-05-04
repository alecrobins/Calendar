package calendar;

import static org.junit.Assert.*;

import java.sql.Timestamp;

import hkust.cse.calendar.apptstorage.ApptStorageSQLImpl;
import hkust.cse.calendar.unit.Event;
import hkust.cse.calendar.unit.Location;
import hkust.cse.calendar.unit.TimeSpan;
import hkust.cse.calendar.unit.Event.Frequency;

import org.junit.Test;

public class ApptSQLTests {
	
	private ApptStorageSQLImpl db;
	
	public ApptSQLTests(){
		db = new ApptStorageSQLImpl();
	}
	
	@Test
	public void testGetAppt() {
		
		Timestamp t1 = new Timestamp(1600);
		Timestamp t2 = new Timestamp(600);
		Timestamp tFail = new Timestamp(700);
		
		Event good = (Event) db.getAppt(t1);
		Event badUser = (Event) db.getAppt(t2);
		
		Event badEvent = (Event) db.getAppt(tFail);
		
		assertNull(badUser);
		assertNull(badEvent);		
		
	}
	
	@Test
	public void testGetLocation(){
		Location good = db.getLocation(1);
		Location bad = db.getLocation(100);
		
		assert(good != null);
		assertNull(bad);
	}
	
	@Test
	public void testSaveAppt(){
		
		Timestamp t1 = new Timestamp(3400);
		Timestamp t2 = new Timestamp(8600);
		TimeSpan eventTime = new TimeSpan(t1, t2);
		
		Timestamp r1 = new Timestamp(1400);
		Timestamp r2 = new Timestamp(3400);
		TimeSpan eventReminder = new TimeSpan(r1, r2);
		
		String title = "This is a title from the test";
		String description = "hello this is a description test";
		String addDescription = "additional goes here . . ";
		int eventLocationID = 1; 
		Frequency f = Frequency.WEEKLY;
		
		Event testEvent = new Event(eventTime, title, description, eventLocationID,
				eventReminder, addDescription, f);
		
		db.SaveAppt(testEvent);
		
	}

}
