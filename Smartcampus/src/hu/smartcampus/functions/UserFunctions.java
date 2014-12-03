package hu.smartcampus.functions;

import hu.smartcampus.entities.User;

public class UserFunctions {
	private User loggedInUser;
	private boolean loginStatus = false;

	public boolean getLoginSatus() {
		return loginStatus;
	}

	public void setLoggedInUser(User loggedInUser) {
		this.loggedInUser = loggedInUser;
		loginStatus = true;
	}

	public User getLoggedInUser() {
		return loggedInUser;
	}

	public void logout() {
		loggedInUser = null;
		loginStatus = false;
	}

}