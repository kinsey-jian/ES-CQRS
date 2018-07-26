package com.kinsey.es.es.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ProductReservedEvent {

    private Long orderId;
    private Long productId;
    private int amount;
}