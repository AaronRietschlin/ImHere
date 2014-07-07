package com.asa.imhere;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.crashlytics.android.Crashlytics;
import com.koushikdutta.ion.Ion;
import com.path.android.jobqueue.JobManager;
import com.path.android.jobqueue.config.Configuration;
import com.path.android.jobqueue.log.CustomLogger;

import timber.log.Timber;

public class IHApplication extends Application {
    public final static String TAG_PREFIX = "IHApplication_";

    public static final boolean DEBUG = true;

    private static Context sContext;
    private static IHApplication sInstance;
    private JobManager mJobManager;


    @Override
    public void onCreate() {
        super.onCreate();
        Crashlytics.start(this);

        sInstance = this;
        sContext = getApplicationContext();
        configureJobManager();

        if (DEBUG) {
            Ion.getDefault(sContext).setLogging(TAG_PREFIX, Log.VERBOSE);
            Timber.plant(new Timber.DebugTree());
        } else {
            Timber.plant(new CrashReportingTree());
        }

    }

    public static IHApplication getInstance() {
        return sInstance;
    }

    public static final Context getContext() {
        return sContext;
    }

    private void configureJobManager() {
        Configuration configuration = new Configuration.Builder(this)
                .customLogger(new CustomLogger() {
                    private static final String TAG = TAG_PREFIX + "Jobs_";
                    {
                        Timber.tag(TAG);
                    }

                    @Override
                    public boolean isDebugEnabled() {
                        return DEBUG;
                    }

                    @Override
                    public void d(String text, Object... args) {
                        Timber.d(String.format(text, args));
                    }

                    @Override
                    public void e(Throwable t, String text, Object... args) {
                        Timber.e(String.format(text, args), t);
                    }

                    @Override
                    public void e(String text, Object... args) {
                        Timber.e(String.format(text, args));
                    }
                }).build();
//                .minConsumerCount(1)//always keep at least one consumer alive
//                .maxConsumerCount(3)//up to 3 consumers at a time
//                .loadFactor(3)//3 jobs per consumer
//                .consumerKeepAlive(120)//wait 2 minute

        mJobManager = new JobManager(this, configuration);
    }

    public JobManager getJobManager() {
        return mJobManager;
    }

    /**
     * A tree which logs important information for crash reporting.
     */
    private static class CrashReportingTree extends Timber.HollowTree {
        @Override
        public void i(String message, Object... args) {
            Crashlytics.log(String.format(message, args));
        }

        @Override
        public void i(Throwable t, String message, Object... args) {
            i(message, args); // Just add to the log.
        }

        @Override
        public void e(String message, Object... args) {
            i("ERROR: " + message, args); // Just add to the log.
        }

        @Override
        public void e(Throwable t, String message, Object... args) {
            e(message, args);
        }
    }

}
