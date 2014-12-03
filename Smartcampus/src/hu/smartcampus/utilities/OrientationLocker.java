package hu.smartcampus.utilities;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.view.Surface;
import android.view.WindowManager;

// Szabályozza, hogy mikor lehet forgatni az alkalmazást
public class OrientationLocker {

	private static boolean lockStatus = false;
	
	public static boolean isLocked() {
		return lockStatus;
	}
	
	public static void lockScreenOrientation(Activity activity) {
		switch (((WindowManager) activity.getSystemService(Context.WINDOW_SERVICE))
		        .getDefaultDisplay().getRotation()) {
		    case Surface.ROTATION_90: 
		        activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE); 
		        break;
		    case Surface.ROTATION_180: 
		    	activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT); 
		        break;          
		    case Surface.ROTATION_270: 
		    	activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE); 
		        break;
		    default : 
		    	activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT); 
		    } 
		lockStatus = true;
	}
	 
	public static void unlockScreenOrientation(Activity activity) {
	    activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
	    lockStatus = false;
	}
	
}