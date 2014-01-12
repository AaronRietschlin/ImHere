package com.asa.imhere.otto;

public class LocationSavedDataChanged {

	private String id;
	private boolean fromFavoritesScreen;

	public LocationSavedDataChanged(boolean fromFavorites) {
		this.fromFavoritesScreen = fromFavorites;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public boolean isFromFavoritesScreen() {
		return fromFavoritesScreen;
	}

	public void setFromFavoritesScreen(boolean fromFavoritesScreen) {
		this.fromFavoritesScreen = fromFavoritesScreen;
	}

}
