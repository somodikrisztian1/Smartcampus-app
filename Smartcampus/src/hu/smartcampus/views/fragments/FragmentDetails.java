package hu.smartcampus.views.fragments;

import hu.smartcampus.R;
import hu.smartcampus.entities.Event;
import hu.smartcampus.functions.SystemFunctions;
import hu.smartcampus.templates.CustomActionBarActivity;
import hu.smartcampus.threads.BackgroundOperations;
import hu.smartcampus.utilities.OrientationLocker;
import hu.smartcampus.views.activities.*;
import hu.smartcampus.views.toaster.Toaster;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.res.StringRes;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Build;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.provider.CalendarContract.Events;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

@EFragment(R.layout.fragment_details)
public class FragmentDetails extends Fragment implements OnClickListener {

	static boolean btnPressed = false;
	private CustomActionBarActivity activity;
	private Event event;
	private MenuItem menuCalendarOne;
	private FragmentTitles fragLargeTitles;
	private FragmentDetails fragLargeDetails;

	@ViewById
	TextView title;
	@ViewById
	TextView editCategories;
	@ViewById
	TextView editStartTime;
	@ViewById
	TextView editEndTime;
	@ViewById
	TextView editLocations;
	@ViewById
	TextView editProviders;
	@ViewById
	Button btnSubscribe;
	@StringRes
	String detailsTitle;
	@StringRes
	String subscribers;
	@StringRes
	String subscribe;
	@StringRes
	String unsubscribe;
	@Bean
	BackgroundOperations bg;
	private View view;

	@Click
	void btnSubscribe(View v) {
		if (Toaster.isOnlineAndLoggedIn(activity)) {
			Button button = (Button) v;

			btnPressed = true;

			if (button.getText().equals(subscribe)) {
				bg.markEvent(event.getEventId(), true, this);
			} else {
				bg.markEvent(event.getEventId(), false, this);
			}
		}
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		this.activity = (CustomActionBarActivity) activity;
		event = activity.getIntent().getParcelableExtra("event");
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}

	@AfterViews
	void afterViews() {
		view = getView();
		fragLargeTitles = (FragmentTitles) getFragmentManager().findFragmentById(R.id.fragLargeTitles);
		fragLargeDetails = (FragmentDetails) getFragmentManager().findFragmentById(R.id.fragLargeDetails);
		if (ActivityMain.fragment instanceof FragmentNearby) {
			btnSubscribe.setVisibility(View.GONE);
		}
		if (fragLargeDetails == null) {
			view.setBackgroundResource(R.drawable.deik);
		}
		if (event != null) {
			viewSetter(view);
		}
		if (SystemFunctions.isLargeLandscape(getActivity())) {
			if (this.event == null) {
				view.setVisibility(View.INVISIBLE);
			}
		}
	}

	public void updateContent(Event event) {
		if (view.getVisibility() == View.INVISIBLE) {
			view.setVisibility(View.VISIBLE);
			menuCalendarOne.setVisible(true);
		}
		this.event = event;
		viewSetter(view);
		activity.supportInvalidateOptionsMenu();
	}

