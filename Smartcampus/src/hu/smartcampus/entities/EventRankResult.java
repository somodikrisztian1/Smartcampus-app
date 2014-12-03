package hu.smartcampus.entities;

import android.os.Parcel;
import android.os.Parcelable;

public class EventRankResult implements Parcelable {

	private long rankId;
	private String title;
	private long count;
	private Double rankValue;

	public EventRankResult() {
	}

	public EventRankResult(long rankId, String title, long count, Double rankValue) {
		super();
		this.rankId = rankId;
		this.title = title;
		this.count = count;
		this.rankValue = rankValue;
	}

	public long getRankId() {
		return rankId;
	}

	public void setRankId(long rankId) {
		this.rankId = rankId;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public long getCount() {
		return count;
	}

	public void setCount(long count) {
		this.count = count;
	}

	public Double getRankValue() {
		return rankValue;
	}

	public void setRankValue(Double rankValue) {
		this.rankValue = rankValue;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	public static final Parcelable.Creator<EventRankResult> CREATOR = new Parcelable.Creator<EventRankResult>() {

		@Override
		public EventRankResult createFromParcel(Parcel source) {
			return new EventRankResult(source);
		}

		@Override
		public EventRankResult[] newArray(int size) {
			return new EventRankResult[size];
		}

	};

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeLong(rankId);
		dest.writeLong(count);
		if (count > 0) { // ha a count 0 akkor nem jön vissza rankValue mező
			dest.writeDouble(rankValue);
		} else {
			dest.writeDouble(0.0);
		}
		dest.writeString(title);
	}

	private EventRankResult(Parcel in) {
		rankId = in.readLong();
		count = in.readLong();
		rankValue = in.readDouble();
		title = in.readString();
	}

}