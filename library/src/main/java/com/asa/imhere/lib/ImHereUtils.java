package com.asa.imhere.lib;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Build;
import android.text.format.DateUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.webkit.WebView;

import java.util.List;

public class ImHereUtils {

    /**
     * Checks if the given Intent can be used on the users device.
     *
     * @param context
     * @param intent
     * @return {@code true} if the Intent can be fired, {@code false} otherwise.
     */
    public static boolean isIntentAvailable(Context context, Intent intent) {
        if (intent == null) {
            return false;
        }
        PackageManager mgr = context.getPackageManager();
        List<ResolveInfo> list = mgr.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
        return list.size() > 0;
    }

    /**
     * This method convets dp unit to equivalent device specific value in
     * pixels.
     *
     * @param dp      A value in dp(Device independent pixels) unit. Which we need
     *                to convert into pixels
     * @param context Context to get resources and device specific display metrics
     * @return A float value to represent Pixels equivalent to dp according to
     * device
     */
    public static float convertDpToPixel(float dp, Context context) {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float px = dp * (metrics.densityDpi / 160f);
        return px;
    }

    /**
     * This method converts device specific pixels to device independent pixels.
     *
     * @param px      A value in px (pixels) unit. Which we need to convert into db
     * @param context Context to get resources and device specific display metrics
     * @return A float value to represent db equivalent to px value
     */
    public static float convertPixelsToDp(float px, Context context) {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float dp = px / (metrics.densityDpi / 160f);
        return dp;
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public static void startActivity(Activity activity, View anchorView, Intent intent) {
        if (isJellyBeanOrGreater() && anchorView != null) {
            ActivityOptions opts = ActivityOptions.makeScaleUpAnimation(anchorView, 0, 0, anchorView.getWidth(), anchorView.getHeight());
            activity.startActivity(intent, opts.toBundle());
        } else {
            activity.startActivity(intent);
        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public static void startActivityForResult(Activity activity, View anchorView, Intent intent, int requestCode) {
        if (isJellyBeanOrGreater() && anchorView != null) {
            ActivityOptions opts = ActivityOptions.makeScaleUpAnimation(anchorView, 0, 0, anchorView.getWidth(), anchorView.getHeight());
            activity.startActivityForResult(intent, requestCode, opts.toBundle());
        } else {
            activity.startActivityForResult(intent, requestCode);
        }
    }

    // BEGIN ANDROID VERSION CHECKING

    /**
     * Determines if the device has a version of android that is greater than
     * Android 2.3 (API 9).
     */
    public static boolean isGingerBreadOrGreater() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD;
    }

    /**
     * Determines if the device has a version of android that is greater than
     * Android 2.3.3 (API 10).
     */
    public static boolean isGingerBreaMr1dOrGreater() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD_MR1;
    }

    /**
     * Determines if the device has a version of android that is greater than
     * Android 3.0 (API 11).
     */
    public static boolean isHoneycombOrGreater() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB;
    }

    /**
     * Determines if the device has a version of android that is greater than
     * Android 3.1 (API 12).
     */
    public static boolean isHoneycombMr1OrGreater() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1;
    }

    /**
     * Determines if the device has a version of android that is greater than
     * Android 3.2 (API 13).
     */
    public static boolean isHoneycombMr2OrGreater() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2;
    }

    /**
     * Determines if the device has a version of android that is greater than
     * Android 4.0 (API 14).
     */
    public static boolean isIcsOrGreater() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH;
    }

    /**
     * Determines if the device has a version of android that is greater than
     * Android 4.0.3 (API 15).
     */
    public static boolean isIcsMr1OrGreater() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1;
    }

    /**
     * Determines if the device has a version of android that is greater than
     * Android 4.1 (API 16).
     */
    public static boolean isJellyBeanOrGreater() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN;
    }

    /**
     * Determines if the device has a version of android that is greater than
     * Android 4.2 (API 17).
     */
    public static boolean isJellyBeanMr1OrGreater() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1;
    }

    // END ANDROID VERSION CHECKING

    /**
     * Sets the view visibility.
     *
     * @param view {@link View#VISIBLE} when {@code true}, {@link View#GONE} when
     *             {@code false}.
     * @param show
     */
    public static void setViewVisibility(View view, boolean show) {
        setViewVisibility(view, show, View.GONE);
    }

    /**
     * Sets the view visibility. Allows you to specify whether the view will be
     * gone or invisible.
     *
     * @param view
     * @param show
     * @param goneType Pass in either {@link View#GONE} or {@link View#INVISIBLE}.
     *                 Throws an exception if you pass {@link View#VISIBLE}.
     */
    public static void setViewVisibility(View view, boolean show, int goneType) {
        if (view == null) {
            return;
        }
        if (goneType == View.VISIBLE) {
            throw new IllegalStateException("You must pass either View.INVISIBLE or View.GONE as the goneType. View.VISIBLE does nothing.");
        }
        view.setVisibility(show ? View.VISIBLE : goneType);
    }

    /**
     * Loads the given HTML content into the given WebView.
     *
     * @param wv    The WebView to load into.
     * @param str   A string of raw HTML content
     * @param color The color to make the content
     * @param bold  Tells whether or not to bold the text.
     */
    @SuppressLint("NewApi")
    public static void loadHtmlData(WebView wv, String str, String color, boolean bold) {
        // TODO - Set color according to the pharmacy.
        // str = str.replaceAll("%", "%25");
        String html = "";
        if (color != null) {
            html = "<font color=\"" + color + "\"";
            if (bold)
                html += " style=\"font-weight:bold\"";
            html += ">" + str + "</font>";
        } else {
            html = str;
        }

        wv.loadDataWithBaseURL("", html, "text/html", "utf-8", "");
        if (isHoneycombOrGreater()) {
            // if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
            wv.setBackgroundColor(Color.TRANSPARENT);
            wv.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
            // } else {
            // wv.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
            // }
        } else {
            wv.setBackgroundColor(0x00000000);
        }
    }

    public static String getDurationFromTimeElapsed(long timeElapsed) {
        if (timeElapsed == 0) {
            return null;
        }
        return DateUtils.formatElapsedTime(timeElapsed / 1000);
    }

}
