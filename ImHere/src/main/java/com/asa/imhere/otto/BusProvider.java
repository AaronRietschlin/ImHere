package com.asa.imhere.otto;

import com.squareup.otto.Bus;

/**
 * A convenience class for interacting with the singleton Bus.
 */
public class BusProvider {

	private static Bus sBus;

	public static Bus getInstance() {
		if (sBus == null) {
			sBus = new Bus();
		}
		return sBus;
	}

	public static Bus post(Object object) {
		getInstance().post(object);
		return sBus;
	}

	public static Bus register(Object object) {
		getInstance().register(object);
		return sBus;
	}

	public static Bus unregister(Object object) {
		getInstance().unregister(object);
		return sBus;
	}

}
