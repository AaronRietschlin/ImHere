package com.asa.imhere.jobs;

import com.asa.imhere.IHApplication;
import com.asa.imhere.SerializationProvider;
import com.asa.imhere.foursquare.ExploreGroup;
import com.asa.imhere.foursquare.Foursquare;
import com.asa.imhere.foursquare.FsVenue;
import com.asa.imhere.model.responses.ExploreResponse;
import com.asa.imhere.otto.ExploreVenuesRetreived;
import com.asa.imhere.utils.LogUtils;
import com.crashlytics.android.Crashlytics;
import com.koushikdutta.ion.Ion;
import com.path.android.jobqueue.Params;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

public class FetchVenuesExploreJob extends BaseJob {
    private static final String TAG = LogUtils.makeLogTag("FetchVenuesExploreJob");
    private static final AtomicInteger jobCounter = new AtomicInteger(0);
    private final int id;

    private double mLatitude;
    private double mLongitude;

    private ArrayList<FsVenue> mVenuesRetreived;

    public FetchVenuesExploreJob(double latitude, double longitude) {
        super(new Params(Priority.LOW).requireNetwork().groupBy(TAG));
        id = jobCounter.incrementAndGet();

        mLatitude = latitude;
        mLongitude = longitude;
    }

    @Override
    public void onAdded() {
        // So far, do nothing because we do not need to
    }

    @Override
    public void onRun() throws Throwable {
        if (id != jobCounter.get()) {
            //looks like other fetch jobs has been added after me. no reason to keep fetching
            //many times, cancel me, let the other one fetch the locations.
            return;
        }

        String url = Foursquare.constructExploreUrl(null, mLatitude, mLongitude, IHApplication.getContext());
        String result = Ion.with(IHApplication.getContext(), url).group(TAG).asString().get();
        if (result == null) {
            Exception e = new Exception("No result found for request: " + url);
            Crashlytics.logException(e);
            throw e;
        }
        ExploreResponse fullResponse = new SerializationProvider<ExploreResponse>().serialize(result, ExploreResponse.class);
        mVenuesRetreived = new ArrayList<FsVenue>();
        // The structure of the "explore" api is verbose. Using an
        // ExploreResponse object that houses the "Reponse" object that
        // houses a list of "ExploreGroup"s, each of which houses a list of
        // "Items", each of which houses a venue. That's what we want
        ExploreResponse.Response response = fullResponse.getResponse();
        ArrayList<ExploreGroup> groups = response.getGroups();
        for (ExploreGroup group : groups) {
            ArrayList<ExploreGroup.Item> items = group.getItems();
            if (items == null) {
                continue;
            }
            for (ExploreGroup.Item item : items) {
                if (item == null) {
                    continue;
                }
                FsVenue venue = item.getVenue();
                if (venue != null) {
                    mVenuesRetreived.add(venue);
                }
            }
        }
        postEventToMain(new ExploreVenuesRetreived(mVenuesRetreived));
    }

    @Override
    protected void onCancel() {
        LogUtils.LOGD(TAG, "Test");
    }

    @Override
    protected boolean shouldReRunOnThrowable(Throwable throwable) {
        LogUtils.LOGD(TAG, "Test");
        return true;
    }
}