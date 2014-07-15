package com.asa.imhere.service;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;

import com.asa.imhere.IHApplication;
import com.asa.imhere.lib.foursquare.FsUtils;
import com.asa.imhere.lib.foursquare.FsVenue;
import com.asa.imhere.lib.otto.BusProvider;
import com.asa.imhere.notifications.CheckinNotification;
import com.asa.imhere.lib.otto.CheckinStatusEvent;
import com.asa.imhere.utils.DebugVenueProvider;
import com.crashlytics.android.Crashlytics;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.Response;

import java.util.concurrent.ExecutionException;

import timber.log.Timber;

public class CheckinService extends IntentService {
    private static final String TAG = "CheckinService";

    private static final String EXTRA_VENUE_ID = "venue_id";

    public CheckinService() {
        super(TAG);
    }

    public static Intent getIntent(Context context, String venueId) {
        Intent intent = new Intent(context, CheckinService.class);
        intent.putExtra(EXTRA_VENUE_ID, venueId);
        return intent;
    }

    public static Intent getIntent(Context context, FsVenue venue) {
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
    public void onCreate() {
        super.onCreate();
        Timber.tag(TAG);
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
        try {
            String checkinJsonResult = "";
            if (!IHApplication.DEBUG) {
                Response<String> response = Ion.with(getApplicationContext()).load("POST", url)
                        .group(TAG).asString().withResponse().get();
                if (response == null || response.getHeaders() == null) {
                    logToCrashlytics(url, null, -1);
                    Timber.e(new Exception("Checkin response was null"), "");
                    return;
                }
                int responseCode = response.getHeaders().getResponseCode();
                checkinJsonResult = response.getResult();
                if (TextUtils.isEmpty(checkinJsonResult)) {
                    logToCrashlytics(url, checkinJsonResult, responseCode);
                    Timber.e(new Exception("Checkin response was null"), "");
                    return;
                }
            } else {
                checkinJsonResult = DebugVenueProvider.getCheckinResponseString(getApplicationContext());
                Timber.e("");
//                postEventToMainThread(new CheckinStatusEvent(true));
                BusProvider.postThreaded(new CheckinStatusEvent(true));
            }
            // TODO -
        } catch (InterruptedException e) {
            logToCrashlytics(url, null, -1);
            Timber.e(e, "");
        } catch (ExecutionException e) {
            logToCrashlytics(url, null, -1);
            Timber.e(e, "");
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // TODO - if getting the location, close it here.
    }

    private void logToCrashlytics(String url, String jsonResult, int responseCode) {
        if (!TextUtils.isEmpty(url)) {
            Crashlytics.setString("URL", url);
        }
        if (!TextUtils.isEmpty(jsonResult)) {
            Crashlytics.setString("RESULT", jsonResult);
        }
        if (responseCode > 0) {
            Crashlytics.setInt("RSPONSE_CODE", responseCode);
        }
    }

    private void postEventToMainThread(final Object object) {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                BusProvider.post(object);
            }
        });
    }

}
