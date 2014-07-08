package com.asa.imhere.utils;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.asa.imhere.AppData;
import com.asa.imhere.lib.ImHereUtils;
import com.asa.imhere.lib.foursquare.FsMeta;
import com.asa.imhere.lib.model.responses.BaseResponseItem;
import com.asa.imhere.ui.DetailActivity;
import com.crashlytics.android.Crashlytics;

/**
 * Common methods that are used.
 */
public class Utils extends ImHereUtils{
    @SuppressWarnings("unused")
    private final static String TAG = "Utils";

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public static void launchDetailActivity(Activity context, String venueId, String venueName, View v) {
        Intent intent = new Intent(context, DetailActivity.class);
        intent.putExtra(AppData.Extras.VENUE_ID, venueId);
        if (!TextUtils.isEmpty(venueName)) {
            intent.putExtra(AppData.Extras.VENUE_NAME, venueName);
        }
        if (Utils.isJellyBeanOrGreater() && v != null) {
            ActivityOptions opts = ActivityOptions.makeScaleUpAnimation(v, (int) v.getX(), (int) v.getY(), v.getWidth(), v.getHeight());
            context.startActivity(intent, opts.toBundle());
        } else {
            context.startActivity(intent);
        }
    }

    /**
     * Checks to see if the Foursquare response item has any error data in it
     * and logs it.
     *
     * @param item
     * @param tag
     * @return The code. If this is not 200 ({#link {@link FsMeta.Code#OK}, then
     * something went wrong.
     */
    public static int checkIfResponseIsValid(BaseResponseItem item, String tag) {
        if (item == null) {
            return FsMeta.Code.NULL;
        }
        FsMeta meta = item.getMeta();
        if (meta == null) {
            return FsMeta.Code.NULL;
        }
        int code = meta.getCode();
        if (code != FsMeta.Code.OK) {
            // Log to crashlytics what happened.
            Crashlytics.log(Log.WARN, tag,
                    "Error code: " + code + "; Error type: " + meta.getErrorType() + "; Error Detail: " + meta.getErrorDetail() + "; Error Message: " + meta.getErrorMessage());
            if (code == FsMeta.Code.BAD_RESPONSE) {
                Crashlytics.log(Log.WARN, TAG, "An invalid auth occurred.");
                // TODO - Handle invalid auth
                return FsMeta.Code.BAD_RESPONSE;
            }
            // TODO handle the rest of the possible error responses.
            return code;
        } else {
            return FsMeta.Code.OK;
        }
    }

    /**
     * Determines if an AccessToken has been placed inside of teh
     * SharedPreferences. If not, then the user has not registered.
     *
     * @param context
     * @return
     */
    public static boolean isConnectedToFoursquare(Context context) {
        String accessToken = PreferenceUtils.getAuthToken(context);
        return !TextUtils.isEmpty(accessToken);
    }

    public static void throwException(String message) {
        Exception e = new Exception(message);
        Crashlytics.logException(e);
    }

    public static void throwException(String message, String tag, Exception e) {
        if (message != null) {
            Crashlytics.log(Log.ERROR, tag, message);
        }
        Crashlytics.logException(e);
    }
}
