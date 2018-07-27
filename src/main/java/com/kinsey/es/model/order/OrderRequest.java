package com.kinsey.es.model.order;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * Created by zj on 2018/7/26
 */
@Setter
@Getter
public class OrderRequest {
    @NotEmpty
    private String username;
    @NotNull
    private List<OrderProduct> orderProducts;
}


