package me.exrates.model.dto;

import me.exrates.model.main.ExOrder;

/**
 * Created by Maks on 30.08.2017.
 */
public class CancelOrderEvent extends OrderEvent {

    private boolean byAdmin;

    public boolean isByAdmin() {
        return byAdmin;
    }

    /**
     * Create a new ApplicationEvent.
     *
     * @param source the object on which the event initially occurred (never {@code null})
     */
    public CancelOrderEvent(ExOrder source, boolean byAdmin) {
        super(source);
        this.byAdmin = byAdmin;
    }
}
