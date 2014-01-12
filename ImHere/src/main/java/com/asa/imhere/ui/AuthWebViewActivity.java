package com.asa.imhere.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.asa.imhere.R;
import com.asa.imhere.foursquare.Foursquare;
import com.asa.imhere.utils.PreferenceUtils;

/**
 * The authorization Activity.
 */
public class AuthWebViewActivity extends AsaBaseActivity {

	@SuppressLint("SetJavaScriptEnabled")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_auth);

		WebView webView = (WebView) findViewById(R.id.webView);

		webView.getSettings().setJavaScriptEnabled(true);
		webView.setWebViewClient(new WebViewClient() {
			public void onPageStarted(WebView view, String url, Bitmap favicon) {
				String fragment = "#access_token=";
				int start = url.indexOf(fragment);
				if (start > -1) {
					String accessToken = url.substring(start + fragment.length(), url.length());
					PreferenceUtils.setAuthToken(getApplicationContext(), accessToken);
				}
				if (url.startsWith(Foursquare.CALLBACK_URL)) {
					setResult(Activity.RESULT_OK);
					finish();
				}
			}

			@Override
			public void onPageFinished(WebView view, String url) {
				url.length();
			}
		});
		webView.loadUrl(Foursquare.URL_AUTH);
	}
}
