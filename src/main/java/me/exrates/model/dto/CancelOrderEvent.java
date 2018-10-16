package me.exrates.model.dto;

import me.exrates.model.main.ExOrder;

public class CancelOrderEvent extends OrderEvent {

    private boolean byAdmin;

    public boolean isByAdmin() {
        return byAdmin;
    }

    public CancelOrderEvent(ExOrder source, boolean byAdmin) {
        super(source);
        this.byAdmin = byAdmin;
    }
}
