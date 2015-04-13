package hkust.cse.calendar.unit;

/*
 * how much time ahead the reminder should be triggered
 */
public class Location{
	
	// contains the index of the selected index
	private String name;
	
	public Location(String _name) {
		name = _name;
	}

	public String getName() {
		return name;
	}
	
	public String toString() {
		return name;
	}

	
}
