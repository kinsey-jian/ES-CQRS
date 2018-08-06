package com.kinsey.order.es.aggregates;

import com.kinsey.common.domain.OrderProduct;
import com.kinsey.common.event.OrderCancelledEvent;
import com.kinsey.common.event.OrderConfirmedEvent;
import com.kinsey.common.event.OrderCreatedEvent;
import com.kinsey.order.enums.OrderStateEnum;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.axonframework.commandhandling.model.AggregateIdentifier;
import org.axonframework.commandhandling.model.AggregateMember;
import org.axonframework.eventhandling.EventHandler;
import org.axonframework.spring.stereotype.Aggregate;

import java.util.Map;

import static org.axonframework.commandhandling.model.AggregateLifecycle.apply;
import static org.axonframework.commandhandling.model.AggregateLifecycle.markDeleted;

/**
 * Created by zj on 2018/7/22
 */
@Aggregate
@Getter
@NoArgsConstructor
public class OrderAggregate {

    @AggregateIdentifier
    private Long id;

    private String username;

    private double payment;

    private OrderStateEnum state = OrderStateEnum.PROCESSING;

    @AggregateMember
    private Map<String, OrderProduct> products;

    public OrderAggregate(Long id, String username, Map<String, OrderProduct> products) {
        apply(new OrderCreatedEvent(id, username, products));
    }

    @EventHandler
    public void on(OrderCreatedEvent event) {
        this.id = event.getId();
        this.username = event.getUsername();
        this.products = event.getProducts();
        computePrice();
    }

    private void computePrice() {
        products.forEach((id, product) -> payment += product.getPrice() * product.getAmount());
    }

    public double getPayment() {
        return payment / 100;
    }

    public void addProduct(OrderProduct product) {
        this.products.put(product.getId(), product);
        payment += product.getPrice() * product.getAmount();
    }

    public void removeProduct(String productId) {
        OrderProduct product = this.products.remove(productId);
        payment = payment - product.getPrice() * product.getAmount();
    }

    public void delete() {
        apply(new OrderCancelledEvent(id));
    }

    public void confirm() {
        apply(new OrderConfirmedEvent(id));
    }

    @EventHandler
    public void on(OrderConfirmedEvent event) {
        this.state = OrderStateEnum.CONFIRMED;
    }

    @EventHandler
    public void on(OrderCancelledEvent event) {
        this.state = OrderStateEnum.DELETED;
        markDeleted();
    }
}
