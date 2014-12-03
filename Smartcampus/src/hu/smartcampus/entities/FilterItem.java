package hu.smartcampus.entities;

public class FilterItem {
	
	private long id;
	private String name;
	
	public FilterItem(long id, String name) {
		super();
		this.id = id;
		this.name = name;
	}

	public long getId() {
		return id;
	}
	
	public String getName() {
		return name;
	}
	
	@Override
	public String toString() {
		return name;
	}
	
}