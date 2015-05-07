package hkust.cse.calendar.unit;

import java.util.List;

public class Invite {
	
	private List<TimeSpan> timeslots;
	private List<User> users;
	
	public Invite() {
		setTimeslots(null);
		setUsers(null);
	}
	
	public Invite(List<TimeSpan> _timeslots, List<User> _users){
		setTimeslots(_timeslots);
		setUsers(_users);
	}

	public List<TimeSpan> getTimeslots() {
		return timeslots;
	}

	public void setTimeslots(List<TimeSpan> timeslots) {
		this.timeslots = timeslots;
	}

	public List<User> getUsers() {
		return users;
	}

	public void setUsers(List<User> users) {
		this.users = users;
	}
	
}
