package com.kinsey.common.event;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductNotEnoughEvent {

    private Long orderId;
    private Long productId;

    public ProductNotEnoughEvent(Long orderId, Long productId) {
        this.orderId = orderId;
        this.productId = productId;
    }
}