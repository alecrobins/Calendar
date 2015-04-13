package calendar;

import static org.junit.Assert.*;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;

import hkust.cse.calendar.controllers.EventController;
import hkust.cse.calendar.controllers.EventController.EventReturnMessage;
import hkust.cse.calendar.controllers.NotificationController;
import hkust.cse.calendar.unit.Event;
import hkust.cse.calendar.unit.Location;
import hkust.cse.calendar.unit.TimeSpan;
import hkust.cse.calendar.unit.Event.Frequency;

import org.junit.Test;

public class EventControllerTest {
	
	private NotificationController testNC = new NotificationController();
	
//	@Test
//	public void testEventCreation() {
//		EventController test = new EventController(testNC, null);		
//
//		EventReturnMessage returnEvent = test.createEvent(
//				"2015", "04", "31", 
//				"08", "00", "09","00",
//				"This is the detail area", "Title here", 
//				"2015", "04", "30", "08", "00",
//				"DAILY", null, null);
//
//		assertEquals(EventReturnMessage.SUCCESS, returnEvent);
//	}
	
	@Test
	public void testEventFailureSecondDatePast() {
		EventController test = new EventController(testNC, null);		

		EventReturnMessage returnEvent = test.createEvent(
				"2015", "04", "31",
				"09", "00", "08","00", 
				"This is the detail area", "Title here", 
				"2015", "04", "30", "08", "00",
				"DAILY", null, null);
		
		System.out.println(returnEvent);

		assertEquals(EventReturnMessage.ERROR_SECOND_DATE_PAST, returnEvent);
	}
	
	@Test
	public void testEventFailurePastDate() {
		EventController test = new EventController(testNC, null);		

		EventReturnMessage returnEvent = test.createEvent(
				"2015", "02", "31",
				"09", "00", "08","00", 
				"This is the detail area", "Title here", 
				"2015", "04", "30", "08", "00",
				"DAILY", null, null);
		
		System.out.println(returnEvent);

		assertEquals(EventReturnMessage.ERROR_PAST_DATE, returnEvent);
	}
	
	@Test
	public void testEventFailureUnfilledRequirements() {
		EventController test = new EventController(testNC, null);		

		EventReturnMessage returnEvent = test.createEvent(
				"2015", "02", "31",
				"09", "00", null,null,
				"2015", "04", "30", "08", "00",
				"This is the detail area", "Title here",
				"DAILY", null, null);
		
		System.out.println(returnEvent);

		assertEquals(EventReturnMessage.ERROR_UNFILLED_REQUIRED_FIELDS, returnEvent);
	}
	
//	@Test
//	public void testEventReminder() throws InterruptedException {
//		EventController test = new EventController(testNC);		
//		
//		// Set a reminder to go off in 10 seconds
//		Date reminder = new Date();
//		reminder.setSeconds(reminder.getSeconds() + 10);
//		
//		EventReturnMessage returnEvent = test.createEvent(
//				"2015", "04", "31",
//				"08", "00", "09","00",
//				"2015", "04", "30", "08", "00",
//				"This is the detail area", "Brand New Title here", 
//				"DAILY", null, null);
//
//		System.out.println(returnEvent);
//		
//		Thread.sleep(12000);
//
//		assertEquals(EventReturnMessage.SUCCESS, returnEvent);
//	}

}
