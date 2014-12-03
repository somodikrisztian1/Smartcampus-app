package hu.smartcampus.access;

import hu.smartcampus.access.calendar.CalendarAccessRemote;
import hu.smartcampus.access.mapping.MappingAccessRemote;

public class DataAccess
{
	
	public static final int DATABASE_VERSION = 1;
	private static DataAccess instance;
	private CalendarAccessRemote calendarAccessRemote;
	private MappingAccessRemote mappingAccessRemote;
	
	/**
	 * Singleton. Az adatelérési réteg belépési pontja.
	 * 
	 * @return példány.
	 */
	public static DataAccess getInstance()
	{
		synchronized (DataAccess.class)
		{
			if (instance == null) {
				instance = new DataAccess();
			}
			return instance;
		}
	}

	private DataAccess()
	{
		calendarAccessRemote = new CalendarAccessRemote();
		mappingAccessRemote = new MappingAccessRemote();
	}

	
	public CalendarAccessRemote getCalendarAccessRemote()
	{
		return calendarAccessRemote;
	}
	
	public MappingAccessRemote getMappingAccessRemote()
	{
		return mappingAccessRemote;
	}
	
}
