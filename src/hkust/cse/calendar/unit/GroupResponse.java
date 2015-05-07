package hkust.cse.calendar.unit;

import java.sql.Timestamp;
import java.util.List;

public class GroupResponse {
	
	private User intiator;
	private List<TimeSpan> proposedTimeslots;
	private List<TimeSpan> selectedTimeslots;
	boolean isRejected;
	private int groupID; // points to the 
	
	public GroupResponse(){
		intiator = null;
		proposedTimeslots = null;
		selectedTimeslots = null;
		isRejected = false;
		groupID = -1;
	}
	
	public GroupResponse(User _intiator, List<TimeSpan> _proposedTimeslots, 
						 List<TimeSpan> _selectedTimeslots, boolean _isRejected, int _groupID){
		intiator = _intiator;
		proposedTimeslots = _proposedTimeslots;
		selectedTimeslots = _selectedTimeslots;
		isRejected = _isRejected;
		groupID = _groupID;
	}

	public boolean getIsRegjected(){
		return isRejected;
	}
	
	public void setIsRejected(boolean _isRejected){
		isRejected = _isRejected;
	}

	public User getIntiator() {
		return intiator;
	}

	public void setIntiator(User intiator) {
		this.intiator = intiator;
	}

	public List<TimeSpan> getProposedTimeslots() {
		return proposedTimeslots;
	}

	public void setProposedTimeslots(List<TimeSpan> proposedTimeslots) {
		this.proposedTimeslots = proposedTimeslots;
	}

	public List<TimeSpan> getSelectedTimeslots() {
		return selectedTimeslots;
	}

	public void setSelectedTimeslots(List<TimeSpan> selectedTimeslots) {
		this.selectedTimeslots = selectedTimeslots;
	}

	public int getGroupID() {
		return groupID;
	}

	public void setGroupID(int groupID) {
		this.groupID = groupID;
	}
	
}