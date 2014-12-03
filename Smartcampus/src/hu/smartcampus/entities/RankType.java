package hu.smartcampus.entities;

public class RankType {

	private long id;
	private String title;
	private String description;
	private RankValue[] values;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public RankValue[] getValues() {
		return values;
	}

	public void setValues(RankValue[] values) {
		this.values = values;
	}

}