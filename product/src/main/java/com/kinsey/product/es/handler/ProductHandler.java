package com.kinsey.product.es.handler;

import com.kinsey.common.commands.ReserveProductCommand;
import com.kinsey.common.commands.RollbackReservationCommand;
import com.kinsey.product.es.aggregates.ProductAggregate;
import com.kinsey.product.es.jpa.CustomEventSourcingRepository;
import lombok.AllArgsConstructor;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.commandhandling.model.Aggregate;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class ProductHandler {

    private CustomEventSourcingRepository<ProductAggregate> repository;

    @CommandHandler
    public void on(ReserveProductCommand command) {
        Aggregate<ProductAggregate> aggregate = repository.load(command.getProductId());
        aggregate.execute(aggregateRoot -> aggregateRoot.reserve(command.getOrderId(), command.getNumber()));
    }

    @CommandHandler
    public void on(RollbackReservationCommand command) {
        Aggregate<ProductAggregate> aggregate = repository.load(command.getProductId());
        aggregate.execute(aggregateRoot -> aggregateRoot.cancelReserve(command.getOrderId(), command.getNumber()));
    }
}