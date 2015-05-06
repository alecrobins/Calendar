package hkust.cse.calendar.gui;

import hkust.cse.calendar.apptstorage.ApptStorageControllerImpl;
import hkust.cse.calendar.apptstorage.ApptStorageSQLImpl;
import hkust.cse.calendar.unit.Appt;
import hkust.cse.calendar.unit.TimeSpan;
import hkust.cse.calendar.unit.User;

import java.awt.BorderLayout;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;

public class test {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		//TOFIX:  make columns a good width, make borders between columns and rows, 
		//make popup size nice and center the popup, color in available times, 
		//make method to identify available times (need alec to finish database)
		
		
		
		List<Appt> appList = new LinkedList<Appt>();
		Timestamp a = new Timestamp(0);
		for (int m = 0; m < 5; m++){
		Appt standard = new Appt();
		Timestamp b = new Timestamp(a.getTime() + 900000*10);
		TimeSpan d = new TimeSpan(a, b);
		standard.setTimeSpan(d);
		appList.add(standard);
		a = new Timestamp(b.getTime() + 900000*16);  //4 hours later 
		}
		
		HashMap<User, List<Appt>> userMap = new HashMap<User, List<Appt>>();
		User us = new User("poop","poopie");
		userMap.put(us, appList);
		
		List<Timestamp> dates = new LinkedList<Timestamp>();
		dates.add(new Timestamp(0));
		dates.add(new Timestamp(86400000));
		
		boolean[] rowBool = new boolean[0];
		
		User user = new User("popp", "poop");
		List<User> lis = new LinkedList<User>();
		lis.add(user);
		ApptStorageSQLImpl sql = new ApptStorageSQLImpl(user);
		ApptStorageControllerImpl as = new ApptStorageControllerImpl(sql);
		CalGrid cal = new CalGrid(as);
		
		MultipleUserSchedule mus = new MultipleUserSchedule("Invitee", cal, userMap, dates);

		
		
		
	}
}