	private void viewSetter(View layout) {
		if (event.getMarkerType() != -1) {
			btnSubscribe.setText(unsubscribe);
		} else {
			btnSubscribe.setText(subscribe);
		}
		title.setText(event.getTitle());
		title.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);
		StringBuilder sb = new StringBuilder();
		ArrayList<String> cats = (ArrayList<String>) event.getCategory();
		int size = cats.size();
		for (int i = 0; i < size; ++i) {
			sb.append(cats.get(i));
			if (i != size - 1) {
				sb.append(", ");
			}
		}
		editCategories.setText(sb.toString());
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy.MM.dd   HH:mm", Locale.getDefault());
		editStartTime.setText(formatter.format(event.getEventStart().getTime()));
		editEndTime.setText(formatter.format(event.getEventEnd().getTime()));
		sb.setLength(0);
		ArrayList<String> locs = (ArrayList<String>) event.getLocation();
		size = locs.size();
		for (int i = 0; i < size; ++i) {
			sb.append(locs.get(i).toString());
			if (i != size - 1) {
				sb.append(", ");
			}
		}
		editLocations.setText(sb.toString());
		sb.setLength(0);
		ArrayList<String> provs = (ArrayList<String>) event.getProvider();
		size = provs.size();
		for (int i = 0; i < size; ++i) {
			sb.append(provs.get(i).toString());
			if (i != size - 1) {
				sb.append(", ");
			}
		}
		editProviders.setText(sb.toString());
	}

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		this.activity.getSupportActionBar().setTitle(detailsTitle);
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		if (menu != null) {
			if (fragLargeTitles == null) {
				menu.clear();
			}
			inflater.inflate(R.menu.menu_event_details, menu);
			menuCalendarOne = menu.findItem(R.id.menuCalendarOne);
		}
		if (event != null) {
			if (event.isRankable()) {
				MenuItem menuRating = menu.findItem(R.id.menuRating);
				menuRating.setVisible(true);
			}
			if (event.isEditable()) {
				MenuItem menuSubsrcibers = menu.findItem(R.id.menuSubscribers);
				menuSubsrcibers.setVisible(true);
			}
		} else {
			menuCalendarOne.setVisible(false);
		}
		super.onCreateOptionsMenu(menu, inflater);
	}

	@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			activity.onBackPressed();
			return super.onOptionsItemSelected(item);
		case R.id.menuRating:
			if (Toaster.isOnline(activity)) {
				Intent intent = new Intent(activity, ActivityRatings_.class);
				intent.putExtra("event", event);
				startActivity(intent);
			}
			return super.onOptionsItemSelected(item);
		case R.id.menuSubscribers:
			if (Toaster.isOnlineAndLoggedIn(activity)) {
				bg.getSubscribers(event.getEventId(), this);
			}
			return super.onOptionsItemSelected(item);
		case R.id.menuCalendarOne:
			try {
				if (Toaster.isSDKVersionOk(activity)) {
					StringBuilder locations = new StringBuilder();
					for (String s : event.getLocation()) {
						locations.append(s);
						locations.append(", ");
					}
					locations.delete(locations.length() - 2, locations.length()); 
					Intent calIntent = new Intent(Intent.ACTION_INSERT);
					calIntent.setType("vnd.android.cursor.item/event");
					calIntent.putExtra(Events.TITLE, event.getTitle());
					calIntent.putExtra(Events.EVENT_LOCATION, locations.toString());
					calIntent.putExtra(Events.DESCRIPTION, event.getDescription());
					calIntent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, event.getEventStart().getTimeInMillis());
					calIntent.putExtra(CalendarContract.EXTRA_EVENT_END_TIME, event.getEventEnd().getTimeInMillis());
					TimeZone timeZone = TimeZone.getDefault();
					calIntent.putExtra(CalendarContract.Events.EVENT_TIMEZONE, timeZone.getID());

					// default calendar
					calIntent.putExtra(CalendarContract.Events.CALENDAR_ID, 1);
					calIntent.setData(CalendarContract.Events.CONTENT_URI);
					startActivity(calIntent);
				}
			} catch (Exception e) {
				Toaster.otherException(activity);
			}
			return super.onOptionsItemSelected(item);
		}
		return super.onOptionsItemSelected(item);
	}

	@UiThread
	public void showSubscribersDialog(List<String> result) {
		OrientationLocker.lockScreenOrientation(activity);
		if (result.size() != 0) {
			StringBuilder sBuilder = new StringBuilder();
			int size = result.size();
			for (int i = 0; i < size; ++i) {
				sBuilder.append(result.get(i));
				if (i != size - 1) {
					sBuilder.append("\n");
				}
			}
			AlertDialog.Builder builder = new AlertDialog.Builder(activity);
			builder.setCancelable(false);
			builder.setTitle(subscribers).setMessage(sBuilder).setPositiveButton("OK", this).create().show();
		}
	}

	@Override
	public void onClick(DialogInterface dialog, int which) {
		OrientationLocker.unlockScreenOrientation(activity);
	}

	@UiThread
	public void afterMark(boolean b) {
		if (b) {
			if (!SystemFunctions.isLargeLandscape(getActivity())) {
				activity.onBackPressed();
			} else {
				if (ActivityMain.fragment instanceof FragmentWelcome) {
					fragLargeTitles.getEvents();
					if (btnSubscribe.getText().equals(subscribe)) {
						btnSubscribe.setText(unsubscribe);
					} else {
						btnSubscribe.setText(subscribe);
					}
				} else {
					activity.onBackPressed();
				}
			}
		}
	}

}