package hkust.cse.calendar.unit;

import hkust.cse.calendar.apptstorage.LocationStorage;

import java.util.Arrays;
import java.util.List;


// Contains a list of locations
public class LocationList {

	// List of cities in Hong Kong 
	public List<String> cityList;
	private LocationStorage ls;
	
	public LocationList(LocationStorage _ls) {
		ls = _ls;
		cityList = ls.getLocations();
	}
	
	public List<String> getCityList() { return ls.getLocations(); } 
	
	public int getCityIndex(String s) { return cityList.indexOf(s); } 
	
	public String getCityName(int index){return cityList.get(index);}

}
