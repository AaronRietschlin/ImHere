package com.asa.imhere.model;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.asa.imhere.foursquare.FsLocation;
import com.asa.imhere.foursquare.FsVenue;
import com.google.android.gms.location.Geofence;
import com.google.gson.annotations.SerializedName;

public class Favorite  implements Nameable {
	public static final String TABLE = "favorite_venues";

    private Long _id;
	@SerializedName("venueId")
	private String venueId;
	@SerializedName("name")
	private String name;
	private boolean autoCheckInOn;
	private double latitude;
	private double longitude;
	private float radius;
	private int duration;

    public Long get_id() {
        return _id;
    }

    public void set_id(Long _id) {
        this._id = _id;
    }

    public String getRemoteId() {
		return venueId;
	}

	public void setRemoteId(String id) {
		this.venueId = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isAutoCheckInOn() {
		return autoCheckInOn;
	}

	public void setAutoCheckInOn(boolean autoCheckInOn) {
		this.autoCheckInOn = autoCheckInOn;
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public float getRadius() {
		return radius;
	}

	public void setRadius(float radius) {
		this.radius = radius;
	}

	public int getDuration() {
		return duration;
	}

	public void setDuration(int duration) {
		this.duration = duration;
	}

	public static Favorite constructFromVenue(FsVenue venue, boolean save) {
		Favorite fav = new Favorite();
		fav.setRemoteId(venue.getVenueId());
		fav.setName(venue.getName());
		fav.setAutoCheckInOn(false);
		FsLocation loc = venue.getLocation();
		if (loc != null) {
			fav.setLatitude(loc.getLat());
			fav.setLongitude(loc.getLng());
		}
		if (save) {
			fav.save();
		}
		return fav;
	}

	public static Favorite constructFromNameable(Nameable venue, boolean save) {
		Favorite fav = new Favorite();
		fav.setRemoteId(venue.getVenueId());
		fav.setName(venue.getName());
		if (save) {
			fav.save();
		}
		return fav;
	}

	public Geofence constructGeofence() {
		Geofence.Builder builder = new Geofence.Builder();
		builder.setCircularRegion(latitude, longitude, radius).setRequestId(venueId).setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT)
				.setExpirationDuration(Geofence.NEVER_EXPIRE);
		return builder.build();
	}

	public static class Columns {
		public static final String ID = "Id";
		public static final String REMOTE_ID = "remote_id";
		public static final String NAME = "name";
		public static final String AUTO_CHECKIN_ON = "auto_checkin_on";
		public static final String LATITUDE = "latitude";
		public static final String LONGITUDE = "longitude";
		public static final String RADIUS = "radius";
		public static final String DURATION = "duration";
	}

	@Override
	public String getVenueId() {
		return getRemoteId();
	}
}
