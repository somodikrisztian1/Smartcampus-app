package hu.smartcampus.dao;

import hu.smartcampus.entities.User;
import hu.smartcampus.functions.SystemFunctions;
import hu.smartcampus.utilities.LocalDatabaseOpenHelper;

import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.Transactional;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;

@EBean
public class UserDAOImpl implements UserDAO {

	private LocalDatabaseOpenHelper dbHelper;

	public UserDAOImpl(Context context) {
		super();
		this.dbHelper = new LocalDatabaseOpenHelper(context);
	}

	@Override
	public void insertUser(User user) {
		SQLiteDatabase writeDB = dbHelper.getWritableDatabase();
		insertU(writeDB, user);
		writeDB.close();
	}

	@Override
	public void deleteUserWrongSession() {
		SQLiteDatabase writeDB = dbHelper.getWritableDatabase();
		writeDB.delete("User", null, null);
		writeDB.close();
	}

	@Override
	public User getUser() {
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		Cursor c = db.query("User", new String[] { "*" }, null, null, null, null, null);
		if (c.moveToFirst()) {
			User user = null;
			while (!c.isAfterLast()) {
				String sessionID = c.getString(0);
				String displayName = c.getString(1);
				String userName = c.getString(2);
				String profilePic = c.getString(3);
				Bitmap profilePicture = SystemFunctions.stringToBitmap(profilePic);
				user = new User(sessionID, displayName, userName, profilePicture);
				c.move(1);
			}
			return user;
		}
		db.close();
		return null;
	}

	@Override
	public void updateProfilePic(String bitmapString) {
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		ContentValues v = new ContentValues();
		v.put("profilePic", bitmapString);
		db.update("User", v, null, null);
		db.close();
	}

	@Transactional
	void insertU(SQLiteDatabase db, User user) {
		db.delete("User", null, null);
		ContentValues values = new ContentValues();
		values.put("sessionID", user.getSessionId());
		values.put("displayName", user.getDisplayName());
		values.put("userName", user.getUserName());
		values.put("profilePic", SystemFunctions.bitMapToString(user.getProfilePicture()));
		db.insert("User", null, values);
	}

}