package com.asa.imhere.ui;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.net.Uri;
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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.asa.imhere.AsaBaseAdapter;
import com.asa.imhere.AsaBaseFragment;
import com.asa.imhere.AsaFutureCallback;
import com.asa.imhere.R;
import com.asa.imhere.VenueAdapter.OnAddButtonClickListener;
import com.asa.imhere.foursquare.ExploreGroup;
import com.asa.imhere.foursquare.Foursquare;
import com.asa.imhere.foursquare.FsVenue;
import com.asa.imhere.model.DatabaseQueries;
import com.asa.imhere.model.Favorite;
import com.asa.imhere.model.IHSqlOpenHelper;
import com.asa.imhere.model.Nameable;
import com.asa.imhere.model.responses.ExploreResponse;
import com.asa.imhere.otto.BusProvider;
import com.asa.imhere.otto.FavoriteDeletedEvent;
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

public class ExploreFragment extends AsaBaseFragment implements OnAddButtonClickListener, OnItemClickListener {
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
        mAdapter = new VenueAdapter(mActivity);
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(this);

//        mAdapter.setOnAddButtonClickListener(this);
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

    private class PrepAdapterTask extends AsyncTask<Void, Void, ArrayList<FsVenue>> {

        private ExploreResponse mResponse;

        public PrepAdapterTask(ExploreResponse response) {
            mResponse = response;
        }

        @Override
        protected ArrayList<FsVenue> doInBackground(Void... params) {
            ArrayList<FsVenue> venues = new ArrayList<FsVenue>();
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
        protected void onPostExecute(ArrayList<FsVenue> venues) {
            if (isCancelled() || !isAdded()) {
                return;
            }
            if (venues != null) {
                if (mAdapter == null) {
                    mAdapter = new com.asa.imhere.ui.ExploreFragment.VenueAdapter(mActivity);
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

    class ViewHolder {
        @InjectView(R.id.list_item_venue_text)
        TextView text;
        @InjectView(R.id.list_item_venue_fav_indicator)
        View indicator;
        @InjectView(R.id.list_item_venue_add)
        ImageView add;

        public ViewHolder(View view) {
            ButterKnife.inject(this, view);
        }
    }

    private class VenueAdapter extends AsaBaseAdapter<FsVenue> {

        public VenueAdapter(Context context) {
            super(context);
            items = new ArrayList<FsVenue>();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.list_item_venue, parent, false);
                holder = new ViewHolder(convertView);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            final FsVenue venue = items.get(position);
            holder.text.setText(venue.getName());

            new CheckIfFavoriteTask(holder, mActivity).execute(venue.getVenueId());
            holder.add.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new DatabaseTask(mActivity).execute(venue);
                }
            });

            return convertView;
        }
    }

    private class CheckIfFavoriteTask extends AsyncTask<String, Void, Boolean> {

        private ViewHolder mHolder;
        private Context mContext;

        private CheckIfFavoriteTask(ViewHolder mHolder, Context context) {
            this.mHolder = mHolder;
            mContext = context.getApplicationContext();
        }

        @Override
        protected Boolean doInBackground(String... params) {
            if (params == null || params.length == 0 || isCancelled()) {
                return false;
            }
            if (isCancelled() || !isAdded()) {
                return false;
            }
            String venueId = params[0];
            return DatabaseQueries.isInDatabase(mContext, venueId);
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            if (isCancelled() || !isAdded()) {
                return;
            } else {
                if (result == null) {
                    result = false;
                }
                Utils.setViewVisibility(mHolder.indicator, result);
                mHolder.add.setImageResource(result ? R.drawable.ic_action_remove : R.drawable.ic_action_add);
            }
        }
    }

    @Subscribe
    public void onFavoriteDeletedEvent(FavoriteDeletedEvent event){
        if(event != null && event.isDeleted()){
            if(mAdapter != null){
                mAdapter.notifyDataSetChanged();
            }
        }
    }

    private class DatabaseTask extends AsyncTask<FsVenue, Void, Void> {
        private Context mContext;

        private DatabaseTask(Context context) {
            this.mContext = context.getApplicationContext();
        }

        @Override
        protected Void doInBackground(FsVenue... params) {
            if (params == null || params.length == 0 || isCancelled()) {
                return null;
            }
            SQLiteDatabase db = new IHSqlOpenHelper(mContext).getWritableDatabase();
            FsVenue venue = params[0];
            Favorite fav = DatabaseQueries.getFavoriteByVenueId(mContext, venue.getVenueId());
            if (fav == null || fav.getId() == null) {
                // Save
                fav = Favorite.constructFromVenue(venue);
                Uri uri = DatabaseQueries.saveFavorite(mContext, fav);
                Log.d(TAG, "Uri null: " + (uri == null));
            } else {
                DatabaseQueries.deleteFavorite(mContext, fav);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if(!isAdded() || isCancelled()){
                return;
            }
            if(mAdapter == null){
                mAdapter = new VenueAdapter(mActivity);
            }
            mAdapter.notifyDataSetChanged();
        }
    }

}
