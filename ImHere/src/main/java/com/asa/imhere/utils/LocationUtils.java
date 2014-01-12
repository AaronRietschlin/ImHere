package com.asa.imhere.utils;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.location.Location;
import android.support.v4.app.FragmentManager;
import android.util.Log;

import com.asa.imhere.widget.ErrorDialogFragment;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

public class LocationUtils {
	private static final String TAG = "LocationUtils";

	public static final double DEFAULT_NO_LOCATION = -1111;
	/**
	 * Define a request code to send to Google Play services This code is
	 * returned in Activity.onActivityResult
	 */
	public final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;

	public static boolean isValidLatLon(double... latLon) {
		if (latLon == null || latLon.length < 2 || latLon.length > 2) {
			return false;
		}
		return latLon[0] != DEFAULT_NO_LOCATION && latLon[1] != DEFAULT_NO_LOCATION;
	}

	public static void storeCurrentLocation(Context context, Location location) {
		if (location == null) {
			return;
		}

		double lat = location.getLatitude();
		double lon = location.getLongitude();
		Log.d(TAG, "Lat: " + lat + "; Lon: " + lon);
		PreferenceUtils.setMostRecentLatLon(context, lat, lon);
	}

	/**
	 * Show a dialog returned by Google Play services for the connection error
	 * code
	 * 
	 * @param errorCode
	 *            An error code returned from onConnectionFailed
	 */
	public static void showErrorDialog(int errorCode, Activity activity, FragmentManager fragmentManager) {
		// Get the error dialog from Google Play services
		Dialog errorDialog = GooglePlayServicesUtil.getErrorDialog(errorCode, activity, CONNECTION_FAILURE_RESOLUTION_REQUEST);
		// If Google Play services can provide an error dialog
		if (errorDialog != null) {
			// Create a new DialogFragment in which to show the error dialog
			ErrorDialogFragment errorFragment = new ErrorDialogFragment();
			// Set the dialog in the DialogFragment
			errorFragment.setDialog(errorDialog);
			// Show the error dialog in the DialogFragment
			errorFragment.show(fragmentManager, TAG);
		}
	}

	public static boolean servicesConnected(Activity activity, FragmentManager fragmentManager) {
		int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(activity);

		if (ConnectionResult.SUCCESS == resultCode) {
			// In debug mode, log the status
			Log.d(TAG, "Google Play services is available");
			return true;
			// Google Play services was not available for some reason
		} else {
			// Display an error dialog
			Dialog dialog = GooglePlayServicesUtil.getErrorDialog(resultCode, activity, 0);
			if (dialog != null) {
				ErrorDialogFragment errorFragment = new ErrorDialogFragment();
				errorFragment.setDialog(dialog);
				errorFragment.show(fragmentManager, TAG);
			}
			return false;
		}
	}

}
