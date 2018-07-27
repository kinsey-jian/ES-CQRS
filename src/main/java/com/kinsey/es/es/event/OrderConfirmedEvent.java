package com.kinsey.es.es.event;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderConfirmedEvent {
    private Long id;

    public OrderConfirmedEvent(Long id) {
        this.id = id;
    }
}