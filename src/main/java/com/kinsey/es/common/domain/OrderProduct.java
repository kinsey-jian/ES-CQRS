package com.kinsey.es.common.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Created by zj on 2018/7/22
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OrderProduct {
    private String id;
    private String name;
    private long price;
    private int amount;
    private boolean reserved;

    public OrderProduct(String id, String name, long price, int amount) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.amount = amount;
    }
}
