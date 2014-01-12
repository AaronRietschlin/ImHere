package com.asa.imhere.ui;

import android.app.ActionBar;
import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;
import android.provider.SearchRecentSuggestions;
import android.support.v4.app.NavUtils;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MenuItem.OnActionExpandListener;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import com.asa.imhere.AppData;
import com.asa.imhere.R;
import com.asa.imhere.SearchSuggestionContentProvider;
import com.asa.imhere.VenueAdapter;
import com.asa.imhere.foursquare.Foursquare;
import com.asa.imhere.foursquare.FsVenue;
import com.asa.imhere.model.responses.SearchResponse;
import com.asa.imhere.utils.PreferenceUtils;
import com.asa.imhere.utils.Utils;
import com.crashlytics.android.Crashlytics;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.koushikdutta.async.future.Future;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

public class SearchActivity extends Activity implements OnActionExpandListener, SearchView.OnQueryTextListener, SearchView.OnSuggestionListener, FutureCallback<SearchResponse>,
		OnItemClickListener {
	private final static String TAG = "SearchActivity";

	private ListView mListView;
	private View mLayoutLoading;
	private TextView mTvEmpty;
	private SearchView mSearchView;

	private VenueAdapter mAdapter;

	private ActionBar mActionBar;
	private Gson mGson;

	private double mLat;
	private double mLon;
	private boolean mIsCoordsSet;

	private Future<SearchResponse> mLastRequest;

	// TODO - Use Volley or Ion?

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_explore);

		mListView = (ListView) findViewById(R.id.explore_list);
		mLayoutLoading = findViewById(R.id.loading);
		mTvEmpty = (TextView) findViewById(R.id.explore_empty_text);

		// Initialize this to null because we are going to be using the
		// ActionBar
		mLayoutLoading.setVisibility(View.GONE);

		setupActionBar();
		mAdapter = new VenueAdapter(getApplicationContext());
		mListView.setAdapter(mAdapter);
		mListView.setOnItemClickListener(this);
		mGson = new Gson();

		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			mLat = extras.getDouble(AppData.Extras.LATITUDE);
			mLon = extras.getDouble(AppData.Extras.LONGITUDE);
			mIsCoordsSet = true;
		} else {
			setupCoordsIfNeeded();
		}
	}

	private void setupActionBar() {
		mActionBar = getActionBar();
		mActionBar.setDisplayHomeAsUpEnabled(true);
		mActionBar.setHomeButtonEnabled(true);
	}

	private void setupCoordsIfNeeded() {
		double[] coords = PreferenceUtils.getMostRecentLatLon(getApplicationContext());
		if (coords != null && coords.length == 2) {
			mLat = coords[0];
			mLon = coords[1];
		}
	}

	private void performSearch(String query) {
		if (!mIsCoordsSet) {
			setupCoordsIfNeeded();
		}
		if (mLastRequest != null && !mLastRequest.isCancelled() && !mLastRequest.isDone()) {
			mLastRequest.cancel(true);
		}
		String url = Foursquare.constructSearchUrl(query, mLat, mLon, getApplicationContext());
		setProgressBarIndeterminateVisibility(true);
		mLastRequest = Ion.with(this, url).as(new TypeToken<SearchResponse>() {}).setCallback(this);
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

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.search, menu);

		MenuItem item = menu.findItem(R.id.menu_search);
		item.expandActionView();
		item.setOnActionExpandListener(this);
		setupSearchView((SearchView) item.getActionView());

		return super.onCreateOptionsMenu(menu);
	}

	private void goBack() {
		NavUtils.navigateUpFromSameTask(this);
	}

	private void setupSearchView(SearchView view) {
		mSearchView = view;
		// view.setQueryHint(getString(R.string.search_query_hint));
		view.setQueryRefinementEnabled(true);
		view.setOnQueryTextListener(this);
		view.setOnSuggestionListener(this);
		// view.setInputType(InputType.TYPE_TEXT_FLAG_CAP_WORDS);

		// TODO - Probably won't use this.
		SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
		view.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
		view.setIconifiedByDefault(false);
	}

	@Override
	public boolean onMenuItemActionCollapse(MenuItem item) {
		goBack();
		return true;
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View v, int position, long id) {
		FsVenue venue = (FsVenue) mAdapter.getItem(position);
		if (venue == null) {
			// TODO - Handle this.
			return;
		}
		String venueId = venue.getId();
		Utils.launchDetailActivity(this, venueId, v);

		saveQuery(venue.getName(), null);
	}

	private void itemSelected(int position) {

	}

	@Override
	public boolean onMenuItemActionExpand(MenuItem item) {
		// DO nothing. It's being expanded on load.
		return false;
	}

	@Override
	public boolean onSuggestionClick(int position) {
		Log.d(TAG, "Suggestion Item Clicked: Pos " + position);
		// TODO - Handle this. Make the Text go into the SearchView.Need to
		// obtain the proper text first.

		// SearchSuggestionContentProvider provider = new
		// SearchSuggestionContentProvider();
		// provider.query(uri, projection, selection, selectionArgs, sortOrder);
		// mSearchView.setQuery(query, false);
		return true;
	}

	@Override
	public boolean onSuggestionSelect(int position) {
		Log.d(TAG, "Suggestion Item Selected: Pos " + position);
		return true;
	}

	@Override
	public boolean onQueryTextChange(String newText) {
		if (TextUtils.isEmpty(newText)) {
			mAdapter.clear(true);
			return false;
		}
		try {
			newText = URLEncoder.encode(newText, "utf-8");
		} catch (UnsupportedEncodingException e) {
			Crashlytics.logException(e);
			Crashlytics.log(Log.ERROR, TAG, "Error encoding string: " + newText);
			Crashlytics.setString("failed_encoding_str", newText);
		}
		performSearch(newText);
		return false;
	}

	@Override
	public boolean onQueryTextSubmit(String query) {
		Log.d(TAG, "Query Text Submit: " + query);
		saveQuery(query, null);
		return true;
	}

	@Override
	public void onCompleted(Exception e, SearchResponse result) {
		Log.d(TAG, "Ion request completed.");
		handleResponse(result);
		setProgressBarIndeterminateVisibility(false);
	}

	private void handleResponse(SearchResponse result) {
		if (result != null) {
			// Add them to a List of Nameable objects to be displayed in the
			// VenueAdapter
			SearchResponse.Response searchResponse = result.getResponse();
			if (searchResponse == null) {
				Log.d(TAG, "SearchResponse.Response item is null.");
				// TODO - inform user
				return;
			}
			mAdapter.clear(true);
			List<FsVenue> venues = searchResponse.getVenues();
			if (venues == null) {
				Log.d(TAG, "SearchResponse venues is null.");
				// TODO - inform user
				return;
			}
			for (FsVenue venue : venues) {
				mAdapter.addItem(venue);
			}
			mAdapter.notifyDataSetChanged();
		}
	}

	// TODO - Allow the user to clear suggestions!!
	private void saveQuery(String query, String secondItem) {
		SearchRecentSuggestions suggestions = new SearchRecentSuggestions(this, SearchSuggestionContentProvider.AUTHORITY, SearchSuggestionContentProvider.MODE);
		suggestions.saveRecentQuery(query, secondItem);
	}

}