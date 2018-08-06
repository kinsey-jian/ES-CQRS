package com.kinsey.common.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReserveCancelledEvent {

    private Long orderId;
    private Long productId;
    private int amount;
}