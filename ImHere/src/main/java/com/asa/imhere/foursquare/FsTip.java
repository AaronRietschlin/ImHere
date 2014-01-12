package com.asa.imhere.foursquare;

import com.google.gson.annotations.SerializedName;

public class FsTip extends BaseItem {

	/**
	 * The actual tip
	 */
	@SerializedName("text")
	private String text;
	@SerializedName("createdAt")
	private long createdAt;
	@SerializedName("status")
	private String status;
	@SerializedName("photo")
	private FsPhoto photo;
	@SerializedName("url")
	private String url;

	// private FsVenue TODO
	// TODO - likes, todo, venue

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public long getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(long createdAt) {
		this.createdAt = createdAt;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public FsPhoto getPhoto() {
		return photo;
	}

	public void setPhoto(FsPhoto photo) {
		this.photo = photo;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}
}
