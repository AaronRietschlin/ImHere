package com.asa.imhere.utils;

import android.util.Log;

import com.asa.imhere.BuildConfig;
import com.asa.imhere.IHApplication;
import com.crashlytics.android.Crashlytics;

public class LogUtils {
	// TODO _ Change this to the name of the app
	private static final String LOG_PREFIX = IHApplication.TAG_PREFIX;
	private static final int LOG_PREFIX_LENGTH = LOG_PREFIX.length();
	private static final int MAX_LOG_TAG_LENGTH = 23;

	public static String makeLogTag(String str) {
		if (str.length() > MAX_LOG_TAG_LENGTH - LOG_PREFIX_LENGTH) {
			return LOG_PREFIX + str.substring(0, MAX_LOG_TAG_LENGTH - LOG_PREFIX_LENGTH - 1);
		}

		return LOG_PREFIX + str;
	}

	/**
	 * Don't use this when obfuscating class names!
	 */
	public static String makeLogTag(Class cls) {
		return makeLogTag(cls.getSimpleName());
	}

	public static void LOGD(final String tag, String message) {
		if (BuildConfig.DEBUG || Log.isLoggable(tag, Log.DEBUG)) {
			Crashlytics.log(Log.DEBUG, tag, message);
		}
	}

	public static void LOGD(final String tag, String message, Throwable cause) {
		if (BuildConfig.DEBUG || Log.isLoggable(tag, Log.DEBUG)) {
			Crashlytics.log(Log.DEBUG, tag, message);
			Crashlytics.logException(cause);
		}
	}

	public static void LOGV(final String tag, String message) {
		if (BuildConfig.DEBUG && Log.isLoggable(tag, Log.VERBOSE)) {
			Crashlytics.log(Log.VERBOSE, tag, message);
		}
	}

	public static void LOGV(final String tag, String message, Throwable cause) {
		if (BuildConfig.DEBUG && Log.isLoggable(tag, Log.VERBOSE)) {
			Crashlytics.log(Log.VERBOSE, tag, message);
			Crashlytics.logException(cause);
		}
	}

	public static void LOGI(final String tag, String message) {
		Crashlytics.log(Log.INFO, tag, message);
	}

	public static void LOGI(final String tag, String message, Throwable cause) {
		Crashlytics.log(Log.INFO, tag, message);
		Crashlytics.logException(cause);
	}

	public static void LOGW(final String tag, String message) {
		Crashlytics.log(Log.WARN, tag, message);
	}

	public static void LOGW(final String tag, String message, Throwable cause) {
		Crashlytics.log(Log.WARN, tag, message);
		Crashlytics.logException(cause);
	}

	public static void LOGE(final String tag, String message) {
		Crashlytics.log(Log.ERROR, tag, message);
	}

	public static void LOGE(final String tag, String message, Throwable cause) {
		Crashlytics.log(Log.ERROR, tag, message);
		Crashlytics.logException(cause);
	}

	private LogUtils() {
	}
}
