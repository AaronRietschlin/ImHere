package com.asa.imhere.lib.foursquare;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;

import com.asa.imhere.lib.FsPreferenceUtils;
import com.google.gson.JsonObject;

public class FsUtils {

    public static final String VIEW_VENUE_URL = "http://foursquare.com/venue/";

    /**
     * The versioning date that is required by the API.
     * https://developer.foursquare.com/overview/versioning
     */
    public static final String VERSIONING_DATE = "20140113";

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
    public static final String PATH_CHECKINS = "checkins/";

    public static final String PATH_ADD = "add/";

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

    public static String constructCheckinsUrl(Context context, String venueId, long lat, long lng,
                                              int llAcc, String broadcast, String shout) {
        String url = URL_API_V2 + PATH_CHECKINS + PATH_ADD;
        url += appendAccessParams(url, context);
        url += appendApiVersion();
        url += String.format("&" + FsJson.VENUE_ID + "=%s", venueId);
        if(lat > 0 && lng > 0){
            if(llAcc <= 0){
                llAcc = 1;
            }
            url += String.format("&" + FsJson.LAT_LNG + "=%s,%s&" + FsJson.LAT_LNG_ACCURACY + "=%s", String.valueOf(lat),
                    String.valueOf(lng), String.valueOf(llAcc));
        }
        if(!TextUtils.isEmpty(shout)){
            url += String.format("&" + FsJson.SHOUT + "=%s", shout);
        }
        if(!TextUtils.isEmpty(broadcast)){
            url += String.format("&" + FsJson.BROADCAST + "=%s", broadcast);
        }
        return url;
    }

    public static JsonObject buildCheckinsJsonParams(String venueId, long lat, long lng, int llAcc,
                                                     String broadcast, String shout) {
        JsonObject params = new JsonObject();
        params.addProperty(FsJson.VENUE_ID, venueId);
        if (lat != 0 && lng != 0) {
            // TODO - Make sure it's only 1 decimal place.
            String ll = lat + "," + lng;
            params.addProperty(FsJson.LAT_LNG, ll);
            if(llAcc <= 0){
                llAcc = 1;
            }
            params.addProperty(FsJson.LAT_LNG_ACCURACY, llAcc);
        }
        if(!TextUtils.isEmpty(shout)){
            params.addProperty(FsJson.SHOUT, shout);
        }
        if(!TextUtils.isEmpty(broadcast)){
            params.addProperty(FsJson.BROADCAST, broadcast);
        }
        return params;
    }

    /**
     * Appends the client_id and "". You do not HAVE to be authenticated for
     * some calls. As such, you need to append some things to the URL. Example:
     * https
     * ://api.foursquare.com/v2/venues/search?ll=40.7,-74&client_id=CLIENT_ID
     * &client_secret=CLIENT_SECRET&v=YYYYMMDD
     *
     * @return
     */
    public static String appendAccessParamsForNoAuth() {
        return "&client_id=" + CLIENT_ID + "&client_secret=IROUQLT0RBTZ4ETREKKMUCNWAK1XT3A5LJ4ZDSVZ3S2GYHG3";
    }

    /**
     * Appends the API version. Each request requires "v=YYYYMMDD" at the end of
     * it.
     *
     * @return
     */
    public static String appendApiVersion() {
        return "&v=" + VERSIONING_DATE;
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
        String token = FsPreferenceUtils.getAuthToken(context);
        append += "oauth_token=" + token;
        return append;
    }

    public static String buildViewVenueUrl(String venueId) {
        return VIEW_VENUE_URL + venueId;
    }

    public static Intent buildViewVenueIntent(String venueId) {
        return new Intent(Intent.ACTION_VIEW, Uri.parse(buildViewVenueUrl(venueId)));
    }
}
