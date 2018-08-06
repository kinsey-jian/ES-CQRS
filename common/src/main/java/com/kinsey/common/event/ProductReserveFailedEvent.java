package com.kinsey.common.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ProductReserveFailedEvent {
    private Long orderId;
    private String productId;
}