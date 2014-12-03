package hu.smartcampus.views.fragments;

import hu.smartcampus.R;
import hu.smartcampus.adapters.EventAdapter;
import hu.smartcampus.dao.EventsDAOImpl;
import hu.smartcampus.entities.Event;
import hu.smartcampus.entities.User;
import hu.smartcampus.functions.ApplicationFunctions;
import hu.smartcampus.functions.SystemFunctions;
import hu.smartcampus.templates.CustomActionBarActivity;
import hu.smartcampus.threads.BackgroundOperations;
import hu.smartcampus.views.activities.ActivityDetails_;
import hu.smartcampus.views.activities.ActivityEvents;
import hu.smartcampus.views.activities.ActivityMain;
import hu.smartcampus.views.toaster.Toaster;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.UiThread;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ListFragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.SearchView.OnQueryTextListener;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

@EFragment
public class FragmentTitles extends ListFragment implements OnQueryTextListener, android.support.v4.view.MenuItemCompat.OnActionExpandListener {

	private CustomActionBarActivity context;
	private ArrayAdapter<Event> adapter;
	private ArrayAdapter<Event> tmpAdapter;
	private boolean shouldSort = true;
	private ArrayList<Event> sortedList;
	private String query;
	private MenuItem menuItem;
	private FragmentTitles fragLargeTitles;
	private FragmentDetails fragLargeDetails;
	
	@Bean
	BackgroundOperations bg;
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		context = (CustomActionBarActivity) getActivity();
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
		setRetainInstance(true);
		
		if (ActivityMain.fragment instanceof FragmentWelcome) {
			getEvents();
		}
		else if(ActivityMain.fragment instanceof FragmentPersonalData) {
			getMarkedEvents();
		}
		if(adapter == null) {
			User user = ApplicationFunctions.getInstance().getUserFunctions().getLoggedInUser();
			if(getActivity() instanceof ActivityEvents && user != null && user.getSessionId() != null) {
				adapter = new EventAdapter<Event>(
						context, R.layout.custom_list_item, new ArrayList<Event>());
				tmpAdapter = new EventAdapter<Event>(
						context, R.layout.custom_list_item, new ArrayList<Event>());
			}
			else {
				adapter = new ArrayAdapter<Event>(
						context, R.layout.custom_list_item, new ArrayList<Event>());
				tmpAdapter = new ArrayAdapter<Event>(
						context, R.layout.custom_list_item, new ArrayList<Event>());
			}
			setListAdapter(adapter);
		}
		else {
			setListAdapter(adapter);
		}
	}
	
