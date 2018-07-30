package com.kinsey.es.es.commands;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
public class CreateOrderCommand extends AbstractCommand{

    private String username;
    private Map<String, Integer> products;

    public CreateOrderCommand(Long id, String username, Map<String, Integer> products) {
        super(id);
        this.username = username;
        this.products = products;
    }
}
