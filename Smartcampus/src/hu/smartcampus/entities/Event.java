package hu.smartcampus.entities;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.os.Parcel;
import android.os.Parcelable;

public class Event implements Comparable<Event>, Parcelable{

	private int eventId;
	private int markerType = -1;
	private String markerRemark;
	private String title;
	private String description;
	private Calendar eventStart;
	private Calendar eventEnd;
	private List<String> categories;
	private List<String> providers;
	private List<String> locations;
	private List<EventRankResult> rankResult;
	private boolean provided;
	private boolean rankable;
	private boolean isNearby = false;
	private boolean editable;
		
	public Event(int markerType, String markerRemark, String title,
			String description, Calendar eventStart, Calendar eventEnd,
			List<String> categories, List<String> providers,
			List<String> locations, List<EventRankResult> rankResult,
			int eventId, boolean provided, boolean rankable, boolean editable) {
		super();
		this.markerType = markerType;
		this.markerRemark = markerRemark;
		this.title = title;
		this.description = description;
		this.eventStart = eventStart;
		this.eventEnd = eventEnd;
		this.categories = categories;
		this.providers = providers;
		this.locations = locations;
		this.rankResult = rankResult;
		this.eventId = eventId;
		this.provided = provided;
		this.rankable = rankable;
		this.editable = editable;
	}

	public Event(int eventId, String title, String description, Calendar eventStart,
			Calendar eventEnd) {
		super(); 
		this.eventId = eventId;
		this.title = title;
		this.description = description;
		this.eventStart = eventStart;
		this.eventEnd = eventEnd;
		this.providers = new ArrayList<String>();
		this.categories = new ArrayList<String>();
		this.locations = new ArrayList<String>();
		this.rankResult = new ArrayList<EventRankResult>();

	}

	public void setProvided(boolean provided) {
		this.provided = provided;
	}
	
	public boolean isNearby() {
		return isNearby;
	}

	public void setNearby(boolean isNearby) {
		this.isNearby = isNearby;
	}
	
	public boolean isEditable() {
		return editable;
	}

	public void setEditable(boolean editable) {
		this.editable = editable;
	}
	
	public boolean isRankable() {
		return rankable;
	}

	public boolean isProvided() {
		return provided;
	}
	
	public int getEventId() {
		return eventId;
	}
	
	public String getMarkerRemark() {
		return markerRemark;
	}
	
	public int getMarkerType() {
		return markerType;
	}
	
	public void addCategory(long id, String name) {
		categories.add(name);
	}

	public void addProvider(long id, String name) {
		providers.add(name);
	}
	
	public void addLocation(long id, String name) {
		locations.add(name);
	}

	public void addRankResult(EventRankResult result) {
		rankResult.add(result);
	}
	
	public List<EventRankResult> getRankResult() {
		return rankResult;
	}
	
	public Iterable<String> getCategory() {
		return categories;
	}

	public Iterable<String> getProvider() {
		return providers;
	}
	
	public Iterable<String> getLocation() {
		return locations;
	}
	
	public String getTitle() {
		return title;
	}

	public String getDescription() {
		return description;
	}

	public Calendar getEventStart() {
		return eventStart;
	}

	public Calendar getEventEnd() {
		return eventEnd;
	}
	
	public void setMarkerType(int markerType) {
		this.markerType = markerType;
	}

	public void setMarkerRemark(String markerRemark) {
		this.markerRemark = markerRemark;
	}

	public void setRankable(boolean rankable) {
		this.rankable = rankable;
	}

	@Override
	public String toString() {
		if(isNearby) {
			StringBuilder sb = new StringBuilder();
			int size = locations.size();
			for(int i = 0; i < size; ++i) {
				sb.append(locations.get(i));
				if(i != size - 1) {
					sb.append(", ");
				}
			}
			return sb.toString();
		}
		return title;
	}

	@Override
	public int compareTo(Event another) {
		if(isNearby) {
			StringBuilder sb1 = new StringBuilder();
			for(String s : this.locations) {
				sb1.append(s);
			}
			StringBuilder sb2 = new StringBuilder();
			for(String s : another.locations) {
				sb2.append(s);
			}
			return sb1.toString().compareToIgnoreCase(sb2.toString());
		}
		return this.title.compareToIgnoreCase(another.title);
	}

	@Override
	public int describeContents() {
		return 0;
	}

	public static final Parcelable.Creator<Event> CREATOR = new Parcelable.Creator<Event>() {
		
		@Override
		public Event createFromParcel(Parcel source) {
			return new Event(source);
		}
		
		@Override
		public Event[] newArray(int size) {
			return new Event[size];
		}
		
	};
	
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(markerType);
		dest.writeInt(eventId);
		dest.writeString(markerRemark);
		dest.writeString(title);
		dest.writeString(description);
		dest.writeSerializable(eventStart);
		dest.writeSerializable(eventEnd);
		dest.writeStringList(categories);
		dest.writeStringList(providers);
		dest.writeStringList(locations);
		dest.writeList(rankResult);
		dest.writeValue(provided);
		dest.writeValue(rankable);
		dest.writeValue(isNearby);
		dest.writeValue(editable);
	}

	private Event(Parcel in) {
		markerType = in.readInt();
		eventId = in.readInt();
		markerRemark = in.readString();
		title = in.readString();
		description = in.readString();
		eventStart = (Calendar) in.readSerializable();
		eventEnd = (Calendar) in.readSerializable();
		categories = new ArrayList<String>();
		providers = new ArrayList<String>();
		locations = new ArrayList<String>();
		in.readStringList(categories);
		in.readStringList(providers);
		in.readStringList(locations);
		rankResult = new ArrayList<EventRankResult>();
		in.readList(rankResult, getClass().getClassLoader());
		provided = (Boolean) in.readValue(null);
		rankable = (Boolean) in.readValue(null);
		isNearby = (Boolean) in.readValue(null);
		editable = (Boolean) in.readValue(null);
	}
	
}