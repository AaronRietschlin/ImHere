package com.asa.imhere.wear;

import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.wearable.activity.InsetActivity;
import android.support.wearable.view.BoxInsetLayout;
import android.support.wearable.view.CardFragment;

import com.asa.imhere.lib.wear.WearUtils;

public class WearCheckinActivity extends InsetActivity {

    private CardFragment mCardFragment;
    private BoxInsetLayout mContainer;

    public static Intent getIntent(Context context, String venueName, String venueId, Bitmap bitmap) {
        Intent intent = new Intent(context, WearCheckinActivity.class);
        intent.putExtra(WearUtils.KEY_VENUE_NAME, venueName);
        intent.putExtra(WearUtils.KEY_VENUE_ID, venueId);
        if (bitmap != null) {
            intent.putExtra(WearUtils.KEY_VENUE_IMAGE, bitmap);
        }
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onReadyForContent() {
        setContentView(R.layout.activity_main);

        mContainer = (BoxInsetLayout) findViewById(R.id.container);

        // Pass the data to the Fragment.
        Intent intent = getIntent();
        if (intent == null || intent.getExtras() == null) {
            finish();
            return;
        }

        setBackground(intent.getExtras());

        mCardFragment = WearCheckinCardFragment.newInstance(intent.getExtras(), isRound());
        FragmentManager fm = getFragmentManager();
        fm.beginTransaction().add(R.id.container, mCardFragment, "CARD").commit();
    }

    private void setBackground(Bundle extras) {
        if(extras == null){
            return;
        }
        Bitmap bm = extras.getParcelable(WearUtils.KEY_VENUE_IMAGE);
        if(bm != null){
            mContainer.setBackground(new BitmapDrawable(getResources(), bm));
        }
    }

}
