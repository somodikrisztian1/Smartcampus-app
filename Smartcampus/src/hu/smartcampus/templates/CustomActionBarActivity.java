package hu.smartcampus.templates;

import hu.smartcampus.utilities.OrientationLocker;
import hu.smartcampus.views.toaster.Toaster;

import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.res.StringRes;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;

@EBean
public class CustomActionBarActivity extends ActionBarActivity {

	private ProgressDialog progressDialog;

	@StringRes
	protected String pleaseWait;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ActionBar supportActionBar = getSupportActionBar();
		supportActionBar.setDisplayShowTitleEnabled(true);
		supportActionBar.setDisplayHomeAsUpEnabled(true);
		supportActionBar.setHomeButtonEnabled(true);
	}

	@UiThread
	public void showDialogAndLock() {
		OrientationLocker.lockScreenOrientation(this);
		progressDialog = new ProgressDialog(this);
		progressDialog.setMessage(pleaseWait);
		progressDialog.setCancelable(false);
		progressDialog.setCanceledOnTouchOutside(false);
		progressDialog.show();
	}

	@UiThread
	public void cancelDialogAndUnlock() {
		progressDialog.dismiss();
		OrientationLocker.unlockScreenOrientation(this);
	}

	public void handleError(Exception e) {
		e.printStackTrace();
		if (e instanceof SocketTimeoutException) {
			showErrorToast(true);
		} else if (e instanceof UnknownHostException) {
			showErrorToast(true);
		} else {
			showErrorToast(false);
		}
	}

	@UiThread
	public void showErrorToast(boolean b) {
		if (b) {
			Toaster.timeout(this);
		} else {
			Toaster.otherException(this);
		}
	}

}