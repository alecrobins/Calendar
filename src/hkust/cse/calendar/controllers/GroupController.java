package hkust.cse.calendar.controllers;

import hkust.cse.calendar.gui.CalGrid;
import hkust.cse.calendar.gui.MultipleUserSchedule;
import hkust.cse.calendar.unit.Appt;
import hkust.cse.calendar.unit.Event;
import hkust.cse.calendar.unit.User;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;

public class GroupController {
	private MultipleUserSchedule mus;
	
	//need to use mus to pick
	public GroupController(CalGrid grid, HashMap<User, List<Appt>> uMap, List<Timestamp> d){
		mus = new MultipleUserSchedule(uMap, d);
		mus.setParent(grid);
	}
	
	
	public void createGroupEvent(){
	}
	
	public void sendConfirmation(){
	}
	
	public boolean isConfirmed(){
		return false;
	}
	
	public boolean isGroupEventValid(Event e){
		return false;
	}
}
