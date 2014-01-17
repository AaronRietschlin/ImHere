package com.asa.imhere.otto;

import com.asa.imhere.foursquare.FsVenue;

import java.util.ArrayList;

public class ExploreVenuesRetreived {

    private ArrayList<FsVenue> venues;
    private boolean paginated;

    public ExploreVenuesRetreived(ArrayList<FsVenue> venues) {
        this.venues = venues;
    }

    public ExploreVenuesRetreived(boolean paginated, ArrayList<FsVenue> venues) {
        this.paginated = paginated;
        this.venues = venues;
    }

    public ArrayList<FsVenue> getVenues() {
        return venues;
    }

    public void setVenues(ArrayList<FsVenue> venues) {
        this.venues = venues;
    }

    public boolean isPaginated() {
        return paginated;
    }

    public void setPaginated(boolean paginated) {
        this.paginated = paginated;
    }
}
