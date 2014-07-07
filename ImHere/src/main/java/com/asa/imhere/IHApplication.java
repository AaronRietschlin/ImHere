package com.asa.imhere;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.asa.imhere.utils.LogUtils;
import com.crashlytics.android.Crashlytics;
import com.koushikdutta.ion.Ion;
import com.path.android.jobqueue.JobManager;
import com.path.android.jobqueue.config.Configuration;
import com.path.android.jobqueue.log.CustomLogger;

public class IHApplication extends Application {
	public  final static String TAG_PREFIX = "IHApplication_";

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
		}
	}

    public static IHApplication getInstance(){
        return sInstance;
    }

	public static final Context getContext() {
		return sContext;
	}

    private void configureJobManager() {
        Configuration configuration = new Configuration.Builder(this)
                .customLogger(new CustomLogger() {
                    private static final String TAG = TAG_PREFIX + "Jobs_";
                    @Override
                    public boolean isDebugEnabled() {
                        return DEBUG;
                    }

                    @Override
                    public void d(String text, Object... args) {
                        LogUtils.LOGD(TAG, String.format(text, args));
                    }

                    @Override
                    public void e(Throwable t, String text, Object... args) {
                        LogUtils.LOGE(TAG, String.format(text, args), t);
                    }

                    @Override
                    public void e(String text, Object... args) {
                        LogUtils.LOGE(TAG, String.format(text, args));
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

}
