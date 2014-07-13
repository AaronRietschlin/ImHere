package com.asa.imhere.ui;

import android.app.ActionBar;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentSender.SendIntentException;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.asa.imhere.AppData;
import com.asa.imhere.AsaBaseFragment;
import com.asa.imhere.service.GeofenceIntentService;
import com.asa.imhere.R;
import com.asa.imhere.jobs.FetchVenueDetailJob;
import com.asa.imhere.lib.foursquare.FsPhoto;
import com.asa.imhere.lib.foursquare.FsVenue;
import com.asa.imhere.model.DatabaseQueries;
import com.asa.imhere.model.Favorite;
import com.asa.imhere.lib.otto.BusProvider;
import com.asa.imhere.otto.VenueRetreivedEvent;
import com.asa.imhere.utils.LocationUtils;
import com.asa.imhere.utils.Utils;
import com.crashlytics.android.Crashlytics;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks;
import com.google.android.gms.common.GooglePlayServicesClient.OnConnectionFailedListener;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationClient.OnAddGeofencesResultListener;
import com.google.android.gms.location.LocationClient.OnRemoveGeofencesResultListener;
import com.google.android.gms.location.LocationStatusCodes;
import com.koushikdutta.ion.Ion;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class DetailFragment extends AsaBaseFragment implements OnConnectionFailedListener, ConnectionCallbacks, OnAddGeofencesResultListener,
        OnRemoveGeofencesResultListener {
    public final static String TAG = "DetailFragment";

    @InjectView(R.id.detail_venue_img)
    ImageView mImgVenue;
    @InjectView(R.id.detail_switch_fav)
    CompoundButton mSwitchFav;
    @InjectView(R.id.detail_switch_auto_checkin)
    CompoundButton mSwitchAutoCheckin;
    @InjectView(R.id.detail_text_descrip)
    TextView mTvDescrip;
    @InjectView(R.id.detail_layout_auto_checkin)
    View mLayoutAutoCheckin;
    @InjectView(R.id.detail_layout_spinners)
    View mLayoutSpinners;
    @InjectView(R.id.detail_spinner_auto_radius)
    Spinner mSpinnerRadius;
    // private Spinner mSpinnerDuration;

    private String mVenueId;
    private String mVenueName;
    private FsVenue mVenue;
    private Favorite mFavorite;

    private ActionBar mActionBar;

    // Location stuff
    private LocationClient mLocationClient;
    private PendingIntent mGeofenceRequestIntent;

    /**
     * Defines allowable request types
     */
    private enum RequestType {
        ADD, REMOVE_INTENT, REMOVE_IDS
    }

    private RequestType mRequestType;
    private boolean mRequestInProgress;
    private List<Geofence> mCurrentGeoFences;
    private List<String> mGeofencesToRemove;
    private PendingIntent mPendingIntentToRemove;

    private int[] mRadiusValues;

    /**
     * A flag for whether or not to perform the save. We don't want to when the
     * flag is being set upon initialization. Only when the user clicks on it.
     */
    private boolean mOnAutoCheckinCheckedChangePerformSave;
    /**
     * The spinner defaults itself to the first selection. So, in order to
     * prevent an unwanted save, use this flag and set it after the first
     * "selection".
     */
    private boolean mAllowRadiusSpinnerToSave;

    public static DetailFragment newInstance(String venueId) {
        DetailFragment frag = new DetailFragment();
        Bundle args = new Bundle();
        args.putString(AppData.Extras.VENUE_ID, venueId);
        frag.setArguments(args);
        return frag;
    }

    public static DetailFragment newInstance(String venueId, String venueName) {
        DetailFragment frag = new DetailFragment();
        Bundle args = new Bundle();
        args.putString(AppData.Extras.VENUE_ID, venueId);
        args.putString(AppData.Extras.VENUE_NAME, venueName);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Get the venue Id to perform the correct search.
        Bundle args = getArguments();
        if (args == null) {
            throw new IllegalStateException("You must pass a venueId into DetailFragment!");
        }
        mVenueId = args.getString(AppData.Extras.VENUE_ID);
        mVenueName = args.getString(AppData.Extras.VENUE_NAME);

        mFavorite = DatabaseQueries.getFavoriteByVenueId(mActivity, mVenueId);

        // Default the reqeuest in progress to false
        mRequestInProgress = false;
        mCurrentGeoFences = new ArrayList<Geofence>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_detail, container, false);
        ButterKnife.inject(this, v);

        // mSpinnerDuration = (Spinner)
        // v.findViewById(R.id.detail_spinner_auto_duration);

        mSwitchAutoCheckin.setOnCheckedChangeListener(mOnAutoCheckinListener);
        mSpinnerRadius.setOnItemSelectedListener(mOnSpinnerRadiusItemSelectedListener);
        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initActionBar();
        if (savedInstanceState == null) {
            addJob();
        }
        mRadiusValues = getResources().getIntArray(R.array.radius_values);
    }

    @Override
    public void onStart() {
        super.onStart();
        BusProvider.register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mLocationClient != null && mLocationClient.isConnected()) {
            mLocationClient.disconnect();
        }
        BusProvider.unregister(this);
    }

    private void addJob() {
        mJobManager.addJobInBackground(new FetchVenueDetailJob(mVenueId));
    }


    private void setupUi() {
        if (mVenue == null) {
            // TODO - Make sure there is no infinite looping here.
            addJob();
            return;
        }
        setupActionBar();
        setImage();

        // Set the Venue Description, if present
        String descrip = mVenue.getDescription();
        if (!TextUtils.isEmpty(descrip)) {
            mTvDescrip.setText(descrip);
        }

        // Determine if favorite
        boolean isFav = DatabaseQueries.isInDatabase(mActivity, mVenueId);
        mSwitchFav.setOnCheckedChangeListener(null);
        mSwitchFav.setChecked(isFav);
        mSwitchFav.setOnCheckedChangeListener(mOnFavoritedListener);
        // If it's a fav, show the auto checkin layout
        Utils.setViewVisibility(mLayoutAutoCheckin, isFav);
        if (mFavorite != null) {
            // If it's a favorite, then check the auto check in state
            mOnAutoCheckinCheckedChangePerformSave = false;
            mSwitchAutoCheckin.setChecked(mFavorite.isAutoCheckInOn());
            mOnAutoCheckinCheckedChangePerformSave = true;
            mSpinnerRadius.setSelection(getSpinnerPositionRadiusByValue(mFavorite.getRadius()));
        }
    }

    /**
     * Gets selected spinner position based on the value the user had previously
     * selected as their radius.
     */
    private int getSpinnerPositionRadiusByValue(double value) {
        if (mRadiusValues == null || value == 0) {
            return 0;
        }
        int count = mRadiusValues.length;
        for (int i = 0; i < count; i++) {
            int v = mRadiusValues[i];
            if (v == value) {
                return i;
            }
        }
        return 0;
    }

    /**
     * Sets the image
     */
    private void setImage() {
        FsVenue.VenuePhotoGroup photoGroup = mVenue.getPhotos();
        if (photoGroup != null) {
            List<FsVenue.VenuePhotoItem> photoItemList = photoGroup.getItems();
            if (photoItemList != null && photoItemList.size() > 0) {
                FsVenue.VenuePhotoItem photoItem = photoItemList.get(0);
                if (photoItem != null) {
                    List<FsPhoto> photoList = photoItem.getItems();
                    if (photoList != null && photoList.size() > 0) {
                        FsPhoto photo = photoList.get(0);
                        String imageUrl = photo.getFullUrl(photo.getHeight(), photo.getWidth());
                        Ion.with(mImgVenue).load(imageUrl);
                    } else {
                        setNoPhoto("Venue photo list was null or empty.");
                    }
                } else {
                    setNoPhoto("Venue photo list was null or empty.");
                }
            } else {
                setNoPhoto("Venue photo item list was null or empty.");
            }
        } else {
            setNoPhoto("Venue Photo Group was null.");
        }
    }

    private void setNoPhoto(String message) {
        Utils.setViewVisibility(mImgVenue, false);
        if (message != null) {
            Crashlytics.log(Log.DEBUG, TAG, message);
        }
        mActivity.setProgressBarIndeterminateVisibility(false);
    }

    private void initActionBar() {
        mActionBar = mActivity.getActionBar();
        if (!TextUtils.isEmpty(mVenueName)) {
            mActionBar.setTitle(mVenueName);
        }
    }

    /**
     * Sets up the ActionBar with the venue name as the title, and the address
     * as the subtitle.
     */
    private void setupActionBar() {
        if (mActionBar == null) {
            return;
        }
        // Set the title
        mActionBar.setDisplayShowTitleEnabled(true);
        mActionBar.setTitle(mVenue.getName());
        // Set the subtitle
        String addr = mVenue.getAddress();
        mActionBar.setSubtitle(addr);
    }

    /**
     * Start a request for geofence monitoring by calling
     * LocationClient.connect().
     */
    private void makeGeofenceRequest(RequestType requestType) {
        // Start a request to add geofences
        mRequestType = requestType;
        if (!LocationUtils.servicesConnected(mActivity, mActivity.mFragmentManager)) {
            Crashlytics.log(Log.WARN, TAG, "Not connected to the Location Services.");
            return;
        }
        mLocationClient = new LocationClient(mActivity, this, this);
        if (!mRequestInProgress) {
            mRequestInProgress = true;
            mLocationClient.connect();
        } else {
            Crashlytics.log(Log.DEBUG, TAG, "A request is already pending.");
        }
    }

    private void prepareToRemoveGeofence(RequestType requestType) {
        if (!LocationUtils.servicesConnected(mActivity, mActivity.mFragmentManager)) {
            return;
        }
        if (requestType == RequestType.REMOVE_IDS) {
            // mGeofencesToRemove = ;
            // TODO - build list of String ids to remove
        } else if (requestType == RequestType.REMOVE_INTENT) {
            // TODO - get pending intent to remove
        }
        makeGeofenceRequest(requestType);
    }

    private PendingIntent getPendingIntent() {
        Intent intent = new Intent(mActivity, GeofenceIntentService.class);
        intent.putExtra(AppData.Extras.VENUE_ID, mVenueId);
        return PendingIntent.getService(mActivity, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private void addGeofence() {
        PendingIntent pendingIntent = getPendingIntent();
        if (pendingIntent != null) {
            mCurrentGeoFences.add(mFavorite.constructGeofence());
            mLocationClient.addGeofences(mCurrentGeoFences, pendingIntent, this);
        }
    }

    @Subscribe
    public void onVenueRetreived(VenueRetreivedEvent event) {
        if (event != null && event.getVenue() != null) {
            mVenue = event.getVenue();
            setupUi();
            toggleLoading(false, false);
        }
    }

    private void toggleLoading(boolean showAb, boolean showContent) {
        Utils.setViewVisibility(mLoadingLayout, showContent);
        mActivity.setProgressBarIndeterminateVisibility(showAb);
    }

    @Override
    public void onConnected(Bundle data) {
        Crashlytics.log(Log.DEBUG, TAG, "Connected to the location services.");
        switch (mRequestType) {
            case ADD:
                addGeofence();
                break;
            case REMOVE_IDS:
                mLocationClient.removeGeofences(mGeofencesToRemove, this);
                break;
            case REMOVE_INTENT:
                mLocationClient.removeGeofences(mPendingIntentToRemove, this);
                break;
        }
    }

    @Override
    public void onDisconnected() {
        Crashlytics.log(Log.DEBUG, TAG, "Diconnected from the Location services.");
        mRequestInProgress = false;
        mLocationClient = null;
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        mRequestInProgress = false;
        if (connectionResult.hasResolution()) {
            try {
                connectionResult.startResolutionForResult(mActivity, LocationUtils.CONNECTION_FAILURE_RESOLUTION_REQUEST);
            } catch (SendIntentException e) {
                Crashlytics.logException(e);
            }
        } else {
            LocationUtils.showErrorDialog(connectionResult.getErrorCode(), mActivity, mActivity.mFragmentManager);
        }
    }

    @Override
    public void onAddGeofencesResult(int statusCode, String[] geofenceRequestIds) {
        if (statusCode == LocationStatusCodes.SUCCESS) {
            // Log the success.
            String message = "";
            if (geofenceRequestIds != null) {
                for (String id : geofenceRequestIds) {
                    message += id + "\n";
                }
            }
            Crashlytics.log(Log.DEBUG, TAG, "Succesfully added geofence. They are now: " + message);
            // Inform the user
            Toast.makeText(mActivity, "Successfully added Geofence!", Toast.LENGTH_SHORT).show();
            return;
        } else if (statusCode == LocationStatusCodes.GEOFENCE_NOT_AVAILABLE) {
            String message = "The given geofence was not available. VenueID: " + mVenueId;
            Crashlytics.log(Log.ERROR, TAG, message);
            Utils.throwException(message);
        } else if (statusCode == LocationStatusCodes.GEOFENCE_TOO_MANY_GEOFENCES) {
            String message = "The user has too many geofences.";
            Crashlytics.log(Log.WARN, TAG, message);
            Utils.throwException(message);
            Toast.makeText(mActivity, "You have too many geofences set up. The maximum per user is 100. Consider removing some.", Toast.LENGTH_SHORT).show();
            return;
        } else if (statusCode == LocationStatusCodes.GEOFENCE_TOO_MANY_PENDING_INTENTS) {
            String message = "Too many pending intent.";
            Crashlytics.log(Log.WARN, TAG, message);
            Utils.throwException(message);
        } else if (statusCode == LocationStatusCodes.ERROR) {
            String message = "LocationStatusCodes.ERROR occurred. VenueID: " + mVenueId;
            Utils.throwException(message);
        }
        Toast.makeText(mActivity, "Failed to add the given geofence for this location.", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRemoveGeofencesByPendingIntentResult(int statusCode, PendingIntent pendingIntent) {
        if (statusCode == LocationStatusCodes.SUCCESS) {
            // Log the success.
            Crashlytics.log(Log.DEBUG, TAG, "Succesfully removed geofences.");
            Toast.makeText(mActivity, "Geofence was successfully removed.", Toast.LENGTH_SHORT).show();
            return;
        } else {
            String message = "There was an error removing the Geofence through PendingIntent.";
            Crashlytics.log(Log.ERROR, TAG, message);
            Utils.throwException(message);
            Toast.makeText(mActivity, message, Toast.LENGTH_SHORT).show();
        }
        mRequestInProgress = false;
        mLocationClient.disconnect();
    }

    @Override
    public void onRemoveGeofencesByRequestIdsResult(int statusCode, String[] geofenceIds) {
        if (statusCode == LocationStatusCodes.SUCCESS) {
            // Log the success.
            Crashlytics.log(Log.DEBUG, TAG, "Succesfully removed geofences.");
            Toast.makeText(mActivity, "Geofence was successfully removed.", Toast.LENGTH_SHORT).show();
            return;
        } else {
            String message = "There was an error removing the Geofence through PendingIntent.";
            Crashlytics.log(Log.ERROR, TAG, message);
            Utils.throwException(message);
            Toast.makeText(mActivity, message, Toast.LENGTH_SHORT).show();
        }
        mRequestInProgress = false;
        mLocationClient.disconnect();
    }

    // Check change listeners for the switches
    private OnCheckedChangeListener mOnFavoritedListener = new OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (isChecked) {
                if (mFavorite == null) {
                    // TODO - save
                    mFavorite = Favorite.constructFromVenue(mVenue);
                } else {
                    // TODO - save
//					mFavorite.save();
                }
            } else {
                if (mFavorite == null) {
                    // TODO - remove
//					DatabaseQueries.deleteFavoriteByRemoteId(mVenueId);
                } else {
                    // TODO - remove
//					mFavorite.delete();
                    mFavorite = null;
                }
                Utils.setViewVisibility(mLayoutSpinners, false);
            }
            // Show/hide the dependent views based off of this.
            Utils.setViewVisibility(mLayoutAutoCheckin, isChecked);
            // Turn off the Autocheckin flag in the db
            if (mFavorite != null) {
                resetFavorite(true);
            }
            setupUi();
        }
    };

    private OnCheckedChangeListener mOnAutoCheckinListener = new OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (mFavorite == null) {
                Crashlytics.log(Log.DEBUG, TAG, "Somehow, the favorite was null when it shouldn't be.");
                return;
            }
            if (isChecked) {
                mFavorite.setAutoCheckInOn(isChecked);
                // Set to the default radius.
                mFavorite.setRadius(mRadiusValues[0]);
                mAllowRadiusSpinnerToSave = false;
                mSpinnerRadius.setSelection(0);
                mAllowRadiusSpinnerToSave = true;
                if (mOnAutoCheckinCheckedChangePerformSave) {
                    makeGeofenceRequest(RequestType.ADD);
                }
            } else {
                resetFavorite(false);
            }
            if (mOnAutoCheckinCheckedChangePerformSave) {
                // TODO - save
//				mFavorite.save();
            }
            setupUi();
            Utils.setViewVisibility(mLayoutSpinners, isChecked);
        }
    };

    private OnItemSelectedListener mOnSpinnerRadiusItemSelectedListener = new OnItemSelectedListener() {

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            // See docs for mAllow.. for why this is here
            if (!mAllowRadiusSpinnerToSave) {
                mAllowRadiusSpinnerToSave = true;
                return;
            }
            // Set the radius and save it.
            float radius = mRadiusValues[position];
            if (mFavorite != null) {
                mFavorite.setRadius(radius);
                // TODO - save
//				mFavorite.save();
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
            Log.d(TAG, "Nothing selected.");
        }

    };

    /**
     * Resets the favorite to its initial values. 0 for radius and duration and
     * false for auto checkin.
     */
    private void resetFavorite(boolean save) {
        if (mFavorite == null) {
            return;
        }
        mFavorite.setAutoCheckInOn(false);
        mFavorite.setDuration(0);
        mFavorite.setRadius(0);
        if (save) {
            // TODO - save
//			mFavorite.save();
        }
    }
}
