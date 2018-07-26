package com.kinsey.es.es.handler;

import com.kinsey.es.es.aggregates.OrderAggregate;
import com.kinsey.es.es.aggregates.ProductAggregate;
import com.kinsey.es.es.commands.ConfirmOrderCommand;
import com.kinsey.es.es.commands.CreateOrderCommand;
import com.kinsey.es.es.commands.RollbackOrderCommand;
import com.kinsey.es.common.domain.OrderProduct;
import com.kinsey.es.es.jpa.CustomEventSourcingRepository;
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

    private CustomEventSourcingRepository<ProductAggregate> productRepository;

    @CommandHandler
    public void handle(CreateOrderCommand command) throws Exception {
        Map<String, OrderProduct> products = new HashMap<>();
        command.getProducts().forEach((productId, number) -> {
            log.debug("Loading product information with productId: {}", productId);
            Aggregate<ProductAggregate> aggregate = productRepository.load(productId);
            products.put(productId,
                    new OrderProduct(productId,
                            aggregate.invoke(ProductAggregate::getName),
                            aggregate.invoke(ProductAggregate::getPrice),
                            number));
        });
        repository.newInstance(() -> new OrderAggregate(command.getOrderId(), command.getUsername(), products));
    }

    @CommandHandler
    public void handle(RollbackOrderCommand command) {
        Aggregate<OrderAggregate> aggregate = repository.load(String.valueOf(command.getOrderId()));
        aggregate.execute(OrderAggregate::delete);
    }

    @CommandHandler
    public void handle(ConfirmOrderCommand command) {
        Aggregate<OrderAggregate> aggregate = repository.load(String.valueOf(command.getId()));
        aggregate.execute(OrderAggregate::confirm);
    }

}