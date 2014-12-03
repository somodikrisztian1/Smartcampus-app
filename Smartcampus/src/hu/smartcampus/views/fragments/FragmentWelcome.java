package hu.smartcampus.views.fragments;

import hu.smartcampus.R;
import hu.smartcampus.entities.FilterItem;
import hu.smartcampus.threads.BackgroundOperations;
import hu.smartcampus.views.activities.*;
import hu.smartcampus.views.toaster.Toaster;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.res.StringRes;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.MultiAutoCompleteTextView;
import android.widget.MultiAutoCompleteTextView.CommaTokenizer;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TimePicker;

// fragment destroy lefut ha továbblépünk innen és nullázódik minden field

@EFragment(R.layout.fragment_welcome)
public class FragmentWelcome extends Fragment implements OnClickListener {

	private static Calendar startDate;
	private static Calendar endDate;
	private static Calendar startTime;
	private static Calendar endTime;
	private FragmentManager fm;
	private ActionBarActivity activity;
	private boolean hideMenu1;
	private boolean hideMenu2;
	private boolean hideMenu3;
	private static int DATE_REQUEST_CODE = 1;
	private static int TIME_REQUEST_CODE = 2;
	private ArrayList<Long> selectedCategories;
	private ArrayList<Long> selectedProviders;
	private ArrayList<Long> selectedLocations;
	private boolean isLocationsVisible;
	private boolean isProvidersVisible;
	private boolean isCategoriesVisible;
	private CommaTokenizer tokenizer = new CommaTokenizer();;

	@ViewById
	EditText textDateFrom;
	@ViewById
	EditText textDateTo;
	@ViewById
	EditText textTimeFrom;
	@ViewById
	EditText textTimeTo;
	@ViewById
	Button btnQuery;
	@ViewById
	MultiAutoCompleteTextView autoCategories;
	@ViewById
	MultiAutoCompleteTextView autoProviders;
	@ViewById
	MultiAutoCompleteTextView autoLocations;
	@StringRes
	String queryEvents;
	@Bean
	BackgroundOperations bg;

	public Calendar[] getDates() {
		if (startDate != null && endDate != null) {
			Calendar[] dates = new Calendar[2];

			Calendar from = Calendar.getInstance();
			Calendar to = Calendar.getInstance();

			from.set(startDate.get(Calendar.YEAR), startDate.get(Calendar.MONTH), startDate.get(Calendar.DAY_OF_MONTH));
			to.set(endDate.get(Calendar.YEAR), endDate.get(Calendar.MONTH), endDate.get(Calendar.DAY_OF_MONTH));

			if (startTime == null && endTime == null) {
				from.set(Calendar.HOUR_OF_DAY, 0);
				from.set(Calendar.MINUTE, 0);
				from.set(Calendar.SECOND, 0);

				to.set(Calendar.HOUR_OF_DAY, 23);
				to.set(Calendar.MINUTE, 59);
				to.set(Calendar.SECOND, 59);
			} else if (startTime != null && endTime != null) {
				from.set(Calendar.HOUR_OF_DAY, startTime.get(Calendar.HOUR_OF_DAY));
				from.set(Calendar.MINUTE, startTime.get(Calendar.MINUTE));
				from.set(Calendar.SECOND, 0);

				to.set(Calendar.HOUR_OF_DAY, endTime.get(Calendar.HOUR_OF_DAY));
				to.set(Calendar.MINUTE, endTime.get(Calendar.MINUTE));
				to.set(Calendar.SECOND, 0);
			} else {
				return null;
			}

			dates[0] = from;
			dates[1] = to;

			return dates;

		}
		return null;
	}

	public void setHideMenu1(boolean hideMenu1) {
		this.hideMenu1 = hideMenu1;
	}

	public void setHideMenu2(boolean hideMenu2) {
		this.hideMenu2 = hideMenu2;
	}

	public void setHideMenu3(boolean hideMenu3) {
		this.hideMenu3 = hideMenu3;
	}

