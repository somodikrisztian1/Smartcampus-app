package hu.smartcampus.views.fragments;

import hu.smartcampus.R;
import hu.smartcampus.bcastreceivers.BcastReceiverWifiState;
import hu.smartcampus.entities.Event;
import hu.smartcampus.entities.FilterItem;
import hu.smartcampus.functions.ApplicationFunctions;
import hu.smartcampus.templates.CustomActionBarActivity;
import hu.smartcampus.utilities.OrientationLocker;
import hu.smartcampus.views.toaster.Toaster;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.FragmentById;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.res.StringRes;

import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.widget.ArrayAdapter;

@EFragment(R.layout.fragment_events_nearby)
public class FragmentNearby extends Fragment {

	CustomActionBarActivity activity;
	private BcastReceiverWifiState receiver;
	private IntentFilter filter;
	private boolean shouldRedo = true;

	@StringRes
	String nearby;

	@FragmentById
	Fragment fragNormalNearby;

	public boolean isShouldRedo() {
		return shouldRedo;
	}

	public void setShouldRedo(boolean shouldRedo) {
		this.shouldRedo = shouldRedo;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		receiver = new BcastReceiverWifiState(this);
		filter = new IntentFilter(WifiManager.NETWORK_STATE_CHANGED_ACTION);
		setRetainInstance(true); // emiatt nincs bundle mert nincs destroy
	}

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		this.activity = (CustomActionBarActivity) getActivity();
		activity.getSupportActionBar().setTitle(nearby);
		Toaster.isOnline(this.activity);
	}

	@Override
	public void onResume() {
		super.onResume();
		activity.registerReceiver(receiver, filter);
	}

	@Override
	public void onPause() {
		super.onPause();
		activity.unregisterReceiver(receiver);
		shouldRedo = false;
	}

	@SuppressWarnings("unchecked")
	@UiThread
	public void update(String title, List<Event> result) {
		if (title != null) {
			nearby = title;
			activity.getSupportActionBar().setTitle(title);
		}
		if (result != null) {
			FragmentTitles titlesFrag = (FragmentTitles) activity.getSupportFragmentManager().findFragmentById(R.id.fragNormalNearby);
			ArrayAdapter<Event> adapter = (ArrayAdapter<Event>) titlesFrag.getListAdapter();
			adapter.clear();
			for (Event e : result) {
				adapter.add(e);
			}
			adapter.notifyDataSetChanged();
		}
	}

	@Background
	public void roomList(ScanResult bestAccessPoint) {
		if (!OrientationLocker.isLocked()) {
			activity.showDialogAndLock();
		}
		ArrayList<Event> list = null;
		String actionbarTitle = null;
		boolean success = false;
		try {
			String ssid = bestAccessPoint.SSID;
			String bssid = bestAccessPoint.BSSID;
			int level = bestAccessPoint.level;
			List<String> roomList = ApplicationFunctions.getInstance().getMappingFunctions().roomList(ssid, bssid, level);
			int size = roomList.size();
			if (size > 0) {
				actionbarTitle = roomList.get(0);
				if (size > 1) {
					ArrayList<Long> ids = new ArrayList<Long>();
					for (int i = 1; i < size; ++i) {
						String roomName = roomList.get(i);
						List<FilterItem> locations = ApplicationFunctions.getInstance().getCalendarFunctions().locationsFiltered(roomName);
						for (FilterItem f : locations) {
							ids.add(f.getId());
						}
					}
					if (ids.size() != 0) {
						String sessionId = null;
						if (ApplicationFunctions.getInstance().getUserFunctions().getLoginSatus()) {
							sessionId = ApplicationFunctions.getInstance().getUserFunctions().getLoggedInUser().getSessionId();
						}
						HashMap<String, ArrayList<Long>> filters = new HashMap<String, ArrayList<Long>>();
						filters.put("isNearby", new ArrayList<Long>());
						filters.put("locations", ids);
						Calendar from = Calendar.getInstance();
						Calendar to = Calendar.getInstance();
						to.set(Calendar.MINUTE, to.get(Calendar.MINUTE) + 1);
						List<Event> listFiltered = ApplicationFunctions.getInstance().getCalendarFunctions()
								.listFiltered(sessionId, from, to, filters);
						if (listFiltered.size() > 0) {
							Collections.sort(listFiltered);
							list = (ArrayList<Event>) listFiltered;
						}
					}
				}
			}
			success = true;
		} catch (Exception e) {
			activity.handleError(e);
		}
		if (success) {
			update(actionbarTitle, list);
		}
		if (OrientationLocker.isLocked()) {
			activity.cancelDialogAndUnlock();
		}
	}

}