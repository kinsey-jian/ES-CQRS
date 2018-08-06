package com.kinsey.common.commands;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateProductCommand extends AbstractProductCommand {

    private String name;
    private long price;
    private int stock;

    public CreateProductCommand(Long id, String name, long price, int stock) {
        super(id);
        this.name = name;
        this.price = price;
        this.stock = stock;
    }
}