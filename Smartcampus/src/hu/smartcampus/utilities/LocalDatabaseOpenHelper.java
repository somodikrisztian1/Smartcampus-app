package hu.smartcampus.utilities;

import hu.smartcampus.access.DataAccess;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.androidannotations.annotations.EBean;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

@EBean
public class LocalDatabaseOpenHelper extends SQLiteOpenHelper
{
	
	private Context context;
	private static final String DATABASE_NAME = "SmartcampusDB.db";
	private static final String PREF_NAME = "database";
	private static final String PREF_KEY = "version";
	
	public LocalDatabaseOpenHelper(Context context)
	{
		super(context, DATABASE_NAME, null, DataAccess.DATABASE_VERSION);
		this.context = context;
		
		// Ezzel létrejön az adatbázis, vagy megnyílik ha volt
		SQLiteDatabase database = context.openOrCreateDatabase(DATABASE_NAME, Context.MODE_PRIVATE, null);
		database.close();
		
		// Ezzel meg megtudjuk hol van
		File databaseLocation = context.getDatabasePath(DATABASE_NAME);
		int currentVersion = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE).getInt(PREF_KEY, -1); // TODO
		Log.d("lol", "cur: " + currentVersion);

		// Ha nincs még adatbázis, vagy régebbi verzió
		if (currentVersion == -1 || currentVersion < DataAccess.DATABASE_VERSION)
		{
			Log.d("lol", "Adatbázis nem létezett, vagy régi verziójú. Adatbázis bemásolása az assets mappából.");
			createDatabase(databaseLocation);
			context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE).edit().putInt(PREF_KEY, DataAccess.DATABASE_VERSION).commit();
		} else
		{
			Log.d("lol", "Adatbázis létezik, és friss.");
		}
	}

	@Override
	public void onCreate(SQLiteDatabase db)
	{
		// direkt üres
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
	{
		// direkt üres
	}

	private void createDatabase(File location)
	{
		InputStream src = null;
		try
		{
			src = context.getAssets().open(DATABASE_NAME);
			copyFile(src, new FileOutputStream(location));
		} catch (IOException e)
		{
			Log.d("lol", "I/O hiba!", e);
		}
	}

	private void copyFile(InputStream in, OutputStream out) throws IOException
	{
		byte[] buffer = new byte[1024];
		int read;
		while ((read = in.read(buffer)) != -1)
		{
			out.write(buffer, 0, read);
		}
	}
	
	@Override
	public SQLiteDatabase getReadableDatabase()
	{
		SQLiteDatabase db = null;
		synchronized (LocalDatabaseOpenHelper.class)
		{
			 db = super.getReadableDatabase();
		}
		return db;
	}

	
	@Override
	public SQLiteDatabase getWritableDatabase()
	{
		SQLiteDatabase db = null;
		synchronized (LocalDatabaseOpenHelper.class)
		{
			 db = super.getWritableDatabase();
		}
		return db;
	}
	
}