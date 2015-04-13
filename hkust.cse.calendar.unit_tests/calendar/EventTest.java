package calendar;

import static org.junit.Assert.*;
import hkust.cse.calendar.unit.Event;
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

}
