package com.asa.imhere.otto;

import android.location.Location;

/**
 * An Otto event that provides the {@link Location} object.
 */
public class LocationProvidedEvent {

	private Location location;

	public LocationProvidedEvent(Location location) {
		this.location = location;
	}

	public Location getLocation() {
		return location;
	}

	public void setLocation(Location location) {
		this.location = location;
	}

}
