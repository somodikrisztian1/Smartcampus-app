package hu.smartcampus.functions;

import java.io.ByteArrayOutputStream;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.util.Base64;

public class SystemFunctions {

	private SystemFunctions() {
	};

	public static void makePhoneCallFromNumber(String phoneNumber, Context context) {
		Intent intent = new Intent(Intent.ACTION_CALL);
		intent.setData(Uri.parse("tel:" + phoneNumber));
		context.startActivity(intent);
	}

	public static void sendSmsFromNumber(String phoneNumber, Context context) {
		Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("sms:" + phoneNumber));
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(intent);
	}

	public static String bitMapToString(Bitmap image) {
		// http://stackoverflow.com/questions/9768611/encode-and-decode-bitmap-object-in-base64-string-in-android
		if (image == null) {
			return null;
		}
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		image.compress(Bitmap.CompressFormat.JPEG, 100, baos);
		byte[] b = baos.toByteArray();
		String imageEncoded = Base64.encodeToString(b, Base64.DEFAULT);
		return imageEncoded;
	}

	public static Bitmap stringToBitmap(String input) {
		// http://stackoverflow.com/questions/9768611/encode-and-decode-bitmap-object-in-base64-string-in-android
		if (input != null) {
			byte[] decodedByte = Base64.decode(input, 0);
			return BitmapFactory.decodeByteArray(decodedByte, 0, decodedByte.length);
		} else {
			return null;
		}
	}

	public static boolean isOnline(Context context) {
		// megnézi hogy most van-e kapcsolat
		ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = cm.getActiveNetworkInfo();
		if (netInfo != null && netInfo.isConnected()) // netInfo.isConnectedOrConnecting()
		{
			return true;
		}
		return false;
	}

	public static boolean isLargeLandscape(Context context) {
		if (((context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_LARGE)
				&& (context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE)) {
			return true;
		}
		return false;
	}

}