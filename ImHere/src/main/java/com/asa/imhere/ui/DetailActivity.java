package com.asa.imhere.ui;

import android.app.ActionBar;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;

import com.asa.imhere.AppData;
import com.asa.imhere.R;

public class DetailActivity extends AsaBaseActivity {

	private String mVenueId;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_detail);

		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setHomeButtonEnabled(true);

		// Get the venue Id
		Bundle extras = getIntent().getExtras();
		if (extras == null) {
			throw new IllegalStateException("You must pass either a VenueID or a Venue into the DetailActivity.");
		}
		mVenueId = extras.getString(AppData.Extras.VENUE_ID);

		if (savedInstanceState == null) {
			addFragment(DetailFragment.newInstance(mVenueId), DetailFragment.TAG, false, false);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.detail, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		switch (id) {
		case android.R.id.home:
			goBack();
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	private void goBack() {
		// TODO - Right now, it's going back to the main activity. Fix that.
		NavUtils.navigateUpFromSameTask(this);
	}

}
