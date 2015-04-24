package calendar;

import static org.junit.Assert.*;

import java.sql.Timestamp;
import java.util.HashMap;

import hkust.cse.calendar.apptstorage.ApptStorage;
import hkust.cse.calendar.apptstorage.ApptStorageControllerImpl;
import hkust.cse.calendar.apptstorage.ApptStorageNullImpl;
import hkust.cse.calendar.controllers.EventController;
import hkust.cse.calendar.gui.CalGrid;
import hkust.cse.calendar.unit.Appt;
import hkust.cse.calendar.unit.Event;
import hkust.cse.calendar.unit.TimeSpan;
import hkust.cse.calendar.unit.Event.Frequency;

import org.junit.Test;

public class EventTest {

	@Test
	public void testAssertEventWorks() {
		Event e = new Event();
		String eventDescription = e.getEventDescription();
		assertNull(eventDescription, null);
	}
	
	@Test
	public void testAssertSetterWorkds() {
		Event e = new Event();
		e.setAdditionalEventDescription("This is a test event");
		assertSame(e.getAdditionalEventDescription(), "This is a test event");
	}
	
	@Test
	public void testAssertEventFrequency() {
		Event e = new Event();
		e.setEventFrequency(Frequency.ONETIME);
		assertSame(e.getEventFrequency(), Frequency.ONETIME);
	}
	
	@SuppressWarnings("deprecation")
	@Test
	public void testAssertEventOverlap() {
		
		HashMap<Integer, Appt> map = new HashMap<Integer, Appt>();
		
		long start = Timestamp.UTC(2015, 4, 25, 8, 0, 0);
		long finish = Timestamp.UTC(2015, 4, 25, 10, 0, 0);
		Timestamp a = new Timestamp(start);
		Timestamp b = new Timestamp(finish);
		TimeSpan c = new TimeSpan(a,b);
		Event e = new Event(c, Frequency.ONETIME);
		
		System.out.println(e.TimeSpan().StartTime().getTime());
		
		//so the problem is that only one event is added, in this case two are with the same id = 0 (weird?)
		Event eNew2 = e;
		for (int i = 0; i < 1825; i++){
			Event eNew3 = new Event(eNew2.getEventTime(), eNew2.getEventFrequency()) ;
			map.put(i, eNew3);
			TimeSpan curr = eNew2.getEventTime();
			Timestamp start1 = new Timestamp(curr.StartTime().getTime()+86400000);
			Timestamp fin = new Timestamp(curr.EndTime().getTime()+86400000);
			eNew2.setEventTime(new TimeSpan(start1, fin));
		}
		
		for (Appt val: map.values()){
			System.out.println(val.TimeSpan().StartTime().getTime());
		}
	}


}
