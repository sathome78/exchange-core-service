package me.exrates.model;


import me.exrates.model.dto.OrderEvent;
import me.exrates.model.main.ExOrder;

public class CreateOrderEvent extends OrderEvent {
    public CreateOrderEvent(ExOrder source) {
        super(source);
    }
}
