package calendar;

import static org.junit.Assert.*;

import java.sql.Timestamp;

import hkust.cse.calendar.apptstorage.ApptStorage;
import hkust.cse.calendar.apptstorage.ApptStorageControllerImpl;
import hkust.cse.calendar.apptstorage.ApptStorageNullImpl;
import hkust.cse.calendar.controllers.EventController;
import hkust.cse.calendar.gui.CalGrid;
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
		
		
		
		long start = Timestamp.UTC(2015, 4, 25, 8, 0, 0);
		long finish = Timestamp.UTC(2015, 4, 25, 10, 0, 0);
		Timestamp a = new Timestamp(start);
		Timestamp b = new Timestamp(finish);
		TimeSpan c = new TimeSpan(a,b);
		Event e = new Event(c, b, Frequency.ONETIME);
		
		long start1 = Timestamp.UTC(2015, 4, 25, 9, 0, 0);
		long finish1 = Timestamp.UTC(2015, 4, 25, 10, 0, 0);
		Timestamp a1 = new Timestamp(start);
		Timestamp b1 = new Timestamp(finish);
		TimeSpan c1 = new TimeSpan(a,b);
		Event e1 = new Event(c1, b1, Frequency.ONETIME);
		
		long oneWeek = Timestamp.UTC(70, 0, 8, 0, 0, 0);
		System.out.println(604800000/7.0);
	//	assertSame(hkust.cse.calendar.controllers.EventController.eventOverlap(e1, HashMap<Integer, Appt> as))
		

	}


}
