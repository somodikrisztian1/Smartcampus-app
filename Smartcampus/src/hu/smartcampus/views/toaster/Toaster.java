package hu.smartcampus.views.toaster;

import hu.smartcampus.R;
import hu.smartcampus.functions.ApplicationFunctions;
import hu.smartcampus.functions.SystemFunctions;
import hu.smartcampus.views.activities.ActivityMain;
import android.content.Context;
import android.os.Build;
import android.support.v4.app.FragmentActivity;
import android.widget.Toast;

public class Toaster {

	public static boolean isOnlineAndLoggedIn(Context context) {
		boolean a;
		boolean b;
		if (!SystemFunctions.isOnline(context)) {
			a = false;
		} else {
			a = true;
		}
		if (!ApplicationFunctions.getInstance().getUserFunctions().getLoginSatus()) {
			b = false;
		} else {
			b = true;
		}
		if (a) {
			if (!b) {
				Toast.makeText(context, R.string.plsLogin, Toast.LENGTH_LONG).show();
			}
		} else {
			if (b) {
				Toast.makeText(context, R.string.plsNet, Toast.LENGTH_LONG).show();
			} else {
				Toast.makeText(context, R.string.plsNetAndLogin, Toast.LENGTH_LONG).show();
			}
		}
		return a && b;
	}

	public static boolean isOnline(Context context) {
		boolean b = SystemFunctions.isOnline(context);
		if (!b) {
			Toast.makeText(context, R.string.functionNet, Toast.LENGTH_LONG).show();
		}
		return b;
	}

	public static void timeout(Context context) {
		Toast.makeText(context, R.string.lowNet, Toast.LENGTH_LONG).show();
	}

	public static void otherException(Context context) {
		Toast.makeText(context, R.string.error, Toast.LENGTH_LONG).show();
	}

	public static void setDates(Context context) {
		Toast.makeText(context, R.string.setDate, Toast.LENGTH_LONG).show();
	}

	public static void setFields(FragmentActivity activity) {
		Toast.makeText(activity, R.string.fillFields, Toast.LENGTH_LONG).show();
	}

	public static void loginError(ActivityMain activity) {
		Toast.makeText(activity, R.string.badIDorPass, Toast.LENGTH_LONG).show();
	}

	public static boolean isSDKVersionOk(FragmentActivity activity) {
		boolean b = Build.VERSION.SDK_INT >= 14;
		if (!b) {
			Toast.makeText(activity, R.string.ics, Toast.LENGTH_LONG).show();
		}
		return b;
	}

	public static void fromLocal(FragmentActivity activity) {
		Toast.makeText(activity, R.string.notFresh, Toast.LENGTH_LONG).show();
	}

	public static void noWifiResults(FragmentActivity activity) {
		Toast.makeText(activity, R.string.noWifiResults, Toast.LENGTH_LONG).show();
	}

	public static void noAccessPoints(FragmentActivity activity) {
		Toast.makeText(activity, R.string.noAccessPoints, Toast.LENGTH_LONG).show();
	}

	public static void noItemsForCalendarInsert(FragmentActivity activity) {
		Toast.makeText(activity, R.string.noItemsForCalendar, Toast.LENGTH_LONG).show();
	}

}