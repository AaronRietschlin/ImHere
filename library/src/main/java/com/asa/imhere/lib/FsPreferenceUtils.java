package com.asa.imhere.lib;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

public class FsPreferenceUtils {
	private final static String TAG = "FsPreferenceUtils";

	public static SharedPreferences prefs;
	public static boolean useDefault;

	public static final String NAME = "imhere_prefs";

	public static final String KEY_AUTH_TOKEN = "auth_token";
	public static final String KEY_MOST_RECENT_LAT = "most_recent_lat";
	public static final String KEY_MOST_RECENT_LON = "most_recent_lon";


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
	 * API 11, it will use {@link android.content.SharedPreferences.Editor#apply()}, otherwise it will use
	 * {@link android.content.SharedPreferences.Editor#commit()}.
	 *
	 * @param editor
	 * @return Returns true if applied. It will only return false if
	 *         {@link android.content.SharedPreferences.Editor#commit()} fails.
	 */
	@SuppressLint("NewApi")
	public static boolean commit(Editor editor) {
		if (ImHereUtils.isGingerBreadOrGreater()) {
			editor.apply();
			return true;
		} else {
			return editor.commit();
		}
	}

}
