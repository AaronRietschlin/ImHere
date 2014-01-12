package com.asa.imhere;

import android.content.Context;
import android.util.Log;

import com.activeandroid.app.Application;
import com.koushikdutta.ion.Ion;

public class IHApplication extends Application {
	private final static String TAG = "IHApplication";

	public static final boolean DEBUG = true;
	/**
	 * The versioning date that is required by the API.
	 * https://developer.foursquare.com/overview/versioning
	 */
	public static final String VERSIONING_DATE = "20130523";

	private static Context sContext;

	@Override
	public void onCreate() {
		super.onCreate();

		sContext = getApplicationContext();

		if (DEBUG) {
			Ion.getDefault(sContext).setLogging(TAG, Log.VERBOSE);
		}
	}

	public static final Context getContext() {
		return sContext;
	}

}
