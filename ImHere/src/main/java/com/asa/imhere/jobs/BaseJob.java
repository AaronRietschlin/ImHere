package com.asa.imhere.jobs;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.asa.imhere.IHApplication;
import com.asa.imhere.lib.foursquare.FsException;
import com.asa.imhere.lib.foursquare.FsMeta;
import com.asa.imhere.model.responses.BaseResponseItem;
import com.asa.imhere.otto.BusProvider;
import com.crashlytics.android.Crashlytics;
import com.path.android.jobqueue.Job;
import com.path.android.jobqueue.Params;

/**
 * Created by Aaron on 1/16/14.
 */
public abstract class BaseJob extends Job {

    protected Handler mHandlerMain;
    protected Context mContext;

    protected BaseJob(Params params) {
        super(params);
        mHandlerMain = new Handler(Looper.getMainLooper());
        mContext = IHApplication.getContext();
    }

    /**
     * Allows the ability to post the results on the Main Thread.
     *
     * @param obj
     */
    protected void postEventToMain(final Object obj) {
        mHandlerMain.post(new Runnable() {
            @Override
            public void run() {
                BusProvider.post(obj);
            }
        });
    }

    protected Context getContext() {
        if (mContext == null) {
            mContext = IHApplication.getContext();
        }
        return mContext;
    }

    @Override
    protected boolean shouldReRunOnThrowable(Throwable throwable) {
        if (throwable instanceof FsException) {
            // Here, we check to see what type of Foursquare error there was.
            FsException e = (FsException) throwable;
            FsMeta meta = e.getMeta();
            if (meta == null) {
                return true;
            }
            int code = meta.getCode();
            if (code == FsMeta.Code.UNAUTHORIZE) {
                // If the user was somehow unauthorized, then we need to do something about that.
                // TODO - unauth
                return false;
            }
            // If it's some other error code from 400 - 500, then don't retry because that was an error that won't be resolved with another attempt.
            return code < FsMeta.Code.BAD_RESPONSE || code >= 499;
        }
        return true;
    }
    /** Creates an exception with the given message, posts it to Crashlytics and then throws it.*/
    protected void throwException(String message) throws Throwable {
        Exception e = new Exception(message);
        Crashlytics.logException(e);
        throw e;
    }

    /**
     * Throws a {@link com.asa.imhere.foursquare.FsException} if it's not valid.
     *
     * @param item
     * @param tag
     * @return
     * @throws Exception
     */
    public static boolean checkIfResponseIsValid(BaseResponseItem item, String tag) throws Exception {
        if (item == null) {
            // This should never happen, but just in case
            throw new Exception("Attempting to check if Foursquare API response was valid. Was null. TAG: " + tag);
        }
        FsMeta meta = item.getMeta();
        if (meta == null) {
            throw new Exception("Attempting to check if Foursquare Meta data was valid. Was null. TAG: " + tag);
        }
        int code = meta.getCode();
        if (code != FsMeta.Code.OK) {
            // Log to crashlytics what happened.
            Crashlytics.log(Log.WARN, tag,
                    "Error code: " + code + "; Error type: " + meta.getErrorType() + "; Error Detail: " + meta.getErrorDetail() + "; Error Message: " + meta.getErrorMessage());
            if (code == FsMeta.Code.BAD_RESPONSE) {
                Crashlytics.log(Log.WARN, tag, "An invalid auth occurred.");
            }
            // TODO handle the rest of the possible error responses.
            throw new FsException(meta);
        } else {
            return true;
        }
    }
}
