package com.asa.imhere.foursquare;

public class FsUtils {

    public static final String VIEW_VENUE_URL = "http://foursquare.com/venue/";

    public static String buildViewVenueUrl(String venueId) {
        return VIEW_VENUE_URL + venueId;
    }

}
