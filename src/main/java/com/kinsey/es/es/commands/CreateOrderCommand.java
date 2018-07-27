package com.kinsey.es.es.commands;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public class CreateOrderCommand {

    private Long orderId;
    private String username;
    private Map<String, Integer> products;

    public CreateOrderCommand(Long id, String username, Map<String, Integer> products) {
        this.orderId = id;
        this.username = username;
        this.products = products;
    }
}
