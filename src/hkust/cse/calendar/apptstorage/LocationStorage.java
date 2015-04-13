package hkust.cse.calendar.apptstorage;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class LocationStorage {
	
	private List<String> cityList = Arrays.asList("Shuen Wan","Central District","Hung Hom","Kowloon","Quarry Bay","Ngau Tau Kok","Ying Pun","Repulse Bay","Causeway Bay","Tseung Kwan O","Tai Kok Tsui","Tai Wai","Ma On Shan Tsuen","To Kwa Wan","Wong Tai Sin","Tuen Mun San Hui","Ma Yau Tong","Ngau Chi Wan","Yau Ma Tei","Kennedy Town","Chai Wan Kok","Sham Shui Po","Mid Levels","North Point","Happy Valley","Sai Keng","Kwun Hang","Mong Kok","Shek Tong Tsui","Cheung Sha Wan","Sham Tseng","Yuen Long San Hui","Kwai Chung","Sha Tin Wai","Tin Shui Wai","Hong Kong","Tai Hang","Fo Tan","Tsimshatsui","Tsz Wan Shan","San Tung Chung Hang","Peng Chau","Sha Po Kong","Wan Tsai","Shek Kip Mei","Aberdeen","Tai Po","Lai Chi Wo","Shau Kei Wan","Cheung Kong","Tai Lin Pai","Chuen Lung","Sheung Shui","Sheung Tsuen","Fanling","Fa Yuen","Chek Chue","Tai Tan","Kowloon Tong","Ho Man Tin","Ma Wan","Cha Kwo Ling","Wo Che","Lam Tin","Nam A","Tsing Lung Tau","Ting Kau","Tai Chau To","Lin Fa Tei","Chung Hau","Ping Yeung","Wong Chuk Hang","San Tsuen","Pak Ngan Heung","Lam Tei","Kat O Sheung Wai","Lo So Shing","Sha Tau Kok","Tin Wan Resettlement Estate","Tai Wan To","Mau Ping","Shatin");

	public List<String> locations;
	
	public LocationStorage() {
		// set the default to the list of cities
		locations = new LinkedList<String>(cityList);
	}
	
	public void addLocation(String l){
		System.out.println("ADDEING");
		locations.add(l);
	}
	
	public List<String> getLocations() { return locations; }
}
