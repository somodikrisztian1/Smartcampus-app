package hu.smartcampus.bcastreceivers;

import hu.smartcampus.views.fragments.FragmentNearby;
import hu.smartcampus.views.toaster.Toaster;

import java.util.List;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;

public class BcastReceiverWifiState extends BroadcastReceiver {

	private FragmentNearby fragment;

	public BcastReceiverWifiState() {
		super();
	}

	public BcastReceiverWifiState(FragmentNearby fragment) {
		super();
		this.fragment = fragment;
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		final String action = intent.getAction();
		if (fragment.isShouldRedo()) {
			if (action.equals(WifiManager.NETWORK_STATE_CHANGED_ACTION)) {
				NetworkInfo info = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
				boolean connected = info.isConnected();
				if (connected) {
					if (Toaster.isOnline(context)) {
						ScanResult bestAccessPoint = loadWifiAvailableList();
						if (bestAccessPoint != null) {
							fragment.roomList(bestAccessPoint);
						}
					}
				}
			}
		} else {
			fragment.setShouldRedo(true);
		}

		// Bundle extras = intent.getExtras();
		// NetworkInfo netInfo = (NetworkInfo)
		// extras.get(WifiManager.WIFI_STATE_CHANGED_ACTION);
		//
		// if(netInfo.getState().equals(NetworkInfo.State.CONNECTED)) {
		// WifiManager wifi = (WifiManager)
		// context.getSystemService(Context.WIFI_SERVICE);
		// WifiInfo connectionInfo = wifi.getConnectionInfo();
		//
		// int numberOfLevels=5;
		// int level =
		// WifiManager.calculateSignalLevel(connectionInfo.getRssi(),
		// numberOfLevels);
		//
		// AsyncRequestRoomList async = new AsyncRequestRoomList();
		// async.execute(connectionInfo.getSSID(), connectionInfo.getBSSID(),
		// level);
		// }

	}

	// http://stackoverflow.com/questions/12460445/how-to-get-bssid-of-all-wifi-access-points
	public ScanResult loadWifiAvailableList() {
		ScanResult bestSignal = null;
		WifiManager wifiManager = (WifiManager) fragment.getActivity().getSystemService(Context.WIFI_SERVICE);
		List<ScanResult> results = wifiManager.getScanResults();
		if (results != null) {
			final int size = results.size();
			if (size == 0) {
				Toaster.noAccessPoints(fragment.getActivity());
			} else {
				bestSignal = results.get(0);
				for (ScanResult result : results) {
					if (WifiManager.compareSignalLevel(bestSignal.level, result.level) < 0) {
						if (result.SSID.equals("eduroam")) {
							bestSignal = result;
						}
					}
				}
			}
		} else {
			Toaster.noWifiResults(fragment.getActivity());
		}
		return bestSignal;
	}

}