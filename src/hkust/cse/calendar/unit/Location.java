package hkust.cse.calendar.unit;

/*
 * how much time ahead the reminder should be triggered
 */
public class Location{
	
	// contains the index of the selected index
	private String name;
	private int locationID;
	private boolean isGroupFacility;
	
	public Location(String _name) {
		name = _name;
	}
	
	public Location(String _name, boolean _isGroupFacility) {
		name = _name;
		setIsGroupFacility(_isGroupFacility);
	}

	public String getName() {
		return name;
	}
	
	public void setLocationID(int id){
		locationID = id;
	}
	
	public String toString() {
		return name;
	}
	
	public int getLocationID(){
		return locationID;
	}

	public boolean getIsGroupFacility() {
		return isGroupFacility;
	}

	public void setIsGroupFacility(boolean isGroupFacility) {
		this.isGroupFacility = isGroupFacility;
	}

	
}
