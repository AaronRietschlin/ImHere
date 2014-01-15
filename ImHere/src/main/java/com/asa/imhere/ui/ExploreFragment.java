package com.asa.imhere.ui;

import android.location.Location;
import android.os.AsyncTask;
import android.os.AsyncTask.Status;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.asa.imhere.AsaBaseFragment;
import com.asa.imhere.AsaFutureCallback;
import com.asa.imhere.R;
import com.asa.imhere.VenueAdapter;
import com.asa.imhere.VenueAdapter.CheckIsFavoritedListener;
import com.asa.imhere.VenueAdapter.OnAddButtonClickListener;
import com.asa.imhere.foursquare.ExploreGroup;
import com.asa.imhere.foursquare.Foursquare;
import com.asa.imhere.foursquare.FsVenue;
import com.asa.imhere.model.Favorite;
import com.asa.imhere.model.Nameable;
import com.asa.imhere.model.responses.ExploreResponse;
import com.asa.imhere.otto.BusProvider;
import com.asa.imhere.otto.LocationNeededEvent;
import com.asa.imhere.otto.LocationProvidedEvent;
import com.asa.imhere.otto.LocationSavedDataChanged;
import com.asa.imhere.otto.LocationServicesConnectedEvent;
import com.asa.imhere.utils.LocationUtils;
import com.asa.imhere.utils.PreferenceUtils;
import com.asa.imhere.utils.Utils;
import com.crashlytics.android.Crashlytics;
import com.google.gson.JsonObject;
import com.koushikdutta.ion.Ion;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class ExploreFragment extends AsaBaseFragment implements CheckIsFavoritedListener, OnAddButtonClickListener, OnItemClickListener {
    public final static String TAG = "ExploreFragment";

    @InjectView(R.id.explore_list)
    ListView mListView;
    @InjectView(R.id.explore_empty_text)
    TextView mTvEmpty;

    private VenueAdapter mAdapter;
    // TODO - move this to MainActivity so the favorites tab can use it?
    private ArrayList<Favorite> mFavorites;

    private double mMostRecentLat;
    private double mMostRecentLon;

    private boolean mLocationServicesConnected;

    private PrepAdapterTask mTask;

    private final static String TEST_URL = "https://api.foursquare.com/v2/venues/explore?ll=40.7,-74&oauth_token=JHKJOU0BMNRTWRMM0FKLSULNKCCRRHAPXMKGWXL4XTG2PAAA&v=20130522";

    public static ExploreFragment newInstance() {
        ExploreFragment frag = new ExploreFragment();

        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_explore, container, false);
        ButterKnife.inject(this, v);
        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (savedInstanceState == null) {
            contsructMostRecentLatLon();
            setupAdapter();
            getFavorites(false);
        } else {

        }

        // This use to be here. Working on switching it out and waiting on
        // callbacks from the activity that the location service was connected.
        // if (savedInstanceState == null) {
        // makeRequest();
        // } else {
        // if (mAdapter.getCount() > 0) {
        // Utils.setViewVisibility(mLoadingLayout, false);
        // } else {
        // makeRequest();
        // }
        // }
    }

    private void getFavorites(boolean notify) {
        // TODO - retreive
//        mFavorites = DatabaseQueries.getListOfFavorites();
        if (notify) {
            mAdapter.notifyDataSetChanged();
        }
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
        // Cancel the currently running request (if any).
        // VolleyProvider.getRequestQueue(mActivity).cancelAll(TAG);
        Ion.getDefault(mActivity).cancelAll(TAG);
        if (isTaskCancelable()) {
            mTask.cancel(true);
        }
        BusProvider.unregister(this);
    }

    private boolean isTaskCancelable() {
        Status status = mTask.getStatus();
        return mTask != null && (status == Status.RUNNING || status == Status.PENDING);
    }

    private void contsructMostRecentLatLon() {
        double[] latLon = PreferenceUtils.getMostRecentLatLon(mActivity);
        mMostRecentLat = latLon[0];
        mMostRecentLon = latLon[1];
    }

    @Subscribe
    public void onLocationProvidedEvent(LocationProvidedEvent event) {
        if (event != null && event.getLocation() != null) {
            Location loc = event.getLocation();
            mMostRecentLat = loc.getLatitude();
            mMostRecentLon = loc.getLongitude();
            PreferenceUtils.setMostRecentLatLon(mActivity.getApplicationContext(), mMostRecentLat, mMostRecentLon);
            makeRequest();
        }
    }

    @Subscribe
    public void LocationServicesConnectedEvent(LocationServicesConnectedEvent event) {
        if (event != null) {
            BusProvider.post(new LocationNeededEvent());
        }
    }

    @Subscribe
    public void onLocationDeletedEvent(LocationSavedDataChanged event) {
        if (event != null) {
            if (!event.isFromFavoritesScreen()) {
                return;
            }
            // We only want to accept this event if it was broadcast from
            // another section
            getFavorites(true);
        }
    }

    private void showErrorMessage(boolean showError, String message) {
        if (showError) {
            Utils.setViewVisibility(mLoadingLayout, false);
            Utils.setViewVisibility(mListView, false);
            Utils.setViewVisibility(mTvEmpty, true);
            mTvEmpty.setText(message);
        } else {
            Utils.setViewVisibility(mListView, true);
            Utils.setViewVisibility(mTvEmpty, false);
        }
    }

    private void showErrorMessage(boolean showError, int messageResId) {
        if (!isAdded()) {
            return;
        }
        showErrorMessage(showError, getString(messageResId));
    }

    private void makeRequest() {
        if (!LocationUtils.isValidLatLon(mMostRecentLat, mMostRecentLon)) {
            Utils.setViewVisibility(mLoadingLayout, true);
            BusProvider.post(new LocationNeededEvent());
            return;
        }
        String url = Foursquare.constructExploreUrl(null, mMostRecentLat, mMostRecentLon, mActivity);
        Ion.with(mActivity, url).group(TAG).asJsonObject().setCallback(new AsaFutureCallback<JsonObject, ExploreResponse>(url) {

            @Override
            public void onError() {
                Crashlytics.log(Log.ERROR, TAG, "An error occurred getting the Explore data. Url requested: " + mUrl);
                showErrorMessage(true, R.string.explore_error_network);
            }

            @Override
            public void onException(Exception e) {
                Crashlytics.logException(e);
                onError();
            }

            @Override
            public void onSuccess(JsonObject result) {
                ExploreResponse response = serialize(result, ExploreResponse.class);
                if (response != null) {
                    initTask(response);
                }
            }
        });
    }

    private void initTask(ExploreResponse response) {
        // AsyncTasks cannot execute more than once. So, only allow it to
        // execute once
        if (mTask == null) {
            mTask = new PrepAdapterTask(response);
        } else {
            mTask = null;
            mTask = new PrepAdapterTask(response);
        }
        mTask.execute();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Nameable venue = (Nameable) mAdapter.getItem(position);
        String venueId = venue.getVenueId();
        Utils.launchDetailActivity(mActivity, venueId, view);
    }

    private class PrepAdapterTask extends AsyncTask<Void, Void, ArrayList<Nameable>> {

        private ExploreResponse mResponse;

        public PrepAdapterTask(ExploreResponse response) {
            mResponse = response;
        }

        @Override
        protected ArrayList<Nameable> doInBackground(Void... params) {
            ArrayList<Nameable> venues = new ArrayList<Nameable>();
            // The structure of the "explore" api is verbose. Using an
            // ExploreResponse object that houses the "Reponse" object that
            // houses a list of "ExploreGroup"s, each of which houses a list of
            // "Items", each of which houses a venue. That's what we want
            ExploreResponse.Response response = mResponse.getResponse();
            if (response == null) {
                return venues;
            }
            ArrayList<ExploreGroup> groups = response.getGroups();
            if (groups == null) {
                return venues;
            }
            for (ExploreGroup group : groups) {
                ArrayList<ExploreGroup.Item> items = group.getItems();
                if (items == null) {
                    continue;
                }
                for (ExploreGroup.Item item : items) {
                    if (item == null) {
                        continue;
                    }
                    FsVenue venue = item.getVenue();
                    if (venue != null) {
                        venues.add(venue);
                    }
                }
            }
            return venues;
        }

        @Override
        protected void onPostExecute(ArrayList<Nameable> venues) {
            if (isCancelled() || !isAdded()) {
                return;
            }
            if (venues != null) {
                if (mAdapter == null) {
                    mAdapter = new VenueAdapter(mActivity);
                }
                mAdapter.addAll(venues, true, true);
            }
            Utils.setViewVisibility(mLoadingLayout, false);
        }
    }

    @Override
    public void onAddButtonClicked(Nameable venue) {
        // TODO - save
//        if (isFavorited(venue)) {
//            String id = venue.getVenueId();
//            DatabaseQueries.deleteFavoriteByRemoteId(id);
//            removeFromFavoritesById(id);
//            mAdapter.notifyDataSetChanged();
//        } else {
//            Favorite fav = null;
//            if (venue instanceof FsVenue) {
//                fav = Favorite.constructFromVenue((FsVenue) venue);
//            } else {
//                fav = Favorite.constructFromNameable(venue);
//            }
//            if (Utils.isInDatabase(fav)) {
//                Toast.makeText(mActivity, venue.getName() + " was favorited.", Toast.LENGTH_SHORT).show();
//                mFavorites.add(fav);
//                mAdapter.notifyDataSetChanged();
//            } else {
//                // TODO - inform of failure
//            }
//        }
        BusProvider.post(new LocationSavedDataChanged(false));
    }

    @Override
    public boolean isFavorited(Nameable venue) {
        return isInFavorite(venue.getVenueId());
    }

    private boolean isInFavorite(String id) {
        // TODO
        return false;
//        boolean isIn = false;
//        for (Favorite fav : mFavorites) {
//            if (TextUtils.equals(fav.getRemoteId(), id)) {
//                isIn = true;
//                break;
//            }
//        }
//        return isIn;
    }

    private void removeFromFavoritesById(String id) {
        int size = mFavorites.size();
        for (int i = 0; i < size; i++) {
            Favorite fav = mFavorites.get(i);
            if (TextUtils.equals(fav.getRemoteId(), id)) {
                mFavorites.remove(i);
                break;
            }
        }
    }

    private boolean isInFavoriteDb(String id) {
        // TODO - implement
//        long startTime = System.currentTimeMillis();
//        boolean fav = DatabaseQueries.isFavorited(id);
//        long dif = System.currentTimeMillis() - startTime;
//        Log.d(TAG, "Time using DB: " + dif);
//        return fav;
        return false;
    }

}
