package com.asa.imhere.lib.model.responses;

import java.util.List;

import com.asa.imhere.lib.foursquare.FsMeta;
import com.asa.imhere.lib.foursquare.FsNotification;
import com.google.gson.annotations.SerializedName;

public class BaseResponseItem {

	@SerializedName("meta")
	private FsMeta meta;
	@SerializedName("notifications")
	private List<FsNotification> notifications;

	public FsMeta getMeta() {
		return meta;
	}

	public void setMeta(FsMeta meta) {
		this.meta = meta;
	}

	public List<FsNotification> getNotifications() {
		return notifications;
	}

	public void setNotifications(List<FsNotification> notifications) {
		this.notifications = notifications;
	}

}
