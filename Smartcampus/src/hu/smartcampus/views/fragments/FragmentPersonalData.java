package hu.smartcampus.views.fragments;

import hu.smartcampus.R;
import hu.smartcampus.entities.Event;
import hu.smartcampus.threads.BackgroundOperations;
import hu.smartcampus.utilities.OrientationLocker;
import hu.smartcampus.views.activities.ActivityMain;
import hu.smartcampus.views.toaster.Toaster;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.FragmentById;
import org.androidannotations.annotations.res.StringRes;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.provider.CalendarContract.Events;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ListAdapter;

// ha innen átlépünk a nearby hoz akkor az ottani oncreate hamarabb lefut mint az itteni ondestroy executependingtr nél is

@EFragment(R.layout.fragment_events_pd)
public class FragmentPersonalData extends Fragment implements OnClickListener {

	private ActivityMain activity;
	private ArrayList<Long> insertedEvents;
	
	@FragmentById
	FragmentTitles fragNormalPD;

	@StringRes
	String subscribedEvents;
	
	@Bean
	BackgroundOperations bg;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
		setRetainInstance(true);
	}
	
	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		this.activity = (ActivityMain) getActivity();
		this.activity.getSupportActionBar().setTitle(subscribedEvents);
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(requestCode == 1) {
			activity.showDialogAndLock();
			deleteEventsFromCalendar();
			activity.cancelDialogAndUnlock();
		}
	}
	
	@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
	private boolean addEventstoCalendar() {
		insertedEvents = new ArrayList<Long>();
		ContentResolver cr = getActivity().getApplicationContext().getContentResolver();
		
		
		ListAdapter adapter = fragNormalPD.getListAdapter();
		if(adapter != null && adapter.getCount() > 0) {
			for(int i = 0; i < adapter.getCount(); ++i) {
				Event event = (Event) adapter.getItem(i);
		        ContentValues values = new ContentValues();
		        StringBuilder locations = new StringBuilder();
				for(String s : event.getLocation()) {
					locations.append(s);
					locations.append(", ");
				}
				locations.delete(locations.length() - 2, locations.length());
		        values.put(CalendarContract.Events.DTSTART, event.getEventStart().getTimeInMillis());
		        values.put(CalendarContract.Events.DTEND, event.getEventEnd().getTimeInMillis());
		        values.put(CalendarContract.Events.TITLE, event.getTitle());
		        values.put(CalendarContract.Events.DESCRIPTION, event.getDescription());
		        values.put(CalendarContract.Events.EVENT_LOCATION, locations.toString());
		
		        TimeZone timeZone = TimeZone.getDefault();
		        values.put(CalendarContract.Events.EVENT_TIMEZONE, timeZone.getID());
		
		        // default calendar
		        values.put(CalendarContract.Events.CALENDAR_ID, 1);
		
		        // insert event to calendar
		        Uri uri = cr.insert(CalendarContract.Events.CONTENT_URI, values);
		        long eventID = Long.parseLong(uri.getLastPathSegment());
		        insertedEvents.add(eventID);
			}
			return true;
		}
		else {
			Toaster.noItemsForCalendarInsert(getActivity());
			return false;
		}
	}
	
	@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
	void deleteEventsFromCalendar() {
		ContentResolver cr = getActivity().getApplicationContext().getContentResolver();
		Uri deleteUri = null;
		for(long eventID : insertedEvents) {
			deleteUri = ContentUris.withAppendedId(Events.CONTENT_URI, eventID); 
			cr.delete(deleteUri, null, null);
		}
	}

	@Override
	public void onClick(DialogInterface dialog, int which) {
		OrientationLocker.unlockScreenOrientation(getActivity());
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		Log.d("lol", "clear");
		menu.clear();
		inflater.inflate(R.menu.menu_personal_datas, menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
		    case R.id.logout:
		    	if(Toaster.isOnlineAndLoggedIn(activity)) {
					bg.logout(activity);
				}
		    	break;
		    case R.id.showAllInCalendar:
		    	if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
					try {
						boolean b = addEventstoCalendar();
						if(b) {
							Intent i = new Intent();  
							// Android 2.2+  
							i.setData(Uri.parse("content://com.android.calendar/time"));    
							startActivityForResult(i, 1);
						}
					} catch (Exception e) {
						activity.handleError(e);
					}
					
				}
				else {
					Toaster.isSDKVersionOk(getActivity());
				}
		    	break;
		    case R.id.modifyProfilePicture:
		    	activity.showDialog();
		    	break;
		    case R.id.about:
		    	Calendar c = Calendar.getInstance();
	    		c.set(Calendar.YEAR, 2014);
	    		c.set(Calendar.MONTH, Calendar.SEPTEMBER);
	    		c.set(Calendar.DAY_OF_MONTH, 20);
	    		String s;
		    	if(Locale.getDefault().getLanguage().equals("hu")) {
		    		SimpleDateFormat formatter = new SimpleDateFormat("yyyy.MM.dd.", Locale.getDefault());
			    	s = "Verzió: 1.0\nUtoljára frissítve: " + formatter.format(c.getTime()) + "\nAPK méret: 3.5 MB\nTámogatott nyelvek: angol, magyar\n" +
							"Minimális Android verzió: GINGERBREAD\n\nAz alkalmazás azért jött létre, hogy az egyetemi dolgozók, hallgatók kényelmesen hozzájuthassanak online információkhoz " +
							"és az eseményekhez kapcsolódó ügyeiket elektronikusan intézhessék. Ez az alkalmazás egy lépés az intelligens campus irányába.\n\n" +
							"Készítette: Somodi Krisztián";
		    	}
		    	else {
		    		DateFormat dateInstance = SimpleDateFormat.getDateInstance();
		    	    String date = dateInstance.format(c.getTime());
		    		s = "Version: 1.0\nUpdated: " + date + "\nAPK size: 3.5 MB\nSupported languages: English, Hungarian\n" +
							"Minimal Android version: GINGERBREAD\n\nThis application helps the workers and students of the university to get online informations easily, furthermore, the tasks related to events can be managed online." +
							" Smartcampus app is a step towards the intelligent campus.\n\n" +
							"Developer: Krisztián Somodi";
		    	}
				AlertDialog.Builder b = new AlertDialog.Builder(getActivity()).setTitle(R.string.about).setPositiveButton("OK", this);
				b.setMessage(s);
				b.setCancelable(false);
				OrientationLocker.lockScreenOrientation(getActivity());
				b.show();
				break;
	    }
	    return false;
	}
	
}
