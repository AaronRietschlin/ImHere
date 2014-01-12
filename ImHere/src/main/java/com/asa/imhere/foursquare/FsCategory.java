package com.asa.imhere.foursquare;

import com.google.gson.annotations.SerializedName;

public class FsCategory extends BaseItem {

	@SerializedName("pluralName")
	private String pluralName;
	@SerializedName("shortName")
	private String shortName;
	@SerializedName("primary")
	private boolean primary;

	/**
	 * A unique identifier for this category.
	 */
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	/**
	 * Name of the category.
	 */
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Pluralized version of the category name.
	 */
	public String getPluralName() {
		return pluralName;
	}

	public void setPluralName(String pluralName) {
		this.pluralName = pluralName;
	}

	/**
	 * Shorter version of the category name.
	 */
	public String getShortName() {
		return shortName;
	}

	public void setShortName(String shortName) {
		this.shortName = shortName;
	}

	/**
	 * If this is the primary category for parent venue object.
	 */
	public boolean isPrimary() {
		return primary;
	}

	public void setPrimary(boolean primary) {
		this.primary = primary;
	}

	/**
	 * Pieces needed to construct category icons at various sizes. Combine
	 * prefix with a size (32, 44, 64, and 88 are available) and suffix, e.g.
	 * https://foursquare.com/img/categories/food/default_64.png. To get an
	 * image with a gray background, use bg_ before the size
	 */
	public static class Icon {
		public static final int SIZE_32 = 32;
		public static final int SIZE_44 = 44;
		public static final int SIZE_64 = 64;
		public static final int SIZE_88 = 88;

		public static final String BG = "bg_";

		@SerializedName("prefix")
		private String prefix;
		@SerializedName("suffix")
		private String suffix;

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

		public String getImageUrl(int size) {
			return prefix + size + suffix;
		}

		public String getImageWithBgUrl(int size) {
			return prefix + BG + size + suffix;
		}
	}

}
