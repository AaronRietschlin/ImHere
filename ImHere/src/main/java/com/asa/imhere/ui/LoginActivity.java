package com.asa.imhere.ui;

import android.os.Bundle;

import com.asa.imhere.R;

public class LoginActivity extends AsaBaseActivity {
	public static final String TAG = "LoginActivity";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_detail);

		addFragment(new LoginFragment(), LoginFragment.TAG, false, false);
	}

}
