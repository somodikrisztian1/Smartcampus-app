package hu.smartcampus.functions;

import hu.smartcampus.access.DataAccess;

import java.util.List;

public class MappingFunctions {

	public List<String> roomList(String ssid, String bssid, int level) throws Exception {
		return DataAccess.getInstance().getMappingAccessRemote().roomList(ssid, bssid, level);
	}

}