	public MultiAutoCompleteTextView getAutoCategories() {
		return autoCategories;
	}

	public MultiAutoCompleteTextView getAutoProviders() {
		return autoProviders;
	}

	public MultiAutoCompleteTextView getAutoLocations() {
		return autoLocations;
	}

	@SuppressLint("NewApi")
	public static class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

		private FragmentManager fragmentManager;
		private static int HONEYCOMB = 11;

		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			boolean found = false;
			fragmentManager = getFragmentManager();
			Calendar c = null;
			if (fragmentManager.findFragmentByTag("START_DATE") != null && startDate != null) {
				c = startDate;
				found = true;
			} else if (fragmentManager.findFragmentByTag("END_DATE") != null && endDate != null) {
				c = endDate;
				found = true;
			} else if (fragmentManager.findFragmentByTag("START_TIME") != null && startTime != null) {
				c = startTime;
				found = true;
			} else if (fragmentManager.findFragmentByTag("END_TIME") != null && endTime != null) {
				c = endTime;
				found = true;
			}

			if (!found) {
				c = Calendar.getInstance();
			}

			int year = c.get(Calendar.YEAR);
			int month = c.get(Calendar.MONTH);
			int day = c.get(Calendar.DAY_OF_MONTH);
			int hour = c.get(Calendar.HOUR_OF_DAY);
			int minute = c.get(Calendar.MINUTE);

