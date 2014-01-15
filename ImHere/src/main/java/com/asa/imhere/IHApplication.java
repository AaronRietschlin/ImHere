package com.asa.imhere;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.crashlytics.android.Crashlytics;
import com.koushikdutta.ion.Ion;

public class IHApplication extends Application {
	private final static String TAG = "IHApplication";

	public static final boolean DEBUG = true;
	/**
	 * The versioning date that is required by the API.
	 * https://developer.foursquare.com/overview/versioning
	 */
	public static final String VERSIONING_DATE = "20140113";

	private static Context sContext;


	@Override
	public void onCreate() {
		super.onCreate();
        Crashlytics.start(this);

		sContext = getApplicationContext();

		if (DEBUG) {
			Ion.getDefault(sContext).setLogging(TAG, Log.VERBOSE);
		}
	}

	public static final Context getContext() {
		return sContext;
	}

}
