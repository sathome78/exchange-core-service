package me.exrates.model.dto;

import me.exrates.model.main.ExOrder;

/**
 * Created by Maks on 30.08.2017.
 */
public class AcceptOrderEvent extends OrderEvent {
    /**
     * Create a new ApplicationEvent.
     *
     * @param source the object on which the event initially occurred (never {@code null})
     */
    public AcceptOrderEvent(ExOrder source) {
        super(source);
    }
}
