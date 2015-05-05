package calendar;

import static org.junit.Assert.*;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import hkust.cse.calendar.apptstorage.ApptStorageNullImpl;
import hkust.cse.calendar.unit.Appt;
import hkust.cse.calendar.unit.Clock;
import hkust.cse.calendar.unit.Event;
import hkust.cse.calendar.unit.Location;
import hkust.cse.calendar.unit.TimeSpan;
import hkust.cse.calendar.unit.Appt.Frequency;

import org.junit.Test;

public class TestStorage {

	
	@Test
	public void testApptStorageNullImpl() throws ParseException {
		ApptStorageNullImpl a = new ApptStorageNullImpl();
		
		Appt test = new Appt();
		Appt test2 = new Appt();
		
		DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
		Date date = dateFormat.parse("23/04/2015");
		Date date2 = dateFormat.parse("24/04/2015");
		long time = date.getTime();
		long time2 = date2.getTime();
		Timestamp t1 = new Timestamp(time);
		Timestamp t2 = new Timestamp(time2);
		TimeSpan t = new TimeSpan(t1,t2);
		
		test.setTitle("Test Title 1");
		test.setInfo("Test info 1");
		test.setTimeSpan(t);
		test.generateID();
		
		Date gdate = dateFormat.parse("27/04/2015");
		Date gdate2 = dateFormat.parse("28/04/2015");
		long gtime = gdate.getTime();
		long gtime2 = gdate2.getTime();
		Timestamp gt1 = new Timestamp(gtime);
		Timestamp gt2 = new Timestamp(gtime2);
		TimeSpan gt = new TimeSpan(gt1,gt2);
		test2.setTitle("Test Title 1");
		test2.setInfo("Test info 1");
		test2.setTimeSpan(gt);
		test2.generateID();
		
		assertTrue(a.mAppts.size() == 0);
		a.SaveAppt(test);
		assertTrue(a.mAppts.size() == 1);
		a.SaveAppt(test2);
		assertTrue(a.mAppts.size() == 2);
		
		a.RemoveAppt(test);
		assertTrue(a.mAppts.size() == 1);
		
		Date cdate = dateFormat.parse("21/04/2015");
		Date cdate2 = dateFormat.parse("25/04/2015");
		long ctime = cdate.getTime();
		long ctime2 = cdate2.getTime();
		Timestamp ct1 = new Timestamp(ctime);
		Timestamp ct2 = new Timestamp(ctime2);
		TimeSpan ct = new TimeSpan(ct1,ct2);
		
		Appt[] as = a.RetrieveAppts(ct);
		
		System.out.println(as.toString());
		
		assertTrue(as.length == 1);
		assertEquals(as, test);
	}

}
