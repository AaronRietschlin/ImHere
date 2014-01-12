package com.asa.imhere.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;
import android.util.Log;

public class PreferenceUtils {
	private final static String TAG = "PreferenceUtils";

	public static SharedPreferences prefs;
	public static boolean useDefault;

	public static final String NAME = "imhere_prefs";

	public static final String KEY_AUTH_TOKEN = "auth_token";
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

	public static void setAuthToken(Context context, String authToken) {
		buildSharedPreferences(context);
		commit(prefs.edit().putString(KEY_AUTH_TOKEN, authToken));
	}

	public static String getAuthToken(Context context) {
		buildSharedPreferences(context);
		return prefs.getString(KEY_AUTH_TOKEN, null);
	}

	/**
	 * Builds the {@link #prefs} object with the name {@link #NAME}.
	 * 
	 * @param context
	 * @return
	 */
	public static SharedPreferences buildSharedPreferences(Context context) {
		if (useDefault) {
			return buildDefaultSharedPreferences(context);
		}
		if (prefs == null) {
			prefs = context.getSharedPreferences(NAME, Context.MODE_PRIVATE);
		}
		return prefs;
	}

	public static SharedPreferences buildDefaultSharedPreferences(Context context) {
		if (prefs == null) {
			prefs = PreferenceManager.getDefaultSharedPreferences(context);
		}
		useDefault = true;
		return prefs;
	}

	/**
	 * Commits the given SharedPreferences.Editor. If the device is greater than
	 * API 11, it will use {@link Editor#apply()}, otherwise it will use
	 * {@link Editor#commit()}.
	 * 
	 * @param editor
	 * @return Returns true if applied. It will only return false if
	 *         {@link Editor#commit()} fails.
	 */
	@SuppressLint("NewApi")
	public static boolean commit(SharedPreferences.Editor editor) {
		if (Utils.isGingerBreadOrGreater()) {
			editor.apply();
			return true;
		} else {
			return editor.commit();
		}
	}

}
