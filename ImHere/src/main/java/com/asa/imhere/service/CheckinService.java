package com.asa.imhere.service;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.asa.imhere.lib.foursquare.FsUtils;
import com.asa.imhere.lib.foursquare.FsVenue;
import com.asa.imhere.notifications.CheckinNotification;

import timber.log.Timber;

public class CheckinService extends IntentService {
    private static final String TAG = "CheckinService";

    private static final String EXTRA_VENUE_ID = "venue_id";

    public CheckinService() {
        super(TAG);
    }

    public static Intent getIntent(Context context, String venueId){
        Intent intent = new Intent(context, CheckinService.class);
        intent.putExtra(EXTRA_VENUE_ID, venueId);
        return intent;
    }

    public static Intent getIntent(Context context, FsVenue venue){
        Intent intent = new Intent(context, CheckinService.class);
        intent.putExtra(EXTRA_VENUE_ID, venue.getVenueId());
        return intent;
    }

    public static void startService(Context context, String venueId) {
        context.startService(getIntent(context, venueId));
    }

    public static void startService(Context context, FsVenue venue) {
        context.startService(getIntent(context, venue));
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent == null) {
            return;
        }
        // TODO - get the location possibly.

        String venueId = intent.getStringExtra(EXTRA_VENUE_ID);
        if (TextUtils.isEmpty(venueId)) {
            return;
        }
        // TODO - maybe not cancel until it's done?
        CheckinNotification.cancel(getApplicationContext());
        String url = FsUtils.constructCheckinsUrl(getApplicationContext(), venueId, -1L, -1L, -1, null, null);
        Timber.d("URL (%s).", url);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // TODO - if getting the location, close it here.
    }
}
