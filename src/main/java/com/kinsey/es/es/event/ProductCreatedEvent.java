package com.kinsey.es.es.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Created by zj on 2018/7/22
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductCreatedEvent {

    private Long id;

    private String name;

    private long price;

    private int stock;
}
