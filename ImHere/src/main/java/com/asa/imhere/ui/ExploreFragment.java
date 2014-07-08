package com.asa.imhere.ui;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
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
import com.asa.imhere.R;
import com.asa.imhere.VenueAdapter.OnAddButtonClickListener;
import com.asa.imhere.jobs.FetchVenuesExploreJob;
import com.asa.imhere.jobs.GeofenceJob;
import com.asa.imhere.lib.foursquare.FsUtils;
import com.asa.imhere.lib.foursquare.FsVenue;
import com.asa.imhere.lib.model.Nameable;
import com.asa.imhere.model.DatabaseQueries;
import com.asa.imhere.model.Favorite;
import com.asa.imhere.model.IHSqlOpenHelper;
import com.asa.imhere.lib.otto.BusProvider;
import com.asa.imhere.otto.ExploreVenuesRetreived;
import com.asa.imhere.otto.FavoriteDeletedEvent;
import com.asa.imhere.otto.LocationNeededEvent;
import com.asa.imhere.otto.LocationProvidedEvent;
import com.asa.imhere.otto.LocationSavedDataChanged;
import com.asa.imhere.otto.LocationServicesConnectedEvent;
import com.asa.imhere.utils.PreferenceUtils;
import com.asa.imhere.utils.Utils;
import com.koushikdutta.ion.Ion;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;
import timber.log.Timber;
import uk.co.senab.actionbarpulltorefresh.library.ActionBarPullToRefresh;
import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshLayout;
import uk.co.senab.actionbarpulltorefresh.library.listeners.OnRefreshListener;

public class ExploreFragment extends AsaBaseFragment implements OnAddButtonClickListener, OnItemClickListener, OnRefreshListener {
    public final static String TAG = "ExploreFragment";

    @InjectView(R.id.explore_list)
    ListView mListView;
    @InjectView(R.id.explore_empty_text)
    TextView mTvEmpty;
    @InjectView(R.id.ptr_layout)
    PullToRefreshLayout mPullToRefreshLayout;

    private VenueAdapter mAdapter;
    // TODO - move this to MainActivity so the favorites tab can use it?
    private ArrayList<Favorite> mFavorites;

    private double mMostRecentLat;
    private double mMostRecentLon;

    private boolean mLocationServicesConnected;

    private final static String TEST_URL = "https://api.foursquare.com/v2/venues/explore?ll=40.7,-74&oauth_token=JHKJOU0BMNRTWRMM0FKLSULNKCCRRHAPXMKGWXL4XTG2PAAA&v=20130522";

    public static ExploreFragment newInstance() {
        ExploreFragment frag = new ExploreFragment();

        return frag;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Timber.tag(TAG);
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
        // Now setup the PullToRefreshLayout
        ActionBarPullToRefresh.from(mActivity)
                // Mark All Children as pullable
                .allChildrenArePullable()
                        // Set the OnRefreshListener
                .listener(this)
                        // Finally commit the setup to our PullToRefreshLayout
                .setup(mPullToRefreshLayout);
        if (savedInstanceState == null) {
            contsructMostRecentLatLon();
            setupAdapter();
            getFavorites(false);
        } else {
            // TODO - Restore from previous state.
        }
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
    }

    @Override
    public void onStart() {
        super.onStart();
        BusProvider.register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        // TODO - Cancel jobs somehow.
        // Cancel the currently running request (if any).
        Ion.getDefault(mActivity).cancelAll(TAG);
        BusProvider.unregister(this);
    }

    private void addJob() {
        mJobManager.addJobInBackground(new FetchVenuesExploreJob(mMostRecentLat, mMostRecentLon));
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
            addJob();
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

    @Subscribe
    public void onVenuesRetrieved(ExploreVenuesRetreived event) {
        if (event != null && event.getVenues() != null) {
            if (mAdapter == null) {
                mAdapter = new VenueAdapter(mActivity);
            }
            mAdapter.addAll(event.getVenues(), !event.isPaginated(), true);
            mPullToRefreshLayout.setRefreshComplete();
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

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Nameable venue = (Nameable) mAdapter.getItem(position);
        String venueId = venue.getVenueId();
        String url = FsUtils.buildViewVenueUrl(venueId);
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(intent);
        // TODO - As of July 6th, I am simplifying the experience by taking the user to the "View a venue" url
        // Before, I was using the below code to launch the details fragment. For now, I am removing this to keep it simple.
//        Utils.launchDetailActivity(mActivity, venueId, venue.getName(), view);
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
//        Log.d(TAG_PREFIX, "Time using DB: " + dif);
//        return fav;
        return false;
    }

    @Override
    public void onRefreshStarted(View view) {
        addJob();
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

        @Override
        public void notifyDataSetChanged() {
            super.notifyDataSetChanged();
            invalidateLoading();
        }
    }

    private void invalidateLoading() {
        int count = mAdapter != null ? mAdapter.getCount() : 0;
        Utils.setViewVisibility(mLoadingLayout, count == 0);
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
    public void onFavoriteDeletedEvent(FavoriteDeletedEvent event) {
        if (event != null && event.isDeleted()) {
            if (mAdapter != null) {
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
            GeofenceJob.RequestType requestType;
            if (fav == null || fav.getId() == null) {
                // Save
                fav = Favorite.constructFromVenue(venue);
                Uri uri = DatabaseQueries.saveFavorite(mContext, fav);
                Timber.d("Uri null: " + (uri == null));
                requestType = GeofenceJob.RequestType.ADD;
            } else {
                DatabaseQueries.deleteFavorite(mContext, fav);
                requestType = GeofenceJob.RequestType.REMOVE_IDS;
            }
            mJobManager.addJobInBackground(new GeofenceJob(venue, requestType));
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (!isAdded() || isCancelled()) {
                return;
            }
            if (mAdapter == null) {
                mAdapter = new VenueAdapter(mActivity);
            }
            mAdapter.notifyDataSetChanged();
        }
    }

}
