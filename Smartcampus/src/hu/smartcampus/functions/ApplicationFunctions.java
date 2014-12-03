package hu.smartcampus.functions;

public class ApplicationFunctions {

	private static ApplicationFunctions instance;
	private UserFunctions userFunctions;
	private CalendarFunctions calendarFunctions;
	private MappingFunctions mappingFunctions;

	/**
	 * Singleton. Az alkalmazás logikai réteg belépési pontja.
	 * 
	 * @return példány.
	 */
	public static ApplicationFunctions getInstance() {
		synchronized (ApplicationFunctions.class) {
			if (instance == null) {
				instance = new ApplicationFunctions();
			}
			return instance;
		}
	}

	public UserFunctions getUserFunctions() {
		return userFunctions;
	}

	public CalendarFunctions getCalendarFunctions() {
		return calendarFunctions;
	}

	public MappingFunctions getMappingFunctions() {
		return mappingFunctions;
	}

	private ApplicationFunctions() {
		userFunctions = new UserFunctions();
		calendarFunctions = new CalendarFunctions();
		mappingFunctions = new MappingFunctions();
	}

}