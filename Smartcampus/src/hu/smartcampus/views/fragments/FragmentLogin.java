package hu.smartcampus.views.fragments;

import hu.smartcampus.R;
import hu.smartcampus.templates.CustomActionBarActivity;
import hu.smartcampus.threads.BackgroundOperations;
import hu.smartcampus.views.activities.ActivityMain;
import hu.smartcampus.views.toaster.Toaster;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.res.StringRes;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.widget.EditText;

@EFragment(R.layout.fragment_login)
public class FragmentLogin extends Fragment
{
	
	private CustomActionBarActivity activity;
	
	@ViewById
	EditText txtEmail;
	@ViewById
	EditText txtPassword;
	@StringRes
	String loginTitle;
	@Bean
	BackgroundOperations bg;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRetainInstance(true);
	}
	
	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		activity = (CustomActionBarActivity) getActivity();
		activity.getSupportActionBar().setTitle(loginTitle);
	}
	
	@Click(R.id.btnLogin)
	void queryBtnClicked() {
		String username = txtEmail.getText().toString();
		String password = txtPassword.getText().toString();
		if(!username.equals("") && !password.equals("")) {
			if (Toaster.isOnline(getActivity()))
			{
				bg.openSession(username, password, this);
			}
		}
		else {
			Toaster.setFields(getActivity());
		}
	}
	
	@UiThread
	public void afterLogin(boolean success) {
		ActivityMain a = (ActivityMain) activity;
		if(success) {
			a.loginOrPersonal();
			a.onBackPressed();
		}
		else {
			Toaster.loginError(a);
		}
	}
	
}