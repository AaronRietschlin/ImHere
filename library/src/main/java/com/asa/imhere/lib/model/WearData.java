package com.asa.imhere.lib.model;

import android.graphics.Bitmap;

/**
 * Created by Aaron on 7/13/2014.
 */
public class WearData {

    private String venueName;
    private String venueId;
    private Bitmap venueImage;

    public String getVenueName() {
        return venueName;
    }

    public void setVenueName(String venueName) {
        this.venueName = venueName;
    }

    public String getVenueId() {
        return venueId;
    }

    public void setVenueId(String venueId) {
        this.venueId = venueId;
    }

    public Bitmap getVenueImage() {
        return venueImage;
    }

    public void setVenueImage(Bitmap venueImage) {
        this.venueImage = venueImage;
    }
}