//	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//	    View v = super.onCreateView(inflater, container, savedInstanceState);
//	    ViewGroup parent = (ViewGroup) inflater.inflate(R.layout.fragment_titles, container, false);
//	    parent.addView(v, 0);
//	    return parent;
//	} 
	
	@SuppressWarnings("unchecked")
	void getEvents() {
		Bundle extras = context.getIntent().getExtras();
		Calendar[] dates = new Calendar[2];
		dates[0] = (Calendar) extras.get("from");
		dates[1] = (Calendar) extras.get("to");
		ArrayList<Long> selectedCategories = (ArrayList<Long>) extras.get("selectedCategories");
		ArrayList<Long> selectedProviders = (ArrayList<Long>) extras.get("selectedProviders");
		ArrayList<Long> selectedLocations = (ArrayList<Long>) extras.get("selectedLocations");
		HashMap<String, ArrayList<Long>> filters = new HashMap<String, ArrayList<Long>>();
		if(selectedCategories != null && selectedCategories.size() != 0) {
			filters.put("categories", selectedCategories);
		}
		if(selectedProviders != null && selectedProviders.size() != 0) {
			filters.put("providers", selectedProviders);
		}
		if(selectedLocations != null && selectedLocations.size() != 0) {
			filters.put("locations", selectedLocations);
		}
		bg.listFiltered(dates, filters, this);
	}

	@UiThread
	public void loadFromLocal() {
		Log.d("lol", "loadfromlocal");
		Toaster.fromLocal(getActivity());
		EventsDAOImpl ed = new EventsDAOImpl(getActivity().getApplicationContext());
		List<Event> markedEvents = ed.getMarkedEvents();
		updateAdapter(markedEvents);
	}
	
	@Override
	public void onResume() {
		super.onResume();
		if(FragmentDetails.btnPressed && !SystemFunctions.isLargeLandscape(getActivity())) {
			if(ActivityMain.fragment instanceof FragmentPersonalData) {
				getMarkedEvents();
			}
			else if(ActivityMain.fragment instanceof FragmentWelcome){
				getEvents();
			}
		}
		FragmentDetails.btnPressed = false;
	}
	
	void getMarkedEvents() {
		if(SystemFunctions.isOnline(getActivity().getApplicationContext())) {
			if(ApplicationFunctions.getInstance().getUserFunctions().getLoginSatus()) {
				Log.d("lol", "listmarked");
				bg.listMarked(this);
			}
		}
		else {
			loadFromLocal();
		}
	}
	
	private void findFragments() {
		fragLargeTitles = (FragmentTitles) getFragmentManager().findFragmentById(R.id.fragLargeTitles);
		fragLargeDetails = (FragmentDetails) getFragmentManager().findFragmentById(R.id.fragLargeDetails);
	}
	
	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		if(SystemFunctions.isLargeLandscape(getActivity())) {
			findFragments();
		}
		else {
			fragLargeTitles = null;
			fragLargeDetails = null;
		}
		if(getId() == R.id.fragNormalWelcome || getId() == R.id.fragNormalPD || getId() == R.id.fragNormalNearby) {
			getListView().setBackgroundResource(R.drawable.deik);
		}
		
	}
	
	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		Event event = null;
		if(MenuItemCompat.isActionViewExpanded(menuItem) && query != null && query.length() > 1) {
			event = tmpAdapter.getItem(position);
		}
		else {
			event = adapter.getItem(position);
		}
		if (!SystemFunctions.isLargeLandscape(getActivity())) {
			Log.d("lol", "itten");
//			if(ActivityMain.fragment instanceof FragmentPersonalData ||
//					ActivityMain.fragment instanceof FragmentNearby) {
				Intent intent = new Intent(context, ActivityDetails_.class);
				intent.putExtra("event", event);
//				intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
				startActivity(intent);
//			}
//			else if(ActivityMain.fragment instanceof FragmentWelcome){
//				Fragment details = new FragmentDetails_();
//				selectedEvent = event;
//				String tag = "" + position;
//				details.setTargetFragment(this, 1);
//				fm.beginTransaction().add(R.id.fragWelcome, details, tag).addToBackStack(tag).commit();
//			}
		}
		else {
			if(fragLargeDetails != null) {
				fragLargeDetails.updateContent(event);
			}
	    }
		
	}
	
	@UiThread
	public void updateAdapter(List<Event> result) {
		if(result != null) {
			adapter.clear();
	        for(Event item : result) {
	        	adapter.add(item);
	        }
	        adapter.notifyDataSetChanged();
		}
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
		    case android.R.id.home:
		    	getActivity().onBackPressed();	// TODO parentactivity sen
		        return true;
	    }
	    return false;
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		if(ActivityMain.fragment instanceof FragmentWelcome) {
			menu.clear();
		}
		inflater.inflate(R.menu.menu_fragment_titles, menu);
		menuItem = menu.findItem(R.id.menuSearch);
		SearchView searchView = (SearchView) MenuItemCompat.getActionView(menuItem);
		searchView.setOnQueryTextListener(this);
		MenuItemCompat.setOnActionExpandListener(menuItem, this);
		MenuItemCompat.setShowAsAction(menuItem, MenuItemCompat.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW | MenuItemCompat.SHOW_AS_ACTION_IF_ROOM);
		if(query != null && !query.equals("")) {  // TODO nemtom mér
//			Log.d("lol", query == "" ? "nem null2" : "null2");  ilyenkor referenciat nez
			String queryCopy = query;				// értékmásolás még jókor
			MenuItemCompat.expandActionView(menuItem);
	        searchView.setQuery(queryCopy, false);
//	        searchView.clearFocus(); 
		}
	}

	@Override
	public boolean onQueryTextChange(String text) {
		query = text;	// expandaction után ez lefut ugy hogy már a text = "", igy a query is azlesz (értékmásolás), == értékvizsgálatot végez
		if(text.length() < 2) {
			if(adapter != getListAdapter()) {
				setListAdapter(adapter);
			}
		}
		else {
			if(shouldSort) {
				sortAdapter();
			}
			tmpAdapter.clear();
			for(Event item : sortedList) {
				if(item.getTitle().toLowerCase(Locale.getDefault()).startsWith(text.toLowerCase(Locale.getDefault()))) {
					tmpAdapter.add(item);
				}
				else if(tmpAdapter.getCount() > 0) {
					setListAdapter(tmpAdapter);
					break;
				}
			}
			if(tmpAdapter.getCount() == 0) {
				setListAdapter(tmpAdapter);
			}
		}
		return true;
	}

	private void sortAdapter() {
		shouldSort = false;
		sortedList = new ArrayList<Event>();
		for(int i = 0; i < adapter.getCount(); ++i) {
			sortedList.add(adapter.getItem(i));
		}
		Collections.sort(sortedList);
	}

	@Override
	public boolean onQueryTextSubmit(String text) {
		return false;
	}

	@Override
	public boolean onMenuItemActionCollapse(MenuItem arg0) {
		setListAdapter(adapter);
		query = null;
		return true;
	}

	@Override
	public boolean onMenuItemActionExpand(MenuItem arg0) {
		return true;
	}
	
}