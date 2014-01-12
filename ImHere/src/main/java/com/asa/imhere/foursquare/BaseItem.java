package com.asa.imhere.foursquare;

public class BaseItem {

	public static final int TYPE_FAVORITE = 0;
	public static final int TYPE_UNFAVORITE = 1;

	protected String id;
	protected String name;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
