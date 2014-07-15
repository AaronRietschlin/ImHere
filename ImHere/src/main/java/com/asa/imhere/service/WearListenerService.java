package com.asa.imhere.service;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.text.TextUtils;

import com.asa.imhere.lib.otto.BusProvider;
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
import com.squareup.otto.Subscribe;

import java.io.InputStream;
import java.util.List;
import java.util.concurrent.TimeUnit;

import timber.log.Timber;

/**
 * This is the {@link com.google.android.gms.wearable.WearableListenerService} for the phone.
 */
public class WearListenerService extends WearableListenerService {

    @Override
    public void onDataChanged(DataEventBuffer dataEvents) {
        super.onDataChanged(dataEvents);
        List<DataEvent> events = FreezableUtils.freezeIterable(dataEvents);
        for (DataEvent event : events) {
            DataItem item = event.getDataItem();
            Uri uri = item.getUri();
            if (TextUtils.equals(uri.getPath(), WearUtils.PATH_CHECKIN)) {
                DataMap dataMap = DataMapItem.fromDataItem(item).getDataMap();
                String venueId = dataMap.getString(WearUtils.KEY_VENUE_ID);
                if (!TextUtils.isEmpty(venueId)) {
                    CheckinService.startService(getApplicationContext(), venueId);
                }
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
    }
}
