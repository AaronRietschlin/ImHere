package com.asa.imhere.foursquare;

import java.util.ArrayList;

import com.google.gson.annotations.SerializedName;

/**
 * An array of objects representing groups of recommendations. Each group
 * contains a type such as "recommended," a human-readable (eventually
 * localized) name such as "Recommended Places," and an array items of
 * recommendation objects, which have an ordered list of objects which contain
 * reasons and venue. The reasons are count and items, where each item has a
 * type such as "social" and a message about why this place may be of interest
 * to the acting user. The venues are compact venues that include stats and
 * hereNow data. We encourage clients to be robust against the introduction or
 * removal of group types by treating the groups as opaque objects to be
 * displayed or by placing unfamiliar groups in a catchall group.
 * <p>
 * See https://developer.foursquare.com/docs/venues/explore
 * </p>
 */
public class ExploreGroup {

	/**
	 * group contains a type such as "recommended,"
	 */
	@SerializedName("type")
	private String type;
	/**
	 * a human-readable (eventually localized) name such as
	 * "Recommended Places,"
	 */
	@SerializedName("name")
	private String name;
	@SerializedName("items")
	private ArrayList<Item> items;

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public ArrayList<Item> getItems() {
		return items;
	}

	public void setItems(ArrayList<Item> items) {
		this.items = items;
	}

	public static class Item {
		@SerializedName("reasons")
		private Reason reasons;
		@SerializedName("venue")
		private FsVenue venue;
		@SerializedName("referralId")
		private String referralId;

		// TODO - Add "phrases
		// TODO - Add tips

		public Reason getReasons() {
			return reasons;
		}

		public void setReasons(Reason reasons) {
			this.reasons = reasons;
		}

		public FsVenue getVenue() {
			return venue;
		}

		public void setVenue(FsVenue venue) {
			this.venue = venue;
		}

		public String getReferralId() {
			return referralId;
		}

		public void setReferralId(String referralId) {
			this.referralId = referralId;
		}
	}

	public static class Reason {

		@SerializedName("count")
		private int count;
		@SerializedName("items")
		private ArrayList<ReasonItem> items;

		public int getCount() {
			return count;
		}

		public void setCount(int count) {
			this.count = count;
		}

		public ArrayList<ReasonItem> getItems() {
			return items;
		}

		public void setItems(ArrayList<ReasonItem> items) {
			this.items = items;
		}

		public static class ReasonItem {
			@SerializedName("type")
			private String type;
			@SerializedName("message")
			private String message;

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
		}
	}

}
