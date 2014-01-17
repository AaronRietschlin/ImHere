package com.asa.imhere.jobs;

import android.util.Log;

import com.asa.imhere.foursquare.Foursquare;
import com.asa.imhere.foursquare.FsVenue;
import com.asa.imhere.model.responses.VenueResponse;
import com.asa.imhere.otto.VenueRetreivedEvent;
import com.asa.imhere.utils.LogUtils;
import com.crashlytics.android.Crashlytics;
import com.google.gson.reflect.TypeToken;
import com.koushikdutta.ion.Ion;
import com.path.android.jobqueue.Params;

public class FetchVenueDetailJob extends BaseJob {
    private static final String TAG = LogUtils.makeLogTag("FetchVenueDetailJob");

    private String mVenueId;

    public FetchVenueDetailJob(String venueId) {
        super(new Params(Priority.LOW).requireNetwork().groupBy(TAG));
        mVenueId = venueId;
    }

    @Override
    public void onAdded() {

    }

    @Override
    public void onRun() throws Throwable {
        String url = Foursquare.constructVenueUrl(mVenueId, getContext());
        VenueResponse response = Ion.with(getContext(), url).group(TAG).as(new TypeToken<VenueResponse>() {
        }).get();

        if (response == null) {
            throwException("Response was null with URL: " + url);
            return;
        }
        checkIfResponseIsValid(response, TAG);
        VenueResponse.Response responseObj = response.getResponse();
        if (responseObj == null) {
            Crashlytics.log(Log.ERROR, TAG, "An error occurred retrieving Venue details. The Response item inside of VenueResponse was null.");
            return;
        }
        FsVenue venue = responseObj.getVenue();
        if (venue == null) {
            Crashlytics.log(Log.ERROR, TAG, "An error occurred retrieving Venue details. The FsVenue item was null.");
            return;
        }
        postEventToMain(new VenueRetreivedEvent(venue));
    }

    @Override
    protected void onCancel() {
        LogUtils.LOGD(TAG, "");
    }

    @Override
    protected boolean shouldReRunOnThrowable(Throwable throwable) {
        return false;
    }
}
