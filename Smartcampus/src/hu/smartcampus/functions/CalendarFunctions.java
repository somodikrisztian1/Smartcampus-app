package hu.smartcampus.functions;

import hu.smartcampus.access.DataAccess;
import hu.smartcampus.entities.Event;
import hu.smartcampus.entities.FilterItem;
import hu.smartcampus.entities.RankType;
import hu.smartcampus.entities.User;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

public class CalendarFunctions {

	public boolean openSession(String userName, String password) throws Exception {
		User logged = DataAccess.getInstance().getCalendarAccessRemote().openSession(userName, password);
		if(logged != null) {
			ApplicationFunctions.getInstance().getUserFunctions().setLoggedInUser(logged);
			return true;
		}
		return false;
	}
	
	public void closeSession(String sessionId) throws Exception {
		DataAccess.getInstance().getCalendarAccessRemote().closeSession(sessionId);
		ApplicationFunctions.getInstance().getUserFunctions().logout();
	}
	
	public List<Event> listFiltered(String sessionId, Calendar from, Calendar to, HashMap<String, ArrayList<Long>> filters) throws Exception {
		return DataAccess.getInstance().getCalendarAccessRemote().listFiltered(sessionId, from, to, filters);
	}
	
	public List<Event> listMarked(String sessionId) throws Exception {
		return DataAccess.getInstance().getCalendarAccessRemote().listMarked(sessionId);
	}
	
	public boolean markEvent(String sessionID, int eventID, String comment) throws Exception {
		return DataAccess.getInstance().getCalendarAccessRemote().markEvent(sessionID, eventID, comment);
	}
	
	public boolean unmarkEvent(String sessionID, int eventID) throws Exception {
		return DataAccess.getInstance().getCalendarAccessRemote().unmarkEvent(sessionID, eventID);
	}
	
	public String[] getSessionInfo(String sessionID) throws Exception {
		return DataAccess.getInstance().getCalendarAccessRemote().getSessionInfo(sessionID);
	}
	
	public ArrayList<FilterItem> categories() throws Exception {
		return DataAccess.getInstance().getCalendarAccessRemote().categories();
	}
	
	public ArrayList<FilterItem> providers() throws Exception {
		return DataAccess.getInstance().getCalendarAccessRemote().providers();
	}
	
	public ArrayList<FilterItem> locations() throws Exception {
		return DataAccess.getInstance().getCalendarAccessRemote().locations();
	}
	
	public List<FilterItem> locationsFiltered(String roomName) throws Exception {
		return DataAccess.getInstance().getCalendarAccessRemote().locationsFiltered(roomName);
	}
	
	public List<String> getSubscribers(String sessionId, int eventId) throws Exception {
		return DataAccess.getInstance().getCalendarAccessRemote().getSubscribers(sessionId, eventId);
	}
	
	public List<RankType> getEventRankTypes(long eventId) throws Exception {
		return DataAccess.getInstance().getCalendarAccessRemote().getEventRankTypes(eventId);
	}
	
	public void rankEvent(String sessionId, long eventId, long rankId, int value, String ip) throws Exception {
		DataAccess.getInstance().getCalendarAccessRemote().rankEvent(sessionId, eventId, rankId, value, ip);
	}
	
	public Event getEvent(long eventId, String sessionId) throws Exception {
		return DataAccess.getInstance().getCalendarAccessRemote().getEvent(eventId, sessionId);
	}
	
}