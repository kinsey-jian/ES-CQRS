package com.kinsey.es.es.commands;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateProductCommand {

    private Long id;
    private String name;
    private long price;
    private int stock;

    public CreateProductCommand(Long id, String name, long price, int stock) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.stock = stock;
    }
}