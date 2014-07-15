package com.asa.imhere.wear;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.wearable.activity.ConfirmationActivity;
import android.support.wearable.view.CardFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.asa.imhere.lib.otto.BusProvider;
import com.asa.imhere.lib.otto.CheckinStatusEvent;
import com.asa.imhere.lib.wear.WearUtils;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;
import com.squareup.otto.Subscribe;

import java.util.concurrent.TimeUnit;

import butterknife.ButterKnife;
import butterknife.InjectView;
import timber.log.Timber;

public class WearCheckinCardFragment extends CardFragment implements View.OnClickListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    public static final String TAG = "WearCheckinCardFragment";
    public static final String KEY_IS_ROUND = "WearCheckinCardFragment_is_round";

    @InjectView(R.id.checkin_card_tv_text)
    TextView mTvText;
    @InjectView(R.id.wear_card_container)
    LinearLayout mContainer;
    @InjectView(R.id.checkin_card_img_check)
    ImageView mImgCheck;

    private String mVenueName;
    private String mVenueId;
    private boolean mIsRound;

    private GoogleApiClient mGoogelApiClient;
    private Context mContext;

    public static WearCheckinCardFragment newInstance(Bundle extras, boolean isRound) {
        WearCheckinCardFragment frag = new WearCheckinCardFragment();
        extras.putBoolean(KEY_IS_ROUND, isRound);
        frag.setArguments(extras);
        return frag;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mContext = activity.getApplicationContext();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args == null) {
            throw new IllegalStateException("You must use one of the newInstance constructors.");
        }
        mVenueName = args.getString(WearUtils.KEY_VENUE_NAME);
        mVenueId = args.getString(WearUtils.KEY_VENUE_ID);
        mIsRound = args.getBoolean(KEY_IS_ROUND);

        mGoogelApiClient = new GoogleApiClient.Builder(mContext).addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this).addApi(Wearable.API).build();
    }

    @Override
    public View onCreateContentView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = null;
        if (mIsRound) {
            /// TODO - inflate different for a round?
        }
        v = inflater.inflate(R.layout.rect_activity_main, container, false);
        ButterKnife.inject(this, v);

        mImgCheck.setOnClickListener(this);

        mTvText.setText(getString(R.string.checkin_message, mVenueName));

        return v;
    }

    @Override
    public void onStart() {
        super.onStart();
        BusProvider.registerThreaded(this);
        if (mGoogelApiClient != null && !mGoogelApiClient.isConnected() && !mGoogelApiClient.isConnecting()) {
            mGoogelApiClient.connect();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        BusProvider.unregisterThreaded(this);
        if (mGoogelApiClient != null) {
            mGoogelApiClient.disconnect();
        }
    }

    private static int sCount = 0;

    private void sendSuccess() {
        if (mGoogelApiClient == null || !mGoogelApiClient.isConnected()) {
            Toast.makeText(mContext, "Not connected to GMS.", Toast.LENGTH_LONG).show();
            return;
        }
        PutDataMapRequest dataMapRequest = PutDataMapRequest.create(WearUtils.PATH_CHECKIN);
        dataMapRequest.getDataMap().putString(WearUtils.KEY_VENUE_ID, mVenueId);
        dataMapRequest.getDataMap().putString(WearUtils.KEY_VENUE_NAME, mVenueName);
        dataMapRequest.getDataMap().putInt(WearUtils.KEY_STATUS, WearUtils.STATUS_CHECKIN);
        dataMapRequest.getDataMap().putInt("Test", sCount++);
        PutDataRequest request = dataMapRequest.asPutDataRequest();
        Wearable.DataApi.putDataItem(mGoogelApiClient, request).setResultCallback(new ResultCallback<DataApi.DataItemResult>() {
            @Override
            public void onResult(DataApi.DataItemResult dataItemResult) {
                if (dataItemResult.getStatus().isSuccess()) {
                    Timber.d("Successfully sent data through from watch to phone.");
                } else {
                    Timber.e("Unsuccessfully sent data through from watch to phone.");
                }
            }
        }, WearUtils.TIMEOUT_IN_MILLIS, TimeUnit.MILLISECONDS);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.checkin_card_img_check:
                sendSuccess();
                break;
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        // No-op. We don't need anything because we use the .isConnected method.
    }

    @Override
    public void onConnectionSuspended(int i) {
        // TODO - Implement this
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        // TODO - implement this.
    }

    private static final int REQUEST_CODE_CONFIRMATION = 5254;

    @Subscribe
    public void onCheckinStatusEvent(CheckinStatusEvent event) {
        if (event != null && isAdded()) {
            Intent intent = new Intent(getActivity(), ConfirmationActivity.class);
            if (event.isSuccess()) {
                intent.putExtra(ConfirmationActivity.EXTRA_ANIMATION_TYPE, ConfirmationActivity.SUCCESS_ANIMATION);
            } else {
                intent.putExtra(ConfirmationActivity.EXTRA_ANIMATION_TYPE, ConfirmationActivity.FAILURE_ANIMATION);
            }
            startActivityForResult(intent, REQUEST_CODE_CONFIRMATION);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CODE_CONFIRMATION:
                if (resultCode == Activity.RESULT_OK || isAdded()) {
                    getActivity().finish();
                }
                break;
        }
    }
}
