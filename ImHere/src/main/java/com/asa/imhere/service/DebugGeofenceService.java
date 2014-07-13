package com.asa.imhere.service;

import android.app.IntentService;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.text.TextUtils;
import android.widget.Toast;

import com.asa.imhere.lib.foursquare.FsPhoto;
import com.asa.imhere.lib.foursquare.FsVenue;
import com.asa.imhere.lib.wear.WearUtils;
import com.crashlytics.android.Crashlytics;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;
import com.koushikdutta.ion.Ion;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import timber.log.Timber;

/**
 * Created by Aaron on 7/13/2014.
 */
public class DebugGeofenceService extends Service implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    private static final String TAG = "DebugGeofenceService";

    private String mName;
    private String mImageUrl;
    private String mVenueId;

    private GoogleApiClient mApiClient;

    public static void startService(Context context, FsVenue venue) {
        Intent intent = new Intent(context, DebugGeofenceService.class);
        String venueId = venue.getVenueId();
        intent.putExtra("venueId", venueId);
        String name = venue.getName();
        intent.putExtra("name", name);

        /// Set the image
        FsVenue.VenuePhotoGroup photoGroup = venue.getPhotos();
        if (photoGroup != null) {
            List<FsVenue.VenuePhotoItem> photoItemList = photoGroup.getItems();
            if (photoItemList != null && photoItemList.size() > 0) {
                FsVenue.VenuePhotoItem photoItem = photoItemList.get(0);
                if (photoItem != null) {
                    List<FsPhoto> photoList = photoItem.getItems();
                    if (photoList != null && photoList.size() > 0) {
                        FsPhoto photo = photoList.get(0);
                        String imageUrl = photo.getFullUrl(photo.getHeight(), photo.getWidth());
                        intent.putExtra("imageUrl", imageUrl);
                    } else {
                        setNoPhoto("Venue photo list was null or empty.");
                    }
                } else {
                    setNoPhoto("Venue photo list was null or empty.");
                }
            } else {
                setNoPhoto("Venue photo item list was null or empty.");
            }
        } else {
            setNoPhoto("Venue Photo Group was null.");
        }

        context.startService(intent);
    }

    private static void setNoPhoto(String message) {
        Timber.e(message);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent == null) {
            return START_NOT_STICKY;
        }

        mName = intent.getStringExtra("name");
        mVenueId = intent.getStringExtra("venueId");
        mImageUrl = intent.getStringExtra("imageUrl");

        if (mApiClient == null) {
            mApiClient = new GoogleApiClient.Builder(getApplicationContext()).addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this).addApi(Wearable.API).build();
        }

        if (!mApiClient.isConnected() || !mApiClient.isConnecting()) {
            mApiClient.connect();
        }

        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mApiClient != null) {
            mApiClient.disconnect();
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        getNodes();
    }

    @Override
    public void onConnectionSuspended(int i) {
        // TODO - Implement
        stopSelf();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        // TODO - Implement
        stopSelf();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // NO-OP
        return null;
    }

    private void getNodes() {
        Wearable.NodeApi.getConnectedNodes(mApiClient).setResultCallback(new ResultCallback<NodeApi.GetConnectedNodesResult>() {
            @Override
            public void onResult(NodeApi.GetConnectedNodesResult nodes) {
                for (Node node : nodes.getNodes()) {
                    String id = node.getId();
                    if (!TextUtils.isEmpty(id)) {
                        sendMessage(id);
                    }
                }
                stopSelf();
            }
        });
    }

    private void sendMessage(String id){
        Wearable.MessageApi.sendMessage(mApiClient,
                id, WearUtils.buildStartCheckinActivityPath(), null).setResultCallback(new ResultCallback<MessageApi.SendMessageResult>() {
            @Override
            public void onResult(MessageApi.SendMessageResult result) {
                if (!result.getStatus().isSuccess()) {
                    Timber.e("ERROR: failed to send Message: " + result.getStatus());
                } else {
                    Timber.d("Message Sent. Result status: " + result.getStatus());
                }
            }
        });
    }

}
