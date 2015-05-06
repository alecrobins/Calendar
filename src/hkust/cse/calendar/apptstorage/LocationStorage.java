package hkust.cse.calendar.apptstorage;

import hkust.cse.calendar.unit.Location;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class LocationStorage {
	
	public ArrayList<String> locations;
	private ApptStorageSQLImpl db;
	
	public LocationStorage() {
		db = new ApptStorageSQLImpl();
		// set the default to the list of cities
		locations = new ArrayList<String>();
		
		List<Location> allLocations = db.getAllLocations();
		
		for(int i = 0; i < allLocations.size(); ++i){
			Location l = allLocations.get(i);
			locations.add(l.getName());
		}
		
	}
	
	// TODO: need to add the ability to pass if isGroupFacility
	public boolean addLocation(String locationName){
		for (String s : locations) {        /// Check whether the location exists.
			if (locationName.equals(s)) return false;	
		}
		
		// save to db
		Location newLocation = new Location(locationName);
		newLocation.setIsGroupFacility(false);
		int locationID = db.createLocation(newLocation);
		newLocation.setLocationID(locationID);
		
		return locations.add(locationName);
	}
	
	public boolean deleteLocation(String locationName){
		
		db.deleteLocationByName(locationName);
		
		return locations.remove(locationName);
	}
	
	public List<String> getLocations() { return locations; }
}
