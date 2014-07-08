package com.asa.imhere.otto;

import com.asa.imhere.lib.foursquare.FsVenue;

/**
 * Created by Aaron on 7/7/2014.
 */
public class VenueEnteredEvent  {

    private FsVenue venue;
    private boolean debug;

    public VenueEnteredEvent(FsVenue venue) {
        this.venue = venue;
    }

    public VenueEnteredEvent(FsVenue venue, boolean debug) {
        this.venue = venue;
        this.debug = debug;
    }

    public FsVenue getVenue() {
        return venue;
    }

    public boolean isDebug() {
        return debug;
    }
}
