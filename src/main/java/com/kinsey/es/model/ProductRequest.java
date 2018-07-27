package com.kinsey.es.model;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;

/**
 * Created by zj on 2018/7/26
 */
@Getter
@Setter
public class ProductRequest {
    @NotEmpty
    private String name;
    private long price;
    private int stock;
}
