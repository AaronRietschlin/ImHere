package com.asa.imhere.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.asa.imhere.AppData;
import com.asa.imhere.AsaBaseFragment;
import com.asa.imhere.R;
import com.crashlytics.android.Crashlytics;

public class LoginFragment extends AsaBaseFragment implements View.OnClickListener {
	public static final String TAG = "LoginFragment";

	private ImageButton mBtnConnectFoursquare;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_login, container, false);

		mBtnConnectFoursquare = (ImageButton) v.findViewById(R.id.login_btn_connect_foursquare);

		mBtnConnectFoursquare.setOnClickListener(this);

		return v;
	}

	@Override
	public void onClick(View v) {
		int vId = v.getId();
		switch (vId) {
		case R.id.login_btn_connect_foursquare:
			connectToFoursquare();
			break;
		}
	}

	private void connectToFoursquare() {
		Intent intent = new Intent(mActivity, AuthWebViewActivity.class);
		startActivityForResult(intent, AppData.ActivityCodes.ACTIVITY_FOURSQUARE_AUTH);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case AppData.ActivityCodes.ACTIVITY_FOURSQUARE_AUTH:
			if (resultCode == Activity.RESULT_OK) {
				// Login was good. Start the Main Activity and finish this one.
				startActivity(new Intent(mActivity, MainActivity.class));
				mActivity.finish();
			} else {
				Crashlytics.log(Log.DEBUG, TAG, "An error occurred with the login process. The result code was: " + resultCode);
				return;
			}
			break;
		}
	}
}
