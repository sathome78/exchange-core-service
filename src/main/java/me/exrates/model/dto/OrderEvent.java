package me.exrates.model.dto;

import me.exrates.model.main.ExOrder;
import org.springframework.context.ApplicationEvent;

public class OrderEvent extends ApplicationEvent {

    public OrderEvent(ExOrder source) {
        super(source);
    }
}
