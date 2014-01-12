package com.asa.imhere;

import android.content.Context;

import com.android.volley.RequestQueue;
import com.asa.okvolley.OkVolley;

public class VolleyProvider {

	// Singleton
	private static RequestQueue sRequestQueue;

	public static RequestQueue getRequestQueue(Context context) {
		if (sRequestQueue == null) {
			sRequestQueue = OkVolley.newRequestQueue(context);
		}
		return sRequestQueue;
	}

}
