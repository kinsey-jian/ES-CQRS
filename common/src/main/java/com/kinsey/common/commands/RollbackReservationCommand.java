package com.kinsey.common.commands;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RollbackReservationCommand {

    private Long orderId;
    private String productId;
    private int number;

    public RollbackReservationCommand(Long orderId, String productId, int number) {
        this.orderId = orderId;
        this.productId = productId;
        this.number = number;
    }
}