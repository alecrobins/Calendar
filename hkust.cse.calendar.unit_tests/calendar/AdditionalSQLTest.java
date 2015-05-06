package calendar;

import static org.junit.Assert.*;
import hkust.cse.calendar.apptstorage.ApptStorageSQLImpl;
import hkust.cse.calendar.unit.Appt;
import hkust.cse.calendar.unit.User;

import org.junit.Test;

public class AdditionalSQLTest {
	private ApptStorageSQLImpl db;
	//TODO: eventually switch out dummyUser with the default User 
	private User dummyUser = new User(4, "alecrobins", "1", true);
	
	public AdditionalSQLTest(){
		db = new ApptStorageSQLImpl(dummyUser);
	}
	
	@Test
	public void testGetEvent(){
		Appt newEvent = db.getAppt(44);
		newEvent.toString();
	}
	
	

}
