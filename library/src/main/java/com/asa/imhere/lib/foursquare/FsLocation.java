package com.asa.imhere.lib.foursquare;

import com.google.gson.annotations.SerializedName;

public class FsLocation extends BaseItem {

	@SerializedName("address")
	private String address;
	@SerializedName("crossStreet")
	private String crossStreet;
	@SerializedName("lat")
	private double lat;
	@SerializedName("lng")
	private double lng;
	@SerializedName("distance")
	private int distance;
	@SerializedName("postalCode")
	private String postalCode;
	@SerializedName("city")
	private String city;
	@SerializedName("state")
	private String state;
	@SerializedName("country")
	private String country;
	@SerializedName("cc")
	private String cc;
	@SerializedName("isFuzzed")
	private boolean isFuzzed;

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getCrossStreet() {
		return crossStreet;
	}

	public void setCrossStreet(String crossStreet) {
		this.crossStreet = crossStreet;
	}

	public double getLat() {
		return lat;
	}

	public void setLat(double lat) {
		this.lat = lat;
	}

	public double getLng() {
		return lng;
	}

	public void setLng(double lng) {
		this.lng = lng;
	}

	public int getDistance() {
		return distance;
	}

	public void setDistance(int distance) {
		this.distance = distance;
	}

	public String getPostalCode() {
		return postalCode;
	}

	public void setPostalCode(String postalCode) {
		this.postalCode = postalCode;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	/**
	 * Returns the full address in "City, State Zip" format.
	 * 
	 * @return
	 */
	public String getFullAddress() {
		return city + ", " + state + " " + postalCode;
	}

	public String getCc() {
		return cc;
	}

	public void setCc(String cc) {
		this.cc = cc;
	}

	public void setFuzzed(boolean isFuzzed) {
		this.isFuzzed = isFuzzed;
	}

	/**
	 * Some venues have their locations intentionally hidden for privacy reasons
	 * (such as private residences). If this is the case, the parameter isFuzzed
	 * will be set to true, and the lat/lng parameters will have reduced
	 * precision.
	 * 
	 * @return
	 */
	public boolean isFuzzed() {
		return isFuzzed;
	}

}
