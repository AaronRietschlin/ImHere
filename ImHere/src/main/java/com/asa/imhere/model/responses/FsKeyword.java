package com.asa.imhere.model.responses;

import java.util.List;

import com.google.gson.annotations.SerializedName;

/**
 * This corresponds with teh "keywords" data in teh "explore" API.
 */
public class FsKeyword {

	@SerializedName("count")
	private int count;
	@SerializedName("items")
	private List<Item> items;

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public List<Item> getItems() {
		return items;
	}

	public void setItems(List<Item> items) {
		this.items = items;
	}

	public static class Item {
		@SerializedName("displayName")
		private String displayName;
		@SerializedName("keyword")
		private String keyword;

		public String getDisplayName() {
			return displayName;
		}

		public void setDisplayName(String displayName) {
			this.displayName = displayName;
		}

		public String getKeyword() {
			return keyword;
		}

		public void setKeyword(String keyword) {
			this.keyword = keyword;
		}
	}

}
