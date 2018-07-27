package com.kinsey.es.query;

import com.kinsey.es.enums.OrderStateEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Map;


@Document
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderEntry {
    @Id
    private String id;
    private String username;
    private double payment;
    private OrderStateEnum status = OrderStateEnum.RESERVING;
    private Map<String, OrderProductEntry> products;

    public OrderEntry(String id, String username, Map<String, OrderProductEntry> products) {
        this.id = id;
        this.username = username;
        this.products = products;
    }
}