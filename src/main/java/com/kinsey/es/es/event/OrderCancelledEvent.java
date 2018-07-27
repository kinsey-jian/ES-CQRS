package com.kinsey.es.es.event;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class OrderCancelledEvent {
    private Long id;

    public OrderCancelledEvent(Long id) {
        this.id = id;
    }

}