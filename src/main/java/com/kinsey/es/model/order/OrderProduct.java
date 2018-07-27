package com.kinsey.es.model.order;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Getter
@Setter
public class OrderProduct {
    @NotEmpty
    private String id;
    @NotNull
    private Integer number;
}