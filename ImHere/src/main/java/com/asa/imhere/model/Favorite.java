package com.asa.imhere.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.asa.imhere.lib.foursquare.FsLocation;
import com.asa.imhere.lib.foursquare.FsVenue;
import com.asa.imhere.lib.model.Nameable;
import com.google.android.gms.location.Geofence;
import com.google.gson.annotations.SerializedName;

public class Favorite implements Nameable, Parcelable {
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

    public Favorite(){}

    public Long getId() {
        return _id;
    }

    public void setId(Long _id) {
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

    public static Favorite constructFromNameable(Nameable venue) {
        Favorite fav = new Favorite();
        fav.setRemoteId(venue.getVenueId());
        fav.setName(venue.getName());
        return fav;
    }

    public Geofence constructGeofence() {
        Geofence.Builder builder = new Geofence.Builder();
        builder.setCircularRegion(latitude, longitude, radius).setRequestId(venueId).setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT)
                .setExpirationDuration(Geofence.NEVER_EXPIRE);
        return builder.build();
    }

    @Override
    public String getVenueId() {
        return getRemoteId();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(this._id);
        dest.writeString(this.venueId);
        dest.writeString(this.name);
        dest.writeByte(autoCheckInOn ? (byte) 1 : (byte) 0);
        dest.writeDouble(this.latitude);
        dest.writeDouble(this.longitude);
        dest.writeFloat(this.radius);
        dest.writeInt(this.duration);
    }

    private Favorite(Parcel in) {
        this._id = (Long) in.readValue(Long.class.getClassLoader());
        this.venueId = in.readString();
        this.name = in.readString();
        this.autoCheckInOn = in.readByte() != 0;
        this.latitude = in.readDouble();
        this.longitude = in.readDouble();
        this.radius = in.readFloat();
        this.duration = in.readInt();
    }

    public static Parcelable.Creator<Favorite> CREATOR = new Parcelable.Creator<Favorite>() {
        public Favorite createFromParcel(Parcel source) {
            return new Favorite(source);
        }

        public Favorite[] newArray(int size) {
            return new Favorite[size];
        }
    };
}
