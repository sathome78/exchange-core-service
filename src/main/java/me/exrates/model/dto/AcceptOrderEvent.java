package me.exrates.model.dto;

import me.exrates.model.main.ExOrder;

public class AcceptOrderEvent extends OrderEvent {
    public AcceptOrderEvent(ExOrder source) {
        super(source);
    }
}
