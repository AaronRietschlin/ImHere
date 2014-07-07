package com.asa.imhere.lib.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.asa.imhere.lib.foursquare.FsLocation;
import com.asa.imhere.lib.foursquare.FsVenue;
import com.google.gson.annotations.SerializedName;

public class IhFavorite implements Nameable, Parcelable {
    public static final String TABLE = "favorite_venues";

    protected Long _id;
    @SerializedName("venueId")
    protected String venueId;
    @SerializedName("name")
    protected String name;
    protected boolean autoCheckInOn;
    protected double latitude;
    protected double longitude;
    protected float radius;
    protected int duration;

    public IhFavorite(){}

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

    protected IhFavorite(Parcel in) {
        this._id = (Long) in.readValue(Long.class.getClassLoader());
        this.venueId = in.readString();
        this.name = in.readString();
        this.autoCheckInOn = in.readByte() != 0;
        this.latitude = in.readDouble();
        this.longitude = in.readDouble();
        this.radius = in.readFloat();
        this.duration = in.readInt();
    }

    public static Creator<IhFavorite> CREATOR = new Creator<IhFavorite>() {
        public IhFavorite createFromParcel(Parcel source) {
            return new IhFavorite(source);
        }

        public IhFavorite[] newArray(int size) {
            return new IhFavorite[size];
        }
    };
}
