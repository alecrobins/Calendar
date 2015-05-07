package hkust.cse.calendar.unit;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class GroupResponse {
	
	private int intiatorID;
	private List<TimeSpan> proposedTimeslots;
	private List<TimeSpan> selectedTimeslots;
	boolean isRejected;
	private int groupID; // points to the 
	
	public GroupResponse(){
		intiatorID = -1;
		proposedTimeslots = null;
		selectedTimeslots = null;
		isRejected = false;
		groupID = -1;
	}
	
	public GroupResponse(int _intiator, List<TimeSpan> _proposedTimeslots, int _groupID){
		intiatorID = _intiator;
		proposedTimeslots = _proposedTimeslots;
		selectedTimeslots = new ArrayList<TimeSpan>();
		isRejected = false;
		groupID = _groupID;
	}

	public boolean getIsRegjected(){
		return isRejected;
	}
	
	public void setIsRejected(boolean _isRejected){
		isRejected = _isRejected;
	}

	public int getIntiatorID() {
		return intiatorID;
	}

	public void setIntiatorID(int _initiatorID) {
		this.intiatorID = _initiatorID;
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