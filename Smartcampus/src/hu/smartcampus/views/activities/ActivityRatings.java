package hu.smartcampus.views.activities;

import hu.smartcampus.R;
import hu.smartcampus.templates.CustomActionBarActivity;

import org.androidannotations.annotations.EActivity;

import android.view.MenuItem;

@EActivity(R.layout.activity_ratings)
public class ActivityRatings extends CustomActionBarActivity {
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if(item.getItemId() == android.R.id.home) {   // TODO kipróbálni egy API level < 11 -en 
			onBackPressed();
		}
		return super.onOptionsItemSelected(item);
	}
	
}