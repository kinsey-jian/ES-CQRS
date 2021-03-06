package com.kinsey.product.es.aggregates;

import com.kinsey.common.commands.CreateProductCommand;
import com.kinsey.common.event.ProductCreatedEvent;
import com.kinsey.common.event.ProductNotEnoughEvent;
import com.kinsey.common.event.ProductReservedEvent;
import com.kinsey.common.event.ReserveCancelledEvent;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.commandhandling.model.AggregateIdentifier;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.spring.stereotype.Aggregate;

import static org.axonframework.commandhandling.model.AggregateLifecycle.apply;

/**
 * Created by zj on 2018/7/22
 */
@Aggregate
@Getter
@Setter
@NoArgsConstructor
public class ProductAggregate {
    @AggregateIdentifier
    private Long id;
    private String name;
    private int stock;
    private long price;

    @CommandHandler
    public ProductAggregate(CreateProductCommand command) {
        apply(new ProductCreatedEvent(command.getId(), command.getName(), command.getPrice(), command.getStock()));
    }

    @EventSourcingHandler
    public void on(ProductCreatedEvent event) {
        this.id = event.getId();
        this.name = event.getName();
        this.price = event.getPrice();
        this.stock = event.getStock();
    }

    public void reserve(Long orderId, int amount) {
        if (stock >= amount) {
            apply(new ProductReservedEvent(orderId, id, amount));
        } else
            apply(new ProductNotEnoughEvent(orderId, id));
    }

    public void cancelReserve(Long orderId, int amout) {
        apply(new ReserveCancelledEvent(orderId, id, amout));
    }

    @EventSourcingHandler
    public void on(ProductReservedEvent event) {
        stock = stock - event.getAmount();
    }

    @EventSourcingHandler
    public void on(ReserveCancelledEvent event) {
        stock += event.getAmount();
    }
}
