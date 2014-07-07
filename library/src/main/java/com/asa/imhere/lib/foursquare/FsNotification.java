package com.asa.imhere.lib.foursquare;

import com.google.gson.annotations.SerializedName;

public class FsNotification {

	// TODO - Add remaining fields
	
	@SerializedName("type")
	private String type;

	public static class Item {
		@SerializedName("unreadCount")
		private int unreadCount;

	}
}
