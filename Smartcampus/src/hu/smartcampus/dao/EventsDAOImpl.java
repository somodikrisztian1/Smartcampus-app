package hu.smartcampus.dao;

import hu.smartcampus.entities.Event;
import hu.smartcampus.entities.EventRankResult;
import hu.smartcampus.utilities.LocalDatabaseOpenHelper;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.Transactional;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

@EBean
public class EventsDAOImpl implements EventDAO {

	private LocalDatabaseOpenHelper dbHelper;

	public EventsDAOImpl(Context context) {
		super();
		this.dbHelper = new LocalDatabaseOpenHelper(context);
	}

	@Override
	public List<Event> getMarkedEvents() {
		SQLiteDatabase readDB = dbHelper.getReadableDatabase();
		
		@SuppressWarnings("unchecked")
		List<Event> list = (List<Event>) getEvents(readDB);
		readDB.close();
		return list;
	}
	
	@Override
	public void insertMarkedEvents(List<Event> markedEvents) {
		SQLiteDatabase writeDB = dbHelper.getWritableDatabase();
		insertEvents(writeDB, markedEvents);    // még nem törölte mikor már insertált (mikor még külön volt)
		writeDB.close();
	}

	void deleteTables(SQLiteDatabase db) {
		db.delete("Event", null, null);
		db.delete("Categories", null, null);
		db.delete("Providers", null, null);
		db.delete("Locations", null, null);
		db.delete("RankResult", null, null);
	}
	
	@Transactional
	Object getEvents(SQLiteDatabase db) {
		ArrayList<Event> events = new ArrayList<Event>();
		Cursor cEvent = db.query("Event", new String[] { "*" }, null, null, null,
				null, null);
		if (cEvent.moveToFirst()) {
			while (!cEvent.isAfterLast()) {
				int id = cEvent.getInt(0);
				String title = cEvent.getString(1);
				String description = cEvent.getString(2);
				Calendar startDate = Calendar.getInstance();
				startDate.setTimeInMillis(cEvent.getLong(3));
				Calendar endDate = Calendar.getInstance();
				endDate.setTimeInMillis(cEvent.getLong(4));
				int markerType = cEvent.getInt(5);
				String markerRemark = cEvent.getString(6);
				boolean provided = cEvent.getInt(7) == 1 ? true : false;
				boolean rankable = cEvent.getInt(8) == 1 ? true : false;
				boolean editable = cEvent.getInt(9) == 1 ? true : false;
				List<String> categories = new ArrayList<String>();
				Cursor cCategories = db.query("Categories", new String[] { "name" }, "eventID=?", new String[]{"" + id}, null,
						null, null);
				if (cCategories.moveToFirst()) {
					while (!cCategories.isAfterLast()) {
						categories.add(cCategories.getString(0));
						cCategories.move(1);
					}
				}
				cCategories.close();
				List<String> providers = new ArrayList<String>();
				Cursor cProviders = db.query("Providers", new String[] { "name" }, "eventID=?", new String[]{"" + id}, null,
						null, null);
				if (cProviders.moveToFirst()) {
					while (!cProviders.isAfterLast()) {
						providers.add(cProviders.getString(0));
						cProviders.move(1);
					}
				}
				cProviders.close();
				List<String> locations = new ArrayList<String>();
				Cursor cLocations = db.query("Locations", new String[] { "name" }, "eventID=?", new String[]{"" + id}, null,
						null, null);
				if (cLocations.moveToFirst()) {
					while (!cLocations.isAfterLast()) {
						locations.add(cLocations.getString(0));
						cLocations.move(1);
					}
				}
				cLocations.close();
				List<EventRankResult> rankResults = new ArrayList<EventRankResult>();
				Cursor cRanks = db.query("RankResult", new String[] { "rankID", "title", "count", "rankValue"}, "eventID=?", new String[]{"" + id}, null,
						null, null);
				if (cRanks.moveToFirst()) {
					while (!cRanks.isAfterLast()) {
						rankResults.add(new EventRankResult(cRanks.getLong(0), cRanks.getString(1), cRanks.getLong(2),
								cRanks.getDouble(3)));
						cRanks.move(1);
					}
				}
				cRanks.close();
				Event event = new Event(markerType, markerRemark, title, description, startDate, endDate, categories, providers,
						locations, rankResults, id, provided, rankable, editable);
				events.add(event);
				cEvent.move(1);
			}
		}
		cEvent.close();
		return events;
	}
	
	@Transactional
	void insertEvents(SQLiteDatabase db, List<Event> markedEvents) {
		deleteTables(db);
		for(Event e : markedEvents) {
			db.execSQL("INSERT INTO Event VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?);", new Object[]{e.getEventId(), 
					e.getTitle(), e.getDescription(), e.getEventStart().getTimeInMillis(), e.getEventEnd().getTimeInMillis(), 
					e.getMarkerType(), e.getMarkerRemark(), e.isProvided() == true ? 1 : 0, 
							e.isRankable() == true ? 1 : 0, e.isEditable() == true ? 1 : 0});
			for(String cName : e.getCategory()) {
				db.execSQL("INSERT INTO Categories VALUES (?, ?);", new Object[]{e.getEventId(), cName});
			}
			for(String pName : e.getProvider()) {
				db.execSQL("INSERT INTO Providers VALUES (?, ?);", new Object[]{e.getEventId(), pName});
			}
			for(String lName : e.getLocation()) {
				db.execSQL("INSERT INTO Locations VALUES (?, ?);", new Object[]{e.getEventId(), lName});
			}
			for(EventRankResult r : e.getRankResult()) {
				db.execSQL("INSERT INTO RankResult VALUES (?, ?, ?, ?, ?);", new Object[]{r.getRankId(), e.getEventId(), 
						r.getTitle(), r.getCount(), r.getRankValue()});
			}
		}
	}

}