package hkust.cse.calendar.apptstorage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class LocationStorage {
	
	public ArrayList<String> locations;
	
	public LocationStorage() {
		// set the default to the list of cities
		locations = new ArrayList<String>();
		locations.add("Default");
	}
	
	public boolean addLocation(String locationName){
		for (String s : locations) {        /// Check whether the location exists.
			if (locationName.equals(s)) return false;	
		}
		return locations.add(locationName);
	}
	
	public boolean deleteLocation(String locationName){
		return locations.remove(locationName);
	}
	
	public List<String> getLocations() { return locations; }
}
