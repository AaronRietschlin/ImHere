package com.asa.imhere.foursquare;

import java.util.List;

import com.google.gson.annotations.SerializedName;

/**
 * Represents a photo object in Foursquare. See:
 * https://developer.foursquare.com/docs/responses/photo
 */
public class FsPhoto extends BaseItem {

	@SerializedName("createdAt")
	private long createdAtInSec;
	private String prefix;
	private String suffix;
	private Source source;
	private FsUser user;
	private FsVenue venue;
	private int width;
	private int height;
	private String visibility;
	private String canonicalUrl;
	private List<FsCategory> categories;
	private boolean verified;

	// TODO - do Tip object

	/**
	 * Convenience method for getting the fully qualified URL.
	 */
	public String getFullUrl(int minHeight, int minWidth) {
		String size = "";
		if (width > 0 && height > 0 && width >= minWidth && height > minHeight) {
			size += height + "x" + width;
		} else {
			size += minHeight + "x" + minWidth;
		}
		return prefix + size + suffix;
	}

	public long getCreatedAtInSec() {
		return createdAtInSec;
	}

	public void setCreatedAtInSec(long createdAtInSec) {
		this.createdAtInSec = createdAtInSec;
	}

	public String getPrefix() {
		return prefix;
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	public String getSuffix() {
		return suffix;
	}

	public void setSuffix(String suffix) {
		this.suffix = suffix;
	}

	public Source getSource() {
		return source;
	}

	public void setSource(Source source) {
		this.source = source;
	}

	public FsUser getUser() {
		return user;
	}

	public void setUser(FsUser user) {
		this.user = user;
	}

	public FsVenue getVenue() {
		return venue;
	}

	public void setVenue(FsVenue venue) {
		this.venue = venue;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public String getVisibility() {
		return visibility;
	}

	public void setVisibility(String visibility) {
		this.visibility = visibility;
	}

	public String getCanonicalUrl() {
		return canonicalUrl;
	}

	public void setCanonicalUrl(String canonicalUrl) {
		this.canonicalUrl = canonicalUrl;
	}

	public List<FsCategory> getCategories() {
		return categories;
	}

	public void setCategories(List<FsCategory> categories) {
		this.categories = categories;
	}

	public boolean isVerified() {
		return verified;
	}

	public void setVerified(boolean verified) {
		this.verified = verified;
	}

	/**
	 * The name and url of the application that created this photo.
	 */
	public static class Source {
		private String name;
		private String url;
	}

}
