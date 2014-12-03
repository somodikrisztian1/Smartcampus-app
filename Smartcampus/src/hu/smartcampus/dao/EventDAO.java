package hu.smartcampus.dao;

import hu.smartcampus.entities.Event;

import java.util.List;

public interface EventDAO {
	
	List<Event> getMarkedEvents();
	void insertMarkedEvents(List<Event> markedEvents);
	
}