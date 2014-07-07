package com.asa.imhere.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.asa.imhere.lib.FsPreferenceUtils;

public class PreferenceUtils extends FsPreferenceUtils{
	private final static String TAG = "PreferenceUtils";

	public static final String KEY_MOST_RECENT_LAT = "most_recent_lat";
	public static final String KEY_MOST_RECENT_LON = "most_recent_lon";

	public static void setMostRecentLatLon(Context context, double... latLon) {
		if (prefs == null) {
			buildSharedPreferences(context);
		}
		SharedPreferences.Editor editor = prefs.edit();
		editor.putFloat(KEY_MOST_RECENT_LAT, (float) latLon[0]);
		editor.putFloat(KEY_MOST_RECENT_LON, (float) latLon[1]);
		commit(editor);
	}

	public static double[] getMostRecentLatLon(Context context) {
		if (prefs == null) {
			buildSharedPreferences(context);
		}
		double[] latLon = new double[2];
		float latF = prefs.getFloat(KEY_MOST_RECENT_LAT, (float) LocationUtils.DEFAULT_NO_LOCATION);
		float lonF = prefs.getFloat(KEY_MOST_RECENT_LON, (float) LocationUtils.DEFAULT_NO_LOCATION);
		if (latF == LocationUtils.DEFAULT_NO_LOCATION) {
			Log.d(TAG, "There was no stored values of latitude in the shared preferences.");
		}
		if (lonF == LocationUtils.DEFAULT_NO_LOCATION) {
			Log.d(TAG, "There was no stored values of longitude in the shared preferences.");
		}
		latLon[0] = (double) latF;
		latLon[1] = (double) lonF;
		return latLon;
	}

}
