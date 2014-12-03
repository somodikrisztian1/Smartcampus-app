package hu.smartcampus.dao;

import hu.smartcampus.entities.User;

public interface UserDAO {
	
	User getUser();
	void insertUser(User user);
	void updateProfilePic(String bitmapString);
	void deleteUserWrongSession();
	
}
