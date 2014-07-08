package com.asa.imhere.utils;

import com.asa.imhere.lib.foursquare.FsVenue;
import com.asa.imhere.lib.otto.BusProvider;
import com.asa.imhere.otto.VenueEnteredEvent;
import com.google.gson.Gson;

import timber.log.Timber;

/**
 * A debug class that is only to be used for testing. This will provide the test venue used based
 * off of hardcoded JSON
 * <p/>
 * Created by Aaron on 7/7/2014.
 */
public class DebugVenueProvider {
    private static final String JSON = "{\"id\":\"53bb43b5498ed8168775491a\",\"name\":\"51 King-TEST Ave\",\"contact\":{},\"location\":{\"address\":\"51 King Ave\",\"crossStreet\":\"Dennison Ave\",\"lat\":39.98999378357614,\"lng\":-83.007652759552,\"distance\":4,\"postalCode\":\"43201\",\"cc\":\"US\",\"city\":\"Columbus\",\"state\":\"OH\",\"country\":\"United States\",\"formattedAddress\":[\"51 King Ave (Dennison Ave)\",\"Columbus, OH 43201\"]},\"categories\":[{\"id\":\"4d954b06a243a5684965b473\",\"name\":\"Residential Building (Apartment \\/ Condo)\",\"pluralName\":\"Residential Buildings (Apartments \\/ Condos)\",\"shortName\":\"Residential\",\"icon\":{\"prefix\":\"https:\\/\\/ss1.4sqi.net\\/img\\/categories_v2\\/building\\/apartment_\",\"suffix\":\".png\"},\"primary\":true}],\"verified\":false,\"stats\":{\"checkinsCount\":0,\"usersCount\":0,\"tipCount\":0},\"specials\":{\"count\":0,\"items\":[]},\"hereNow\":{\"count\":0,\"summary\":\"0 people here\",\"groups\":[]},\"referralId\":\"v-1404782267\"}";

    /**
     * Builds the debug Venue for testing purposes.
     */
    public static FsVenue buildDebugJson() {
        FsVenue venue = new Gson().fromJson(JSON, FsVenue.class);
        Timber.d("Venue created: %s", venue.toString());
        return venue;
    }

    /**
     * Fires a test event with the debug venue created from {@link #buildDebugJson()}.
     */
    public static void sendDebugVenueEnteredEvent() {
        BusProvider.post(new VenueEnteredEvent(buildDebugJson(), true));
    }
}
