package com.asa.imhere.wear;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.view.textservice.TextInfo;

import com.asa.imhere.lib.wear.WearUtils;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.data.FreezableUtils;
import com.google.android.gms.wearable.Asset;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataItem;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.Wearable;
import com.google.android.gms.wearable.WearableListenerService;

import java.io.InputStream;
import java.util.List;
import java.util.concurrent.TimeUnit;

import timber.log.Timber;

/**
 * Created by Aaron on 7/13/2014.
 */
public class WearListenerService extends WearableListenerService {

    private GetDataTask mTask;

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        super.onMessageReceived(messageEvent);
        String path = messageEvent.getPath();
        Timber.d("MESSAGE RECEIVED. Path: %s", path);
        if (TextUtils.isEmpty(path)) {
            return;
        }
        if (TextUtils.equals(WearUtils.buildStartCheckinActivityPath(), path)) {
            Intent intent = new Intent(getApplicationContext(), WearCheckinActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
    }

    @Override
    public void onDataChanged(DataEventBuffer dataEvents) {
        super.onDataChanged(dataEvents);
        List<DataEvent> events = FreezableUtils.freezeIterable(dataEvents);
        for (DataEvent event : events) {
            DataItem item = event.getDataItem();
            Uri uri = item.getUri();
            if (TextUtils.equals(uri.getPath(), WearUtils.PATH_SHOWACTIVITY)) {
                DataMap dataMap = DataMapItem.fromDataItem(item).getDataMap();
                String name = dataMap.getString(WearUtils.KEY_VENUE_NAME);
                String title = dataMap.getString(WearUtils.KEY_VENUE_ID);
                Asset asset = dataMap.getAsset(WearUtils.KEY_VENUE_IMAGE);
                VenueInfo info = new VenueInfo(name, title, asset);
                if (asset != null) {
                    startTask(info);
                } else {
                    startCheckinActivity(info);
                }
                Timber.d("");
            }
        }
    }

    @Override
    public void onPeerConnected(Node peer) {
        super.onPeerConnected(peer);
    }

    @Override
    public void onPeerDisconnected(Node peer) {
        super.onPeerDisconnected(peer);
        if (mTask != null) {
            mTask.cancel(true);
        }
    }

    private void startTask(VenueInfo venueInfo) {
        if (mTask != null) {
            AsyncTask.Status status = mTask.getStatus();
            if (status == AsyncTask.Status.FINISHED || status == AsyncTask.Status.RUNNING) {
                mTask.cancel(true);
            }
            mTask = null;
        }
        mTask = new GetDataTask();
        mTask.execute(venueInfo);
    }

    private class GetDataTask extends AsyncTask<VenueInfo, Void, VenueInfo> {

        @Override
        protected VenueInfo doInBackground(VenueInfo... params) {
            if (params == null || params.length == 0) {
                return null;
            }
            VenueInfo info = params[0];
            GoogleApiClient googleApiClient = new GoogleApiClient.Builder(getApplicationContext())
                    .addApi(Wearable.API).build();
            ConnectionResult connectionResult = googleApiClient.blockingConnect(5000, TimeUnit.MILLISECONDS);
            if (!connectionResult.isSuccess()) {
                return info;
            }
            InputStream assetInputStream = Wearable.DataApi.getFdForAsset(googleApiClient, info.asset).await().getInputStream();
            googleApiClient.disconnect();
            if (assetInputStream == null) {
                return info;
            }
            Bitmap bm = BitmapFactory.decodeStream(assetInputStream);
            info.bm = bm;
            return info;
        }

        @Override
        protected void onPostExecute(VenueInfo venueInfo) {
            if (isCancelled()) {
                return;
            }
            if (venueInfo == null) {
                Timber.e("An error occurred starting checkin activity.");
                return;
            }
            startCheckinActivity(venueInfo);
            Timber.d("");
        }
    }

    private void startCheckinActivity(VenueInfo venueInfo) {
        if (venueInfo != null) {
            Intent intent = WearCheckinActivity.getIntent(getApplicationContext(), venueInfo.name,
                    venueInfo.venueId, venueInfo.bm);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
    }

    private class VenueInfo {
        String name;
        String venueId;
        Bitmap bm;
        Asset asset;

        private VenueInfo(String name, String venueId, Asset asset) {
            this.name = name;
            this.venueId = venueId;
            this.asset = asset;
        }
    }
}
