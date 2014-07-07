package com.asa.imhere.otto;

import com.asa.imhere.lib.foursquare.FsVenue;

public class VenueRetreivedEvent {

    private FsVenue venue;

    public VenueRetreivedEvent(FsVenue venue) {
        this.venue = venue;
    }

    public FsVenue getVenue() {
        return venue;
    }

    public void setVenue(FsVenue venue) {
        this.venue = venue;
    }
}
