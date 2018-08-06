package com.kinsey.order.es.handler;

import com.kinsey.common.commands.ConfirmOrderCommand;
import com.kinsey.common.commands.CreateOrderCommand;
import com.kinsey.common.commands.RollbackOrderCommand;
import com.kinsey.common.domain.OrderProduct;
import com.kinsey.order.client.ProductClient;
import com.kinsey.order.client.model.ProductModel;
import com.kinsey.order.es.aggregates.OrderAggregate;
import com.kinsey.order.es.jpa.CustomEventSourcingRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.commandhandling.model.Aggregate;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
@AllArgsConstructor
public class OrderHandler {

    private CustomEventSourcingRepository<OrderAggregate> repository;

    private ProductClient productClient;

    @CommandHandler
    public void handle(CreateOrderCommand command) throws Exception {
        Map<String, OrderProduct> products = new HashMap<>();
        command.getProducts().forEach((productId, number) -> {
            log.debug("Loading product information with productId: {}", productId);
            ProductModel productModel = productClient.queryProductById(productId);
            products.put(productId, new OrderProduct(productId, productModel.getName(), productModel.getPrice(), number));
        });
        repository.newInstance(() -> new OrderAggregate(command.getId(), command.getUsername(), products));
    }

    @CommandHandler
    public void handle(RollbackOrderCommand command) {
        Aggregate<OrderAggregate> aggregate = repository.load(String.valueOf(command.getId()));
        aggregate.execute(OrderAggregate::delete);
    }

    @CommandHandler
    public void handle(ConfirmOrderCommand command) {
        Aggregate<OrderAggregate> aggregate = repository.load(String.valueOf(command.getId()));
        aggregate.execute(OrderAggregate::confirm);
    }

}