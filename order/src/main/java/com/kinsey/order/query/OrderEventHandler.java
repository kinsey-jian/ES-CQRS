package com.kinsey.order.query;

import com.kinsey.common.event.OrderCancelledEvent;
import com.kinsey.common.event.OrderConfirmedEvent;
import com.kinsey.common.event.OrderCreatedEvent;
import com.kinsey.order.enums.OrderStateEnum;
import lombok.AllArgsConstructor;
import org.axonframework.eventhandling.EventHandler;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Component
@AllArgsConstructor
public class OrderEventHandler {

    private OrderEntryRepository repository;

    @EventHandler
    public void on(OrderCreatedEvent event) {
        Map<String, OrderProductEntry> map = new HashMap<>();
        event.getProducts().forEach((id, product) -> map.put(id,
                new OrderProductEntry(product.getId(), product.getName(), product.getPrice(), product.getAmount())));
        OrderEntry order = new OrderEntry(event.getId().toString(), event.getUsername(), map);
        repository.save(order);
    }

    @EventHandler
    public void on(OrderConfirmedEvent event) {
        Optional<OrderEntry> order = repository.findById(String.valueOf(event.getId()));
        order.ifPresent(a -> {
            a.setStatus(OrderStateEnum.CONFIRMED);
            repository.save(a);
        });
    }

    @EventHandler
    public void on(OrderCancelledEvent event) {
        Optional<OrderEntry> order = repository.findById(String.valueOf(event.getId()));
        order.ifPresent(a -> {
            a.setStatus(OrderStateEnum.CANCELLED);
            repository.save(a);
        });
    }
}