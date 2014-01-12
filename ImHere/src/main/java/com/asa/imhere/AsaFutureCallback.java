package com.asa.imhere;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonSyntaxException;
import com.koushikdutta.async.future.FutureCallback;

public abstract class AsaFutureCallback<T extends JsonElement, S> implements FutureCallback<T> {
	public static final String TAG = "AsaFutureCallback";

	protected String mUrl;
	private Gson mGson;

	public AsaFutureCallback(String url) {
		mUrl = url;
		mGson = new Gson();
	}

	protected S serialize(T result, Class<S> clzz) {
		try {
			return mGson.fromJson(result, clzz);
		} catch (JsonSyntaxException e) {
			onException(e);
		}
		return null;
	}

	@Override
	public void onCompleted(Exception e, T result) {
		if (e != null) {
			Log.e(TAG, "Exception occurred.", e);
			onException(e);
			return;
		}
		if (result == null) {
			Log.e(TAG, "Ion returned result was null.");
			onError();
			return;
		}
		try {
			onSuccess(result);
		} catch (Exception e1) {
			Log.e(TAG, "Exception occurred posting to onSuccess.", e1);
		}

	}

	public abstract void onError();

	public abstract void onException(Exception e);

	public abstract void onSuccess(T result);

}
