package com.asa.imhere.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.asa.imhere.AsaBaseFragment;
import com.asa.imhere.R;

public class HistoryFragment extends AsaBaseFragment {
	public final static String TAG = "ExploreFragment";

	private ListView mListView;

	public static HistoryFragment newInstance() {
		HistoryFragment frag = new HistoryFragment();

		return frag;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_history, container, false);

		mListView = (ListView) v.findViewById(R.id.history_list);

		return v;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

	}
}
