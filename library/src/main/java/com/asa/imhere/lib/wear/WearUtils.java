package com.asa.imhere.lib.wear;

import android.graphics.Bitmap;

import java.io.ByteArrayOutputStream;

import timber.log.Timber;

/**
 * Created by Aaron on 7/13/2014.
 */
public class WearUtils {

    public static final String PATH_START = "/start";
    public static final String PATH_SHOWACTIVITY = "/showactivity";
    public static final String PATH_CHECKIN = "/checkin";

    public static final String PATH_CHECKIN_ACTIVITY = "WearCheckinActivity";

    public static final String KEY_VENUE_NAME = "name";
    public static final String KEY_VENUE_ID = "venue_id";
    public static final String KEY_VENUE_IMAGE = "venue_image";
    public static final String KEY_STATUS = "status";

    public static String buildStartCheckinActivityPath() {
        return PATH_START + "/" + PATH_CHECKIN_ACTIVITY;
    }

    public static byte[] bitmapToByteArray(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        return stream.toByteArray();
    }

    public static final int STATUS_CHECKIN = 5278;
    public static final int STATUS_DO_NOTHING = 5532;

    public static final long TIMEOUT_IN_MILLIS = 5000L;

}
