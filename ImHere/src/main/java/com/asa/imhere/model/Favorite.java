package com.asa.imhere.model;

import com.asa.imhere.lib.foursquare.FsLocation;
import com.asa.imhere.lib.foursquare.FsVenue;
import com.asa.imhere.lib.model.IhFavorite;
import com.asa.imhere.lib.model.Nameable;
import com.google.android.gms.location.Geofence;

public class Favorite extends IhFavorite {

    private static final float DEFAULT_RADIUS = 50F;

    public static Favorite constructFromVenue(FsVenue venue) {
        Favorite fav = new Favorite();
        fav.setRemoteId(venue.getVenueId());
        fav.setName(venue.getName());
        fav.setAutoCheckInOn(false);
        FsLocation loc = venue.getLocation();
        if (loc != null) {
            fav.setLatitude(loc.getLat());
            fav.setLongitude(loc.getLng());
        }
        return fav;
    }

    public static IhFavorite constructFromNameable(Nameable venue) {
        IhFavorite fav = new IhFavorite();
        fav.setRemoteId(venue.getVenueId());
        fav.setName(venue.getName());
        return fav;
    }

    public Geofence constructGeofence() {
        Geofence.Builder builder = new Geofence.Builder();
        if(radius == 0){
            radius = DEFAULT_RADIUS;
        }
        builder.setCircularRegion(latitude, longitude, radius).setRequestId(venueId).setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT)
                .setExpirationDuration(Geofence.NEVER_EXPIRE);
        return builder.build();
    }
}
