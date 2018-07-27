package com.kinsey.es.es.saga;

import com.kinsey.es.es.commands.ConfirmOrderCommand;
import com.kinsey.es.es.commands.ReserveProductCommand;
import com.kinsey.es.es.commands.RollbackOrderCommand;
import com.kinsey.es.es.commands.RollbackReservationCommand;
import com.kinsey.es.common.domain.OrderProduct;
import com.kinsey.es.es.event.*;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.eventhandling.saga.EndSaga;
import org.axonframework.eventhandling.saga.SagaEventHandler;
import org.axonframework.eventhandling.saga.StartSaga;
import org.axonframework.spring.stereotype.Saga;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@Saga
@Slf4j
public class OrderSaga {

    private Long orderIdentifier;
    private Map<String, OrderProduct> toReserve;
    private Map<String, OrderProduct> toRollback;
    private AtomicInteger toReserveNumber;
    private boolean needRollback;

    @Autowired
    private transient CommandGateway commandGateway;

    @StartSaga
    @SagaEventHandler(associationProperty = "orderId")
    public void handle(OrderCreatedEvent event) {
        this.orderIdentifier = event.getOrderId();
        this.toReserve = event.getProducts();
        toRollback = new HashMap<>();
        toReserveNumber = new AtomicInteger(toReserve.size());
        event.getProducts().forEach((id, product) -> {
            ReserveProductCommand command = new ReserveProductCommand(orderIdentifier, id, product.getAmount());
            commandGateway.send(command);
        });
    }

    @SagaEventHandler(associationProperty = "orderId")
    public void handle(ProductNotEnoughEvent event) {
        toReserveNumber.decrementAndGet();
        needRollback = true;
        if (toReserveNumber.get() == 0) tryFinish();
    }

    private void tryFinish() {
        if (needRollback) {
            toReserve.forEach((id, product) -> {
                if (!product.isReserved())
                    return;
                toRollback.put(id, product);
                commandGateway.send(new RollbackReservationCommand(orderIdentifier, id, product.getAmount()));
            });
            if (toRollback.isEmpty())
                commandGateway.send(new RollbackOrderCommand(orderIdentifier));
            return;
        }
        commandGateway.send(new ConfirmOrderCommand(orderIdentifier));
    }

    @SagaEventHandler(associationProperty = "orderId")
    public void handle(ReserveCancelledEvent event) {
        toRollback.remove(String.valueOf(event.getProductId()));
        if (toRollback.isEmpty())
            commandGateway.send(new RollbackOrderCommand(event.getOrderId()));
    }

    @SagaEventHandler(associationProperty = "id", keyName = "orderId")
    @EndSaga
    public void handle(OrderCancelledEvent event) {
        log.info("Order {} is cancelled", event.getId());
    }

    @SagaEventHandler(associationProperty = "orderId")
    public void handle(ProductReservedEvent event) {
        OrderProduct reservedProduct = toReserve.get(String.valueOf(event.getProductId()));
        reservedProduct.setReserved(true);
        toReserveNumber.decrementAndGet();
        //Q: will a concurrent issue raise?
        if (toReserveNumber.get() == 0)
            tryFinish();
    }

    @SagaEventHandler(associationProperty = "id", keyName = "orderId")
    @EndSaga
    public void handle(OrderConfirmedEvent event) {
        log.info("Order {} is confirmed", event.getId());
    }

}
