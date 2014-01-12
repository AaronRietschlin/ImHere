package com.asa.imhere;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.asa.imhere.model.DatabaseQueries;
import com.asa.imhere.model.Favorite;
import com.crashlytics.android.Crashlytics;
import com.google.analytics.tracking.android.GoogleAnalytics;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.LocationClient;

public class GeofenceIntentService extends IntentService {
	public static final String TAG = "GeofenceIntentService";

	private String mVenueId;
	private Favorite mGeofenceFav;

	public GeofenceIntentService() {
		super(TAG);
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		if (intent == null) {
			// Fail.
			return;
		}
		if (LocationClient.hasError(intent)) {
			// TODO - test this. Force an error somehow.
			Bundle extras = intent.getExtras();
			if (extras != null) {
				throwException(extras.toString());
			}
			Crashlytics.setBool(AppData.CrashlyticKeys.LOCATION_CLIENT_INTENT_HAS_ERROR, true);
			Crashlytics.log(Log.ERROR, TAG, "The location Client has an error.");
			// TODO - send user to handle error
		}

		int transitionType = LocationClient.getGeofenceTransition(intent);
		Crashlytics.log(Log.DEBUG, TAG, "Transition Type: " + transitionType);
		if (transitionType == Geofence.GEOFENCE_TRANSITION_ENTER) {
			Crashlytics.log(Log.DEBUG, TAG, "Transition type was Enter.");
			Crashlytics.setString(AppData.CrashlyticKeys.TRANSITION_TYPE, "enter");
		} else if (transitionType == Geofence.GEOFENCE_TRANSITION_EXIT) {
			Crashlytics.log(Log.DEBUG, TAG, "Transition type was Exit.");
			Crashlytics.setString(AppData.CrashlyticKeys.TRANSITION_TYPE, "exit");
		}

		// Retrieve the venue Id passed into the service
		Bundle extras = intent.getExtras();
		if (extras == null) {
			throwException("There were no extras passed into the intent for the : " + TAG);
			return;
		}

		mVenueId = extras.getString(AppData.Extras.VENUE_ID);
		Crashlytics.setString(AppData.CrashlyticKeys.VENUE_ID, mVenueId);

		// Get the Favorite that represents the GeoFence we want to start
		mGeofenceFav = DatabaseQueries.getFavoriteByVenueId(mVenueId);
		if (mGeofenceFav == null) {
			throwException("The given venueId was not in the geofencing database: " + mVenueId);
			return;
		}

		// Log to GA
		GoogleAnalytics.getInstance(getApplicationContext()).getDefaultTracker().sendEvent(AppData.Analytics.EVENT_CHECKIN, AppData.Analytics.ACTION_CHECKIN, mVenueId, 0L);

		// Notify the user
		NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext());
		builder.setAutoCancel(true).setContentTitle("You are entering a favorited area! Checking in!");
		builder.setContentText("We are checking you in to : " + mGeofenceFav.getName()).setSmallIcon(R.drawable.ic_launcher);
		Notification notif = builder.build();
		NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		manager.notify(1, notif);
	}

	/**
	 * Logs an exception to Crashlytics.
	 */
	private void throwException(String message) {
		Exception e = new Exception(message);
		Crashlytics.logException(e);
	}
}
