package com.asa.imhere.lib.foursquare;

public class FsUser extends BaseItem {

	private String firstName;
	private String lastName;
	private FsPhoto photo;
	private String gender;
	private String homeCity;
	private String bio;
	private String fsUrl;

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public FsPhoto getPhoto() {
		return photo;
	}

	public void setPhoto(FsPhoto photo) {
		this.photo = photo;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public String getHomeCity() {
		return homeCity;
	}

	public void setHomeCity(String homeCity) {
		this.homeCity = homeCity;
	}

	public String getBio() {
		return bio;
	}

	public void setBio(String bio) {
		this.bio = bio;
	}

	public String getFsUrl() {
		return fsUrl;
	}

	public void setFsUrl(String fsUrl) {
		this.fsUrl = fsUrl;
	}

}
