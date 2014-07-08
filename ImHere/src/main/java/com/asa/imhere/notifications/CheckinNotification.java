package com.asa.imhere.notifications;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

import com.asa.imhere.R;
import com.asa.imhere.lib.foursquare.FsUtils;
import com.asa.imhere.lib.foursquare.FsVenue;
import com.asa.imhere.service.CheckinService;

/**
 * Helper class for showing and canceling checkin
 * notifications.
 * <p/>
 * This class makes heavy use of the {@link NotificationCompat.Builder} helper
 * class to create notifications in a backward-compatible way.
 */
public class CheckinNotification {
    /**
     * The unique identifier for this type of notification.
     */
    private static final String NOTIFICATION_TAG = "Checkin";

    /**
     * Shows the notification, or updates a previously shown notification of
     * this type, with the given parameters.
     *
     * @see #cancel(Context)
     */
    public static void notify(final Context context, FsVenue venue) {
        final Resources res = context.getResources();

        // Construct the pending intent:
        Intent intent = FsUtils.buildViewVenueIntent(venue.getVenueId());
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 3, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        // This image is used as the notification's large icon (thumbnail).
        Bitmap picture = BitmapFactory.decodeResource(res, R.drawable.ic_launcher);
        String title = res.getString(R.string.notif_title_add_checkin);
        String message = res.getString(R.string.notif_message_add_checkin, venue.getName());

        // Add the checkin action.
        Intent checkinIntent = CheckinService.getIntent(context, venue);
        PendingIntent checkinPendingIntent = PendingIntent.getService(context,
                5, checkinIntent, PendingIntent.FLAG_ONE_SHOT);

        NotificationCompat.WearableExtender extender = new NotificationCompat.WearableExtender();
        extender.addAction(new NotificationCompat.Action(R.drawable.ic_stat_check,
                res.getString(R.string.notif_action_checkin), checkinPendingIntent));

        // Big style
        NotificationCompat.BigTextStyle style = new NotificationCompat.BigTextStyle().bigText(message);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)

                // Set appropriate defaults for the notification light, sound,
                // and vibration.
                .setDefaults(Notification.DEFAULT_ALL).setSmallIcon(R.drawable.ic_stat_foursquare)
                .setContentTitle(title).setContentText(message).setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setLargeIcon(picture).setTicker(message).setContentIntent(pendingIntent).extend(extender)
                .setAutoCancel(true).setStyle(style);

        notify(context, builder.build());
    }

    @TargetApi(Build.VERSION_CODES.ECLAIR)
    private static void notify(final Context context, final Notification notification) {
        final NotificationManagerCompat nm = NotificationManagerCompat.from(context);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ECLAIR) {
            nm.notify(NOTIFICATION_TAG, 0, notification);
        } else {
            nm.notify(NOTIFICATION_TAG.hashCode(), notification);
        }
    }

    /**
     * Cancels any notifications of this type previously shown using
     * {@link #notify(Context, String, int)}.
     */
    @TargetApi(Build.VERSION_CODES.ECLAIR)
    public static void cancel(final Context context) {
        final NotificationManagerCompat nm = NotificationManagerCompat.from(context);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ECLAIR) {
            nm.cancel(NOTIFICATION_TAG, 0);
        } else {
            nm.cancel(NOTIFICATION_TAG.hashCode());
        }
    }
}