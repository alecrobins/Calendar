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
	
	public Location(int _id, String _name, boolean _isGroupFacility) {
		name = _name;
		locationID = _id;
		isGroupFacility = _isGroupFacility;
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

	
}
