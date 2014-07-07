package com.asa.imhere.lib.foursquare;

public class FsException extends Exception {

    private FsMeta meta;

    public FsException(FsMeta meta) {
        this.meta = meta;
    }

    public FsMeta getMeta() {
        return meta;
    }

    public void setMeta(FsMeta meta) {
        this.meta = meta;
    }
}
