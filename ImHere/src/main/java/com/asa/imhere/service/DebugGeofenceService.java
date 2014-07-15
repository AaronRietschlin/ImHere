package com.asa.imhere.service;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.text.TextUtils;

import com.asa.imhere.lib.foursquare.FsPhoto;
import com.asa.imhere.lib.foursquare.FsVenue;
import com.asa.imhere.lib.wear.WearUtils;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.Asset;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import timber.log.Timber;

/**
 * Created by Aaron on 7/13/2014.
 */
public class DebugGeofenceService extends IntentService {
    private static final String TAG = "DebugGeofenceService";

    private String mName;
    private String mImageUrl;
    private String mVenueId;

    private GoogleApiClient mGoogleApiClient;

    private static int sCount;

    public DebugGeofenceService() {
        super(TAG);
    }

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
                        String imageUrl = photo.getFullUrl(photo.getHeight() / 4, photo.getWidth() / 4);
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
    protected void onHandleIntent(Intent intent) {
        if (intent == null) {
            return;
        }

        mName = intent.getStringExtra("name");
        mVenueId = intent.getStringExtra("venueId");
        mImageUrl = intent.getStringExtra("imageUrl");

        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(getApplicationContext()).addApi(Wearable.API).build();
        }

        if (!mGoogleApiClient.isConnected() || !mGoogleApiClient.isConnecting()) {
            ConnectionResult connectionResult = mGoogleApiClient.blockingConnect();
            if (connectionResult.isSuccess()) {
//                sendMessage();

                if(TextUtils.isEmpty(mImageUrl)) {
                    sendDataMessage(null);
                }else{
                    downloadImage();
                }
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mGoogleApiClient != null) {
            mGoogleApiClient.disconnect();
        }
    }

    private Collection<String> getNodes() {
        HashSet<String> results = new HashSet<String>();
        NodeApi.GetConnectedNodesResult nodes =
                Wearable.NodeApi.getConnectedNodes(mGoogleApiClient).await();
        for (Node node : nodes.getNodes()) {
            results.add(node.getId());
        }
        return results;

    }

    private void sendDataMessage(Bitmap bitmap) {
        PutDataMapRequest dataMapRequest = PutDataMapRequest.create(WearUtils.PATH_SHOWACTIVITY);
        DataMap map = dataMapRequest.getDataMap();
        map.putString(WearUtils.KEY_VENUE_NAME, mName);
        map.putString(WearUtils.KEY_VENUE_ID, mVenueId);
        if(bitmap != null && sCount %2 == 0){
            Asset asset = Asset.createFromBytes(WearUtils.bitmapToByteArray(bitmap));
            if(asset != null) {
                map.putAsset(WearUtils.KEY_VENUE_IMAGE, asset);
            }
        }
        PutDataRequest request = dataMapRequest.asPutDataRequest();
        DataApi.DataItemResult result = Wearable.DataApi.putDataItem(mGoogleApiClient, request).await();
        if(result != null){
            Timber.d("Data Message sent");
        }else{
            Timber.e("Data Message not sent.");
        }
        sCount++;
    }

    private void sendMessage() {
        Collection<String> nodes = getNodes();
        Iterator<String> itr = nodes.iterator();
        while (itr.hasNext()) {
            MessageApi.SendMessageResult result = Wearable.MessageApi.sendMessage(mGoogleApiClient,
                    itr.next(), WearUtils.buildStartCheckinActivityPath(), null).await();
            if (!result.getStatus().isSuccess()) {
                Timber.e("ERROR: failed to send Message: " + result.getStatus());
            } else {
                Timber.d("Message sent.");
            }
            // Only do once.
            break;
        }
    }

    private void downloadImage(){
        if(TextUtils.isEmpty(mImageUrl)){
            sendDataMessage(null);
            return;
        }
        Bitmap bm = ImageLoader.getInstance().loadImageSync(mImageUrl);
        sendDataMessage(bm);
    }

}
