package calendar;

import static org.junit.Assert.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;

import hkust.cse.calendar.unit.Clock;

import org.junit.Test;

public class ClockTest {

	@Test
	public void basicClockTest() {
		Clock c = Clock.getInstance();
		Date testDate = c.newDate();
		System.out.println(testDate.toString());
		
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		Date d;
		try {
			
			d = sdf.parse("21/12/2015");
			c.setDate(d);
		
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
//	
//	@Test
//	public void testClockTime() throws InterruptedException {
//		Clock c = Clock.getInstance();
//	
//		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
//		Date d;
//		try {
//			
//			d = sdf.parse("21/12/2015");
//			c.setDate(d);
//		
//		} catch (ParseException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		
//		Date earlyDate = c.newDate();
//		
//		Thread.sleep(5000);
//		
//		Date laterDate = c.newDate();
//		
//		long difference = laterDate.getSeconds() - earlyDate.getSeconds();
//		System.out.println("Differnece of time shoudl be 5 sec: " + difference);
//		
//		assertEquals(difference, 5);
//		
//	}
	
	@Test 
	public void testcockCurrentTimeMillis(){
		long diff = Clock.getInstance().currentTimeMillis();
		System.out.println(diff);
		System.out.println(System.currentTimeMillis());
		assertNotEquals(diff, System.currentTimeMillis());
	}
	
	@Test 
	public void testDifferentTime(){
		Date now = Clock.getInstance().newDate();
		Date newDate = new Date();
		System.out.println("clock date: " + now.toString());
		System.out.println("reg date: " + newDate.toString());
		
		assertNotEquals(now, newDate);
	}
	
	@Test 
	public void testCalendars(){
		GregorianCalendar gNew = Clock.getInstance().newGregorianCalendar();
		GregorianCalendar gNow = new GregorianCalendar();
		System.out.println("clock gNew: " + gNew.toString());
		System.out.println("reg gNow: " + gNow.toString());
		assertNotEquals(gNew, gNow);
	}

}
