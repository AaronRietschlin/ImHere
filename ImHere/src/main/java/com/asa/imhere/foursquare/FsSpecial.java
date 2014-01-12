package com.asa.imhere.foursquare;

import java.util.List;

import com.google.gson.annotations.SerializedName;

/**
 * Represents a special
 * https://developer.foursquare.com/docs/responses/special.html
 */
public class FsSpecial extends BaseItem {

	@SerializedName("type")
	private String type;
	@SerializedName("message")
	private String message;
	@SerializedName("description")
	private String description;
	@SerializedName("finePrint")
	private String finePrint;
	@SerializedName("unlocked")
	private boolean unlocked;
	@SerializedName("icon")
	private String icon;
	@SerializedName("title")
	private String title;
	@SerializedName("state")
	private String state;
	@SerializedName("progress")
	private int progress;
	@SerializedName("progressDescription")
	private String progressDescription;
	@SerializedName("detail")
	private String detail;
	@SerializedName("target")
	private int target;
	// TODO - Complete friendshere
	// TODO do "page"
	@SerializedName("provider")
	private String provider;
	@SerializedName("redemption")
	private String redemption;
	// TODO - do "likes"
	@SerializedName("like")
	private boolean like;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getFinePrint() {
		return finePrint;
	}

	public void setFinePrint(String finePrint) {
		this.finePrint = finePrint;
	}

	public boolean isUnlocked() {
		return unlocked;
	}

	public void setUnlocked(boolean unlocked) {
		this.unlocked = unlocked;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public int getProgress() {
		return progress;
	}

	public void setProgress(int progress) {
		this.progress = progress;
	}

	public String getProgressDescription() {
		return progressDescription;
	}

	public void setProgressDescription(String progressDescription) {
		this.progressDescription = progressDescription;
	}

	public String getDetail() {
		return detail;
	}

	public void setDetail(String detail) {
		this.detail = detail;
	}

	public int getTarget() {
		return target;
	}

	public void setTarget(int target) {
		this.target = target;
	}

	public String getProvider() {
		return provider;
	}

	public void setProvider(String provider) {
		this.provider = provider;
	}

	public String getRedemption() {
		return redemption;
	}

	public void setRedemption(String redemption) {
		this.redemption = redemption;
	}

	public boolean isLike() {
		return like;
	}

	public void setLike(boolean like) {
		this.like = like;
	}

	public static class Item {
		@SerializedName("specials")
		private List<FsSpecial> specials;
	}

}
