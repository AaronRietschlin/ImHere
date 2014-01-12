package com.asa.imhere.foursquare;

import android.content.Context;

import com.asa.imhere.IHApplication;
import com.asa.imhere.utils.PreferenceUtils;

public class Foursquare {

	public static final String CALLBACK_URL = "http://localhost:8888/";
	public static final String CLIENT_ID = "LBAHT5C55FWFDIN1QIST12WGY5UFK0K0W12QFNPGNGQP1BDS";

	public static final String URL_AUTH = "https://foursquare.com/oauth2/authenticate" + "?client_id=" + CLIENT_ID + "&response_type=token" + "&redirect_uri=" + CALLBACK_URL;

	public static final String URL_API_V2 = "https://api.foursquare.com/v2/";

	/**
	 * The attribution URL. Attribution to Foursquare is required. See
	 * https://developer.foursquare.com/overview/attribution.
	 */
	public static final String ATTRIBUTION_URL = "http://m.foursquare.com/venue/43a52546f964a520532c1fe3?ref=" + CLIENT_ID;

	public static final String PATH_VENUES = "venues/";
	public static final String PATH_EXPLORE = "explore/";
	public static final String PATH_SEARCH = "search/";

	public static String constructExploreUrl(String near, double lat, double lon, Context context) {
		String url = URL_API_V2 + PATH_VENUES + PATH_EXPLORE;
		boolean nearAppended = false;
		// Append the near
		if (near != null) {
			url += "?near=" + near;
			nearAppended = true;
		}
		// Append the lat and lon, only if the near is not there
		if (!nearAppended) {
			url += "?ll=" + lat + "," + lon;
		}
		url += appendAccessParams(url, context);
		url += appendApiVersion();
		return url;
	}

	public static String constructSearchUrl(String query, double lat, double lon, Context context) {
		String url = URL_API_V2 + PATH_VENUES + PATH_SEARCH;
		url += "?query=" + query;
		// Append the lat and lon
		url += "&ll=" + lat + "," + lon;
		url += appendAccessParams(url, context);
		url += appendApiVersion();
		return url;
	}

	public static String constructVenueUrl(String venueId, Context context) {
		String url = URL_API_V2 + PATH_VENUES + venueId;
		url += appendAccessParams(url, context);
		url += appendApiVersion();
		return url;
	}

	/**
	 * Appends the client_id and "". You do not HAVE to be authenticated for
	 * some calls. As such, you need to append some things to the URL. Example:
	 * https
	 * ://api.foursquare.com/v2/venues/search?ll=40.7,-74&client_id=CLIENT_ID
	 * &client_secret=CLIENT_SECRET&v=YYYYMMDD
	 * 
	 * @param url
	 * @return
	 */
	public static String appendAccessParamsForNoAuth() {
		return "&client_id=" + CLIENT_ID + "&client_secret=IROUQLT0RBTZ4ETREKKMUCNWAK1XT3A5LJ4ZDSVZ3S2GYHG3";
	}

	/**
	 * Appends the API version. Each request requires "v=YYYYMMDD" at the end of
	 * it.
	 * 
	 * @param url
	 * @return
	 */
	public static String appendApiVersion() {
		return "&v=" + IHApplication.VERSIONING_DATE;
	}

	public static String appendAccessParams(String url, Context context) {
		String append = "";
		boolean isQuestionPresent = url.contains("?");
		if (!isQuestionPresent) {
			append = "?";
		}
		if (context == null) {
			return appendAccessParamsForNoAuth();
		}
		if (isQuestionPresent) {
			append += "&";
		}
		String token = PreferenceUtils.getAuthToken(context);
		append += "oauth_token=" + token;
		return append;
	}
}
