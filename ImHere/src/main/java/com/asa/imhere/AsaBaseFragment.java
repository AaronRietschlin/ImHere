package com.asa.imhere;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;

import com.asa.imhere.ui.AsaBaseActivity;

public class AsaBaseFragment extends Fragment {

	protected AsaBaseActivity mActivity;
	protected View mLoadingLayout;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		mActivity = (AsaBaseActivity) activity;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		mLoadingLayout = view.findViewById(R.id.loading);
	}
}
