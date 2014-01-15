package com.asa.imhere.otto;

public class FavoriteDeletedEvent {

    private boolean deleted;

    public FavoriteDeletedEvent(boolean deleted) {
        this.deleted = deleted;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }
}