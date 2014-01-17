package com.asa.imhere;

import android.app.Activity;

public class AppData {

    public static final String AUTHORITY = "com.asa.imhere.provider";

	public static class Extras {
		public static final String LATITUDE = "lat";
		public static final String LONGITUDE = "lon";

		public static final String VENUE_ID = "venue_id";
        public static final String VENUE_NAME = "venue_name";
	}

	/**
	 * Codes that are used for starting an Activity with
	 * {@link android.app.Activity#startActivityForResult(android.content.Intent, int)}.
	 */
	public static class ActivityCodes {
		/** Used for starting the AuthWebViewActivity. */
		public static final int ACTIVITY_FOURSQUARE_AUTH = 888;
	}

	public static class CrashlyticKeys {
		public static final String LOCATION_CLIENT_INTENT_HAS_ERROR = "location_client_intent_has_error";
		public static final String DID_ENTER_PENDING_INTENT = "did_enter_pending_intent";
		public static final String VENUE_ID = "venue_id";
		public static final String TRANSITION_TYPE = "geofence_transition_type";
	}

	public static class Analytics {
		public static final String EVENT_CHECKIN = "check_in";
		public static final String ACTION_CHECKIN = "check_in";
	}

    public static class Loaders{
        public static final int ID_FAVORITES = 10;
    }

}
