package hu.smartcampus.entities;

import android.graphics.Bitmap;

public class User
{
	
	private String sessionId;
	private String displayName;
	private String userName;
	public Bitmap profilePicture; // profilkép, Bitmap-ban van, a SystemApplications osztályban van konvertálás rá
	
	public User()
	{
		super();
	}
	
	public User(String sessionId) {
		this.sessionId = sessionId;
	}
	
	public User(String sessionId, String displayName, String userName,
			Bitmap profilePicture) {
		super();
		this.sessionId = sessionId;
		this.displayName = displayName;
		this.userName = userName;
		this.profilePicture = profilePicture;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public Bitmap getProfilePicture() {
		return profilePicture;
	}

	public void setProfilePicture(Bitmap profilePicture) {
		this.profilePicture = profilePicture;
	}
	
	public String getSessionId() {
		return sessionId;
	}

	public void setSessionID(String sessionId) {
		this.sessionId = sessionId;
	}
	
}