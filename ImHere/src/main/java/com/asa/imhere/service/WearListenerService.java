package com.asa.imhere.service;

import android.net.Uri;
import android.os.AsyncTask;
import android.text.TextUtils;

import com.asa.imhere.lib.otto.BusProvider;
import com.asa.imhere.lib.wear.WearUtils;
import com.asa.imhere.lib.otto.CheckinStatusEvent;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.data.FreezableUtils;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataItem;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;
import com.google.android.gms.wearable.WearableListenerService;
import com.squareup.otto.Subscribe;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;

import timber.log.Timber;

/**
 * This is the {@link com.google.android.gms.wearable.WearableListenerService} for the phone.
 */
public class WearListenerService extends WearableListenerService {

    private SendMessageTask mTask;

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
                    BusProvider.registerThreaded(this);
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

    @Subscribe
    public void onCheckinStatusEvent(CheckinStatusEvent event) {
        if (event != null) {
            // Now, we want to send over the status to the watch.
            if (mTask != null) {
                AsyncTask.Status status = mTask.getStatus();
                if (status == AsyncTask.Status.RUNNING || status == AsyncTask.Status.FINISHED) {
                    mTask.cancel(true);
                }
                mTask = null;
            }
            mTask = new SendMessageTask();
            mTask.execute(event.getResult());
        }
    }

    /**
     * We need to connect to Google API Client for this. Thus, creating a new thread.
     */
    private class SendMessageTask extends AsyncTask<Integer, Void, Void> {

        @Override
        protected Void doInBackground(Integer... params) {
            if (params == null || params.length == 0) {
                return null;
            }
            int status = params[0];
            // Create a GoogleAPIClient for this.
            GoogleApiClient client = new GoogleApiClient.Builder(getApplicationContext()).addApi(Wearable.API).build();
            ConnectionResult connectionResult = client.blockingConnect(WearUtils.TIMEOUT_IN_MILLIS,
                    TimeUnit.MILLISECONDS);
            if (!connectionResult.isSuccess() || isCancelled()) {
                return null;
            }
            Collection<String> nodes = getNodes(client);
            Iterator<String> itr = nodes.iterator();
            while (itr.hasNext()) {
                if (isCancelled()) {
                    return null;
                }
                MessageApi.SendMessageResult result = Wearable.MessageApi.sendMessage(client,
                        itr.next(), WearUtils.buildCheckinStatusPath(status), null).await();
                if (!result.getStatus().isSuccess()) {
                    Timber.e("ERROR: failed to send Message: " + result.getStatus());
                } else {
                    Timber.d("Message sent.");
                }
                // Only do once.
                break;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            try {
                BusProvider.unregisterThreaded(WearListenerService.this);
            } catch (Exception e) {
                Timber.e("Already unregistered in this class");
            }
        }
    }

    private Collection<String> getNodes(GoogleApiClient client) {
        HashSet<String> results = new HashSet<String>();
        NodeApi.GetConnectedNodesResult nodes =
                Wearable.NodeApi.getConnectedNodes(client).await();
        for (Node node : nodes.getNodes()) {
            results.add(node.getId());
        }
        return results;
    }
}
