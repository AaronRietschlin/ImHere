package com.asa.imhere.lib.otto;

import com.squareup.otto.Bus;
import com.squareup.otto.ThreadEnforcer;

/**
 * A convenience class for interacting with the singleton Bus.
 */
public class BusProvider {

	private static Bus sBus;
    private static Bus sBusThreaded;

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

    public static Bus getInstanceThreaded() {
        if (sBusThreaded == null) {
            sBusThreaded = new Bus(ThreadEnforcer.ANY);
        }
        return sBusThreaded;
    }

    public static Bus postThreaded(Object object) {
        getInstanceThreaded().post(object);
        return sBusThreaded;
    }

    public static Bus registerThreaded(Object object) {
        getInstanceThreaded().register(object);
        return sBusThreaded;
    }

    public static Bus unregisterThreaded(Object object) {
        getInstanceThreaded().unregister(object);
        return sBusThreaded;
    }

}
