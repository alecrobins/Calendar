package hkust.cse.calendar.unit;

import java.sql.Timestamp;
import java.util.List;

public class GroupResponse {
	
	private List<User> userList;
	private List<Timestamp> dates;
	private List<TimeSpan> responses;
	boolean isRejected;
	
	public GroupResponse(List<User> _userList, List<Timestamp> _dates, List<TimeSpan> _responses, boolean _isRejected){
		setUserList(_userList);
		setDates(_dates);
		setResponses(_responses);
		isRejected = _isRejected;
	}

	public boolean getIsRegjected(){
		return isRejected;
	}
	
	public void setIsRejected(boolean _isRejected){
		isRejected = _isRejected;
	}
	
	public List<User> getUserList() {
		return userList;
	}

	public void setUserList(List<User> userList) {
		this.userList = userList;
	}

	public List<Timestamp> getDates() {
		return dates;
	}

	public void setDates(List<Timestamp> dates) {
		this.dates = dates;
	}

	public List<TimeSpan> getResponses() {
		return responses;
	}

	public void setResponses(List<TimeSpan> responses) {
		this.responses = responses;
	}
}