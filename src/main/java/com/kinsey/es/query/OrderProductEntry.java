package com.kinsey.es.query;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderProductEntry {

    @Id
    private Long jpaId;
    private String id;
    private String name;
    private long price;
    private int amount;

    public OrderProductEntry(String id, String name, long price, int amount) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.amount = amount;
    }
}