			if ((fragmentManager.findFragmentByTag("START_DATE") != null || (fragmentManager.findFragmentByTag("END_DATE") != null))) {
				DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), this, year, month, day);
				if (android.os.Build.VERSION.SDK_INT >= HONEYCOMB) {
					datePickerDialog.getDatePicker().setDescendantFocusability(DatePicker.FOCUS_BLOCK_DESCENDANTS);
				}
				return datePickerDialog;
			} else {
				return new TimePickerDialog(getActivity(), this, hour, minute, true);
			}

		}

		@Override
		public void onDateSet(DatePicker view, int year, int month, int day) {

			if (fragmentManager.findFragmentByTag("START_DATE") != null) {
				startDate = Calendar.getInstance();
				SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
				startDate.set(Calendar.YEAR, year);
				startDate.set(Calendar.MONTH, month);
				startDate.set(Calendar.DAY_OF_MONTH, day);
				String sDate = format1.format(startDate.getTime());
				Intent result = new Intent();
				result.putExtra("sDate", sDate);
				getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, result);
			} else if (fragmentManager.findFragmentByTag("END_DATE") != null) {
				endDate = Calendar.getInstance();
				SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
				endDate.set(Calendar.YEAR, year);
				endDate.set(Calendar.MONTH, month);
				endDate.set(Calendar.DAY_OF_MONTH, day);
				String eDate = format1.format(endDate.getTime());
				Intent result = new Intent();
				result.putExtra("eDate", eDate);
				getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, result);
			}
		}

		@Override
		public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
			if (fragmentManager.findFragmentByTag("START_TIME") != null) {
				startTime = Calendar.getInstance();
				SimpleDateFormat format1 = new SimpleDateFormat("HH:mm", Locale.getDefault());
				startTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
				startTime.set(Calendar.MINUTE, minute);
				String sTime = format1.format(startTime.getTime());
				Intent result = new Intent();
				result.putExtra("sTime", sTime);
				getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, result);
			} else if (fragmentManager.findFragmentByTag("END_TIME") != null) {
				endTime = Calendar.getInstance();
				SimpleDateFormat format1 = new SimpleDateFormat("HH:mm", Locale.getDefault());
				endTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
				endTime.set(Calendar.MINUTE, minute);
				String eTime = format1.format(endTime.getTime());
				Intent result = new Intent();
				result.putExtra("eTime", eTime);
				getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, result);
			}
		}

		@Override
		public void onPause() {
			fragmentManager.popBackStack();
			super.onPause();
		}

	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
		setRetainInstance(true);
		if (startDate != null) {
			startDate = null;
		}
		if (endDate != null) {
			endDate = null;
		}
		if (startTime != null) {
			startTime = null;
		}
		if (endTime != null) {
			endTime = null;
		}
	}

	@AfterViews
	void afterViews() {
		this.activity = (ActionBarActivity) getActivity();
		fm = activity.getSupportFragmentManager();
		textDateFrom.setOnClickListener(this);
		textDateTo.setOnClickListener(this);
		textTimeFrom.setOnClickListener(this);
		textTimeTo.setOnClickListener(this);
		btnQuery.setOnClickListener(this);
		if (isCategoriesVisible || isLocationsVisible || isProvidersVisible) {
			if (isCategoriesVisible) {
				autoCategories.setVisibility(View.VISIBLE);
			}
			if (isLocationsVisible) {
				autoLocations.setVisibility(View.VISIBLE);
			}
			if (isProvidersVisible) {
				autoProviders.setVisibility(View.VISIBLE);
			}
			updateView();
		}
		ActionBarActivity activity = (ActionBarActivity) getActivity();
		activity.getSupportActionBar().setTitle(queryEvents);
		activity.supportInvalidateOptionsMenu();
	}

	@Override
	public void onClick(View v) {
		FragmentTransaction fragmentTransaction = fm.beginTransaction();
		DatePickerFragment fragment = new DatePickerFragment();
		fragment.setCancelable(false);
		switch (v.getId()) {
		case R.id.textDateFrom:
			fragmentTransaction.add(fragment, "START_DATE");
			fragment.setTargetFragment(this, DATE_REQUEST_CODE);
			break;
		case R.id.textDateTo:
			fragmentTransaction.add(fragment, "END_DATE");
			fragment.setTargetFragment(this, DATE_REQUEST_CODE);
			break;
		case R.id.textTimeFrom:
			fragmentTransaction.add(fragment, "START_TIME");
			fragment.setTargetFragment(this, TIME_REQUEST_CODE);
			break;
		case R.id.textTimeTo:
			fragmentTransaction.add(fragment, "END_TIME");
			fragment.setTargetFragment(this, TIME_REQUEST_CODE);
			break;
		case R.id.btnQuery:
			if (Toaster.isOnline(activity)) {
				Calendar[] dates = getDates();
				if (dates != null) {
					Intent intent = new Intent(activity, ActivityEvents_.class);
					intent.putExtra("from", dates[0]);
					intent.putExtra("to", dates[1]);
					intent.putExtra("selectedCategories", selectedCategories);
					intent.putExtra("selectedProviders", selectedProviders);
					intent.putExtra("selectedLocations", selectedLocations);
					startActivity(intent);
				} else {
					Toaster.setDates(activity);
				}
			}
			return;
		}
		fragmentTransaction.addToBackStack("DTPicker");
		fragmentTransaction.commit();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == Activity.RESULT_OK) {
			if (DATE_REQUEST_CODE == requestCode) {
				String sDate = data.getStringExtra("sDate");
				if (sDate != null) {
					if (startDate != null) {
						textDateFrom.setText(sDate);
					}
				} else {
					if (endDate != null) {
						textDateTo.setText(data.getStringExtra("eDate"));
					}
				}
			} else {
				String sTime = data.getStringExtra("sTime");
				if (sTime != null) {
					if (startTime != null) {
						textTimeFrom.setText(sTime);
					}
				} else {
					if (endTime != null) {
						textTimeTo.setText(data.getStringExtra("eTime"));
					}
				}
			}
		}
	}

	@UiThread
	public void postCategories(ArrayList<FilterItem> result) {
		if (result != null) {
			ArrayAdapter<FilterItem> adapter = new ArrayAdapter<FilterItem>(activity, android.R.layout.simple_dropdown_item_1line, result);
			autoCategories.setTokenizer(tokenizer);
			autoCategories.setAdapter(adapter);
			autoCategories.setVisibility(View.VISIBLE);
			autoCategories.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
					FilterItem item = (FilterItem) parent.getItemAtPosition(position);
					selectedCategories.add(item.getId());
				}

			});
			setHideMenu1(true);
			activity.supportInvalidateOptionsMenu();
			updateView();
		}
	}

	@UiThread
	public void postProviders(ArrayList<FilterItem> result) {
		if (result != null) {
			ArrayAdapter<FilterItem> adapter = new ArrayAdapter<FilterItem>(activity, android.R.layout.simple_dropdown_item_1line, result);
			autoProviders.setTokenizer(tokenizer);
			autoProviders.setAdapter(adapter);
			autoProviders.setVisibility(View.VISIBLE);
			autoProviders.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
					FilterItem item = (FilterItem) parent.getItemAtPosition(position);
					selectedProviders.add(item.getId());
				}

			});
			setHideMenu2(true);
			activity.supportInvalidateOptionsMenu();
			updateView();
		}
	}

	@UiThread
	public void postLocations(ArrayList<FilterItem> result) {
		if (result != null) {
			ArrayAdapter<FilterItem> adapter = new ArrayAdapter<FilterItem>(activity, android.R.layout.simple_dropdown_item_1line, result);
			autoLocations.setTokenizer(tokenizer);
			autoLocations.setAdapter(adapter);
			autoLocations.setVisibility(View.VISIBLE);
			autoLocations.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
					FilterItem item = (FilterItem) parent.getItemAtPosition(position);
					selectedLocations.add(item.getId());
				}

			});
			setHideMenu3(true);
			activity.supportInvalidateOptionsMenu();
			updateView();
		}
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		menu.clear();
		inflater.inflate(R.menu.menu_fragment_welcome, menu);
		if (hideMenu1 && hideMenu2 && hideMenu3) {
			menu.findItem(R.id.addFilter).setVisible(false);
		} else {
			if (hideMenu1) {
				menu.findItem(R.id.filterCategory).setVisible(false);
			}
			if (hideMenu2) {
				menu.findItem(R.id.filterProvider).setVisible(false);
			}
			if (hideMenu3) {
				menu.findItem(R.id.filterLocation).setVisible(false);
			}
		}
		super.onCreateOptionsMenu(menu, inflater);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (Toaster.isOnline(activity)) {
			switch (item.getItemId()) {
			case R.id.filterCategory:
				selectedCategories = new ArrayList<Long>();
				bg.categories(this);
				break;
			case R.id.filterProvider:
				selectedProviders = new ArrayList<Long>();
				bg.providers(this);
				break;
			case R.id.filterLocation:
				selectedLocations = new ArrayList<Long>();
				bg.locations(this);
				break;
			}
		}
		return true;
	}

	public void updateView() {
		if (activity.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
			LayoutParams params = (LayoutParams) btnQuery.getLayoutParams();
			if (autoCategories.getVisibility() == View.VISIBLE) {
				isCategoriesVisible = true;
				params.addRule(RelativeLayout.BELOW, R.id.autoCategories);
			}
			if (autoProviders.getVisibility() == View.VISIBLE) {
				isProvidersVisible = true;
				params.addRule(RelativeLayout.BELOW, R.id.autoProviders);
			}
			if (autoLocations.getVisibility() == View.VISIBLE) {
				isLocationsVisible = true;
				params.addRule(RelativeLayout.BELOW, R.id.autoLocations);
			}
		} else {
			if (!isCategoriesVisible && autoCategories.getVisibility() == View.VISIBLE) {
				isCategoriesVisible = true;
			}
			if (!isProvidersVisible && autoProviders.getVisibility() == View.VISIBLE) {
				isProvidersVisible = true;
			}
			if (!isLocationsVisible && autoLocations.getVisibility() == View.VISIBLE) {
				isLocationsVisible = true;
			}
		}
	}

}