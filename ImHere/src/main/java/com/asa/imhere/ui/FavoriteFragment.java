package com.asa.imhere.ui;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

import com.asa.imhere.AsaBaseFragment;
import com.asa.imhere.R;
import com.asa.imhere.VenueAdapter;
import com.asa.imhere.VenueAdapter.CheckIsFavoritedListener;
import com.asa.imhere.VenueAdapter.OnAddButtonClickListener;
import com.asa.imhere.model.DatabaseQueries;
import com.asa.imhere.model.Favorite;
import com.asa.imhere.model.Nameable;
import com.asa.imhere.otto.BusProvider;
import com.asa.imhere.otto.LocationSavedDataChanged;
import com.asa.imhere.utils.Utils;
import com.squareup.otto.Subscribe;

public class FavoriteFragment extends AsaBaseFragment implements OnAddButtonClickListener, CheckIsFavoritedListener, OnItemClickListener {
	public final static String TAG = "FavoriteFragment";

	private ListView mListView;
	private TextView mTextEmpty;

	private VenueAdapter mAdapter;
	// TODO - move this to MainActivity so the favorites tab can use it?
	private List<Nameable> mFavorites;

	// TODO - Move retrieving favorites off of the UI thread.

	public static FavoriteFragment newInstance() {
		FavoriteFragment frag = new FavoriteFragment();

		return frag;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRetainInstance(true);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_explore, container, false);

		mListView = (ListView) v.findViewById(R.id.explore_list);
		mTextEmpty = (TextView) v.findViewById(R.id.explore_empty_text);

		return v;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		// TODO - When moving away from this screen (by paging to the history
		// tab), the items disappear.
		if (mFavorites == null) {
			setupAdapter();
			getFavorites(false);
		} else {
			if (mAdapter == null) {
				setupAdapter();
			}
			mAdapter.addAll(mFavorites, true, true);
			Utils.setViewVisibility(mLoadingLayout, false);
		}
	}

	/**
	 * Converts the stored favorites into a list of {@link Nameable} objects.
	 */
	private void getFavorites(boolean clearCurrentList) {
		// TODO - move this off UI thread
		List<Favorite> favorites = DatabaseQueries.getListOfFavorites();
		if (favorites == null || favorites.size() == 0) {
			// TODO _ inform failurer
			notifyDataSetChanged();
			return;
		}
		if (mFavorites == null) {
			mFavorites = new ArrayList<Nameable>();
		}
		// Clear the current list if necessary to prevent duplicates
		if (clearCurrentList) {
			mFavorites.clear();
		}
		for (Favorite favorite : favorites) {
			mFavorites.add(favorite);
		}
		mAdapter.addAll(mFavorites, true, true);
		notifyDataSetChanged();
	}

	private void setupAdapter() {
		mAdapter = new VenueAdapter(mActivity, true);
		mListView.setAdapter(mAdapter);
		mListView.setOnItemClickListener(this);

		mAdapter.setOnAddButtonClickListener(this);
		mAdapter.setCheckIsFavoritedListener(this);
	}

	@Override
	public void onStart() {
		super.onStart();
		BusProvider.register(this);
	}

	@Override
	public void onStop() {
		super.onStop();
		BusProvider.unregister(this);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		Nameable venue = (Nameable) mAdapter.getItem(position);
		String venueId = venue.getVenueId();
		Utils.launchDetailActivity(mActivity, venueId, view);
	}

	@Override
	public void onAddButtonClicked(Nameable venue) {
		if (isFavorited(venue)) {
			String id = venue.getVenueId();
			DatabaseQueries.deleteFavoriteByRemoteId(id);
			removeFromFavoritesById(id);
			mAdapter.notifyDataSetChanged();
		} else {
			// Do nothing because it is no longer there, so there will be no way
			// to click an unfavorited item here.
		}
		BusProvider.post(new LocationSavedDataChanged(true));
	}

	@Override
	public boolean isFavorited(Nameable venue) {
		return isInFavorite(venue.getVenueId());
	}

	private boolean isInFavorite(String id) {
		long startTime = System.currentTimeMillis();
		boolean isIn = false;
		for (Nameable fav : mFavorites) {
			if (TextUtils.equals(fav.getVenueId(), id)) {
				isIn = true;
				break;
			}
		}
		long dif = System.currentTimeMillis() - startTime;
		Log.d(TAG, "Time using looping: " + dif);
		return isIn;
	}

	private void removeFromFavoritesById(String id) {
		int size = mFavorites.size();
		for (int i = 0; i < size; i++) {
			Nameable fav = mFavorites.get(i);
			if (TextUtils.equals(fav.getVenueId(), id)) {
				mFavorites.remove(i);
				break;
			}
		}
		// Delete from the adapter
		mAdapter.removeById(id, true);
		notifyDataSetChanged();
		// TODO -notify to the user that the removal was successful.
	}

	private void notifyDataSetChanged() {
		if (mLoadingLayout != null && mLoadingLayout.getVisibility() == View.VISIBLE) {
			Utils.setViewVisibility(mLoadingLayout, false);
		}
		// TODO - Show an empty view if necessary
		if (mFavorites == null || mFavorites.size() == 0) {
			mTextEmpty.setText(R.string.fav_empty);
			Utils.setViewVisibility(mTextEmpty, true);
		} else {
			Utils.setViewVisibility(mTextEmpty, false);
		}
	}

	@Subscribe
	public void onLocationDeletedEvent(LocationSavedDataChanged event) {
		if (event != null) {
			if (event.isFromFavoritesScreen()) {
				return;
			}
			// We only want to accept this event if it was broadcast from
			// another section
			getFavorites(true);
		}
	}
}
