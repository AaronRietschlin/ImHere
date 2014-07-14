package com.asa.imhere.ui;

import android.app.ActionBar;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.asa.imhere.IHApplication;
import com.asa.imhere.R;
import com.asa.imhere.jobs.FetchVenueDetailJob;
import com.asa.imhere.lib.foursquare.FsUtils;
import com.asa.imhere.lib.foursquare.FsVenue;
import com.asa.imhere.notifications.CheckinNotification;
import com.asa.imhere.service.CheckinService;
import com.asa.imhere.lib.otto.BusProvider;
import com.asa.imhere.otto.LocationNeededEvent;
import com.asa.imhere.otto.LocationProvidedEvent;
import com.asa.imhere.otto.LocationServiceCheckNeededEvent;
import com.asa.imhere.otto.LocationServicesConnectedEvent;
import com.asa.imhere.otto.VenueEnteredEvent;
import com.asa.imhere.service.DebugGeofenceService;
import com.asa.imhere.utils.DebugVenueProvider;
import com.asa.imhere.utils.LocationUtils;
import com.asa.imhere.utils.Utils;
import com.astuetz.PagerSlidingTabStrip;
import com.crashlytics.android.Crashlytics;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;
import com.squareup.otto.Produce;
import com.squareup.otto.Subscribe;
import android.graphics.BitmapFactory;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AsaBaseActivity implements GooglePlayServicesClient.ConnectionCallbacks, GooglePlayServicesClient.OnConnectionFailedListener,
        ViewPager.OnPageChangeListener {
    public static final String TAG = "MainActivity";

    private LocationClient mLocationClient;

    private Location mCurrentLocation;

    private ActionBar mActionBar;
    private ViewPager mPager;
    private PagerSlidingTabStrip mTabStrip;
    private AsaPagerAdapter mPagerAdapter;

    // TODO - Add the ability to click the hardware search button if available
    // (KEYCODE_SEARCH).

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Crashlytics.start(this);
        // Only proceed with this activity if the user has connected with
        // Foursquare.
        if (!Utils.isConnectedToFoursquare(getApplicationContext())) {
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            finish();
            return;
        }
        setContentView(R.layout.activity_main);
        mActionBar = getActionBar();

        mPager = (ViewPager) findViewById(R.id.pager);
        mTabStrip = (PagerSlidingTabStrip) findViewById(R.id.tabs);

        // setup the pager stuff
        mPagerAdapter = new AsaPagerAdapter(mFragmentManager, getApplicationContext());
        mPager.setAdapter(mPagerAdapter);
        // Bind the tabstrip to the viewpager
        mTabStrip.setViewPager(mPager);
        mTabStrip.setOnPageChangeListener(this);

        mLocationClient = new LocationClient(this, this, this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            // TODO - Impl this!
            case LocationUtils.CONNECTION_FAILURE_RESOLUTION_REQUEST:
            /*
             * If the result code is Activity.RESULT_OK, try to connect again
			 */
                switch (resultCode) {
                    case Activity.RESULT_OK:
                /*
				 * Try the request again
				 */
                        break;
                }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Connect the client.
        Log.d(TAG, "Connecting to location services.");
        mLocationClient.connect();
    }

    @Override
    protected void onResume() {
        super.onResume();
        BusProvider.register(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        BusProvider.unregister(this);
    }

    @Override
    protected void onStop() {
        // Disconnecting the client invalidates it.
        mLocationClient.disconnect();
        super.onStop();
    }

    @Subscribe
    public void onLocationNeeded(LocationNeededEvent event) {
        if (event != null && LocationUtils.servicesConnected(this, mFragmentManager)) {
            BusProvider.post(provideLocationProvidedEvent());
        }
    }

    @Subscribe
    public void onLocationServiceCheckNeeded(LocationServiceCheckNeededEvent event) {
        // Called only if location services are needed but have not been
        // obtained by the caller. This would happen in the event that the
        // caller finished building after the location services conencted.
        if (event != null) {
            // Call the event if it's connected
            if (mLocationClient.isConnected()) {
                BusProvider.post(new LocationServicesConnectedEvent());
            }
        }
    }

    @Produce
    public LocationProvidedEvent provideLocationProvidedEvent() {
        if (LocationUtils.servicesConnected(this, mFragmentManager) && mLocationClient.isConnected()) {
            return new LocationProvidedEvent(mLocationClient.getLastLocation());
        } else {
            return null;
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        if (connectionResult.hasResolution()) {
            try {
                // Start an Activity that tries to resolve the error
                connectionResult.startResolutionForResult(this, LocationUtils.CONNECTION_FAILURE_RESOLUTION_REQUEST);
                // Thrown if Google Play services canceled the original
                // PendingIntent
            } catch (IntentSender.SendIntentException e) {
                // Log the error
                e.printStackTrace();
            }
        } else {
            // If no resolution is available, display a dialog to the user with
            // the error.
            // TODO - test this!
            LocationUtils.showErrorDialog(connectionResult.getErrorCode(), this, mFragmentManager);
        }
    }

    @Override
    public void onConnected(Bundle dataBundle) {
        Log.d(TAG, "Connected to Location Services");
        BusProvider.post(new LocationServicesConnectedEvent());
    }

    @Override
    public void onDisconnected() {
        Log.d(TAG, "DisConnected to Location Services");
    }

    @Override
    public void onPageScrollStateChanged(int pos) {

    }

    @Override
    public void onPageScrolled(int arg0, float arg1, int arg2) {

    }

    @Override
    public void onPageSelected(int arg0) {

    }

    /**
     * The Adapter that populates the ViewPager.
     */
    public static class AsaPagerAdapter extends FragmentPagerAdapter {
        private List<TabInfo> mTabs;
        private Context mContext;

        public AsaPagerAdapter(FragmentManager fm, Context context) {
            super(fm);
            mContext = context;
            setupTabs();
        }

        @Override
        public Fragment getItem(int pos) {
            if (mTabs == null) {
                throw new IllegalStateException("You must set the tabs for this PagerAdapter.");
            }
            TabInfo tab = mTabs.get(pos);
            return tab.getFragment();
        }

        @Override
        public int getCount() {
            return mTabs == null ? 0 : mTabs.size();
        }

        @Override
        public CharSequence getPageTitle(int pos) {
            if (mTabs == null) {
                return "";
            }
            TabInfo tab = mTabs.get(pos);
            return tab.getTitle();
        }

        public List<TabInfo> getTabs() {
            return mTabs;
        }

        public void setTabs(List<TabInfo> tabs) {
            this.mTabs = tabs;
        }

        private void setupTabs() {
            if (mTabs == null) {
                mTabs = new ArrayList<TabInfo>();
            }
            TabInfo tab = new TabInfo(mContext, new FavoriteFragment(), R.string.fav_title);
            mTabs.add(tab);
            tab = new TabInfo(mContext, new ExploreFragment(), R.string.explore_title);
            mTabs.add(tab);
            tab = new TabInfo(mContext, new HistoryFragment(), R.string.history_title);
            mTabs.add(tab);
        }

        public static class TabInfo {
            private Fragment fragment;
            private String title;

            public TabInfo(Context context, Fragment fragment, int titleId) {
                this.fragment = fragment;
                title = context.getString(titleId);
            }

            public Fragment getFragment() {
                return fragment;
            }

            public void setFragment(Fragment fragment) {
                this.fragment = fragment;
            }

            public String getTitle() {
                return title;
            }

            public void setTitle(String title) {
                this.title = title;
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.menu_search:
                startActivity(new Intent(this, SearchActivity.class));
                break;
            case R.id.action_settings:
                DebugVenueProvider.sendDebugVenueEnteredEvent();
                break;
            case R.id.action_debug_custom:
                DebugGeofenceService.startService(this, DebugVenueProvider.getVenuefromAssets(getApplicationContext()));
        }
        return super.onOptionsItemSelected(item);
    }

    @Subscribe
    public void onVenueEntered(VenueEnteredEvent event) {
        if (event != null && event.isDebug() && IHApplication.DEBUG) {
            FsVenue venue = event.getVenue();
            CheckinNotification.notify(getApplicationContext(), venue);
        }
    }
}
