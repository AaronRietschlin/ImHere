package com.asa.imhere.jobs;

import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.asa.imhere.AppData;
import com.asa.imhere.GeofenceIntentService;
import com.asa.imhere.lib.foursquare.FsVenue;
import com.asa.imhere.lib.jobs.Priority;
import com.asa.imhere.model.Favorite;
import com.asa.imhere.utils.LocationUtils;
import com.asa.imhere.utils.Utils;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationStatusCodes;
import com.path.android.jobqueue.Params;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

/**
 * A job that will add/remove geofences based off of the given Venue and Favorite.
 * <p/>
 * Created by Aaron on 7/6/2014.
 */
public class GeofenceJob extends BaseJob implements GooglePlayServicesClient.ConnectionCallbacks,
        GooglePlayServicesClient.OnConnectionFailedListener, LocationClient.OnAddGeofencesResultListener,
        LocationClient.OnRemoveGeofencesResultListener {
    private static final String TAG = "AddGeofenceJob";

    private FsVenue mVenue;
    private Favorite mFavorite;

    // NOTE: For now, we are assuming this job would not start unless Play Services is available.

    // Location stuff
    private LocationClient mLocationClient;
    private PendingIntent mGeofenceRequestIntent;

    private RequestType mRequestType;
    private boolean mRequestInProgress;
    private List<Geofence> mCurrentGeoFences;
    private List<String> mGeofencesToRemove;
    private PendingIntent mPendingIntentToRemove;

    /**
     * Defines allowable request types
     */
    public static enum RequestType {
        ADD, REMOVE_INTENT, REMOVE_IDS
    }

    public GeofenceJob(FsVenue venue) {
        super(new Params(Priority.HIGH).groupBy(TAG));
        Timber.tag(TAG);
        mVenue = venue;
        mCurrentGeoFences = new ArrayList<Geofence>();
    }

    public GeofenceJob(FsVenue venue, RequestType requestType) {
        this(venue);
        mRequestType = requestType;
    }

    @Override
    public void onAdded() {

    }

    @Override
    public void onRun() throws Throwable {
        mFavorite = Favorite.constructFromVenue(mVenue);
        makeGeofenceRequest(mRequestType);
    }

    @Override
    protected void onCancel() {

    }

    @Override
    protected boolean shouldReRunOnThrowable(Throwable throwable) {
        return true;
    }

    private PendingIntent getPendingIntent() {
        Intent intent = new Intent(mContext, GeofenceIntentService.class);
        intent.putExtra(AppData.Extras.VENUE_ID, mVenue.getVenueId());
        return PendingIntent.getService(mContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private void addGeofence() {
        PendingIntent pendingIntent = getPendingIntent();
        if (pendingIntent != null) {
            // TODO - construct a geofence from a Venue...
            mCurrentGeoFences.add(mFavorite.constructGeofence());
            mLocationClient.addGeofences(mCurrentGeoFences, pendingIntent, this);
        }
    }

    /**
     * Start a request for geofence monitoring by calling
     * LocationClient.connect().
     */
    private void makeGeofenceRequest(RequestType requestType) {
        // Start a request to add geofences
        mRequestType = requestType;
        if (!LocationUtils.servicesConnected(mContext)) {
            Timber.d("Not connected to the Location Services.");
            return;
        }
        mLocationClient = new LocationClient(mContext, this, this);
        if (!mRequestInProgress) {
            mRequestInProgress = true;
            mLocationClient.connect();
        } else {
            Timber.d("A request is already pending.");
        }
    }

    private void prepareToRemoveGeofence(RequestType requestType) {
        if (requestType == RequestType.REMOVE_IDS) {
            // mGeofencesToRemove = ;
            // TODO - build list of String ids to remove
        } else if (requestType == RequestType.REMOVE_INTENT) {
            // TODO - get pending intent to remove
        }
        makeGeofenceRequest(requestType);
    }


    @Override
    public void onConnected(Bundle data) {
        Timber.d("Connected to the location services.");
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
        Timber.d("Diconnected from the Location services.");
        mRequestInProgress = false;
        mLocationClient = null;
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        mRequestInProgress = false;
        if (connectionResult.hasResolution()) {
            // TODO _ post to bus and let UI handle this.
//            try {
//                connectionResult.startResolutionForResult(mContext, LocationUtils.CONNECTION_FAILURE_RESOLUTION_REQUEST);
//            } catch (IntentSender.SendIntentException e) {
//                Timber.e(e, "");
//            }
        } else {
//            LocationUtils.showErrorDialog(connectionResult.getErrorCode(), mContext, mContext.mFragmentManager);
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
            Timber.d("Succesfully added geofence. They are now: " + message);
            // Inform the user
            // TODO - use Bus to inform user.
            Toast.makeText(mContext, "Successfully added Geofence!", Toast.LENGTH_SHORT).show();
            return;
        } else if (statusCode == LocationStatusCodes.GEOFENCE_NOT_AVAILABLE) {
            String message = "The given geofence was not available. VenueID: " + mVenue.getVenueId();
            Timber.e(message);
            Utils.throwException(message);
        } else if (statusCode == LocationStatusCodes.GEOFENCE_TOO_MANY_GEOFENCES) {
            String message = "The user has too many geofences.";
            Timber.e(message);
            Utils.throwException(message);
            Toast.makeText(mContext, "You have too many geofences set up. The maximum per user is 100. Consider removing some.", Toast.LENGTH_SHORT).show();
            return;
        } else if (statusCode == LocationStatusCodes.GEOFENCE_TOO_MANY_PENDING_INTENTS) {
            String message = "Too many pending intent.";
            Timber.e(message);
            Utils.throwException(message);
        } else if (statusCode == LocationStatusCodes.ERROR) {
            String message = "LocationStatusCodes.ERROR occurred. VenueID: " + mVenue.getVenueId();
            Utils.throwException(message);
        }
        Toast.makeText(mContext, "Failed to add the given geofence for this location.", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRemoveGeofencesByPendingIntentResult(int statusCode, PendingIntent pendingIntent) {
        if (statusCode == LocationStatusCodes.SUCCESS) {
            // Log the success.
            Timber.d("Succesfully removed geofences.");
            Toast.makeText(mContext, "Geofence was successfully removed.", Toast.LENGTH_SHORT).show();
            return;
        } else {
            String message = "There was an error removing the Geofence through PendingIntent.";
            Timber.e(message);
            Utils.throwException(message);
            Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
        }
        mRequestInProgress = false;
        mLocationClient.disconnect();
    }

    @Override
    public void onRemoveGeofencesByRequestIdsResult(int statusCode, String[] geofenceIds) {
        if (statusCode == LocationStatusCodes.SUCCESS) {
            // Log the success.
            Timber.d("Succesfully removed geofences.");
            Toast.makeText(mContext, "Geofence was successfully removed.", Toast.LENGTH_SHORT).show();
            return;
        } else {
            String message = "There was an error removing the Geofence through PendingIntent.";
            Timber.e(message);
            Utils.throwException(message);
            Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
        }
        mRequestInProgress = false;
        mLocationClient.disconnect();
    }

}
