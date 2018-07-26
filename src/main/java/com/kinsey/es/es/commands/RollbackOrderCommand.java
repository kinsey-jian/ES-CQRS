package com.kinsey.es.es.commands;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RollbackOrderCommand {
    private Long orderId;

    public RollbackOrderCommand(Long orderId) {
        this.orderId = orderId;
    }
}