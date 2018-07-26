package com.kinsey.es.controller;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

/**
 * Created by zj on 2018/7/25
 */
@Getter
@Setter
public class OrderRequest {
    private String username;
    private Map<String, Integer> products;
}
