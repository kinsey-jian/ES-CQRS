package com.kinsey.es.query;

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
    private String status = "reserving";
    private Map<String, OrderProductEntry> products;

    public OrderEntry(String id, String username, Map<String, OrderProductEntry> products) {
        this.id = id;
        this.username = username;
        this.products = products;
    }
}