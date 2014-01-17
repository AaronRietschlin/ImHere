package com.asa.imhere.jobs;

import android.os.Handler;
import android.os.Looper;

import com.asa.imhere.otto.BusProvider;
import com.path.android.jobqueue.Job;
import com.path.android.jobqueue.Params;

/**
 * Created by Aaron on 1/16/14.
 */
public abstract class BaseJob extends Job {

    protected Handler mHandlerMain;

    protected BaseJob(Params params) {
        super(params);
        mHandlerMain = new Handler(Looper.getMainLooper());
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
}
