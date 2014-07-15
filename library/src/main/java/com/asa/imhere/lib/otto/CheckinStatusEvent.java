package com.asa.imhere.lib.otto;

import com.asa.imhere.lib.wear.WearUtils;

/**
 * Created by Aaron on 7/14/2014.
 */
public class CheckinStatusEvent {

    private int result;

    public CheckinStatusEvent(int result) {
        this.result = result;
    }

    public CheckinStatusEvent(boolean success) {
        result = success ? WearUtils.CHECKIN_STATUS_SUCCESS : WearUtils.CHECKIN_STATUS_FAILURE;
    }

    public int getResult() {
        return result;
    }

    public boolean isSuccess(){
        return result == WearUtils.CHECKIN_STATUS_SUCCESS;
    }
}
