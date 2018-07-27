package com.kinsey.es.es.saga;

import com.kinsey.es.es.commands.ConfirmOrderCommand;
import com.kinsey.es.es.commands.ReserveProductCommand;
import com.kinsey.es.es.commands.RollbackOrderCommand;
import com.kinsey.es.es.commands.RollbackReservationCommand;
import com.kinsey.es.common.domain.OrderId;
import com.kinsey.es.common.domain.OrderProduct;
import com.kinsey.es.es.event.*;
import com.kinsey.es.common.exception.OrderCreateFailedException;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.eventhandling.saga.EndSaga;
import org.axonframework.eventhandling.saga.SagaEventHandler;
import org.axonframework.eventhandling.saga.StartSaga;
import org.axonframework.spring.stereotype.Saga;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.Map;

@Saga
@Slf4j
public class OrderSaga {

    private Long orderIdentifier;
    private Map<String, OrderProduct> toReserve;
    private Map<String, OrderProduct> toRollback;
    private int toReserveNumber;
    private boolean needRollback;

    @Autowired
    private transient CommandGateway commandGateway;

    @StartSaga
    @SagaEventHandler(associationProperty = "orderId")
    public void handle(OrderCreatedEvent event) {
        this.orderIdentifier = event.getOrderId();
        this.toReserve = event.getProducts();
        toRollback = new HashMap<>();
        toReserveNumber = toReserve.size();
        event.getProducts().forEach((id, product) -> {
            ReserveProductCommand command = new ReserveProductCommand(orderIdentifier, id, product.getAmount());
            commandGateway.send(command);
        });
    }

    @SagaEventHandler(associationProperty = "orderId")
    public void handle(ProductNotEnoughEvent event) {
        toReserveNumber--;
        needRollback = true;
        if (toReserveNumber == 0)
            tryFinish();
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
        toRollback.remove(event.getProductId());
        if (toRollback.isEmpty())
            commandGateway.send(new RollbackOrderCommand(event.getOrderId()));
    }

    @SagaEventHandler(associationProperty = "id", keyName = "orderId")
    @EndSaga
    public void handle(OrderCancelledEvent event) throws OrderCreateFailedException {
        log.info("Order {} is cancelled", event.getId());
        // throw exception here will not cause the onFailure() method in the command callback
        //throw new OrderCreateFailedException("Not enough product to reserve!");
    }

    @SagaEventHandler(associationProperty = "orderId")
    public void handle(ProductReservedEvent event) {
        OrderProduct reservedProduct = toReserve.get(event.getProductId());
        reservedProduct.setReserved(true);
        toReserveNumber--;
        //Q: will a concurrent issue raise?
        if (toReserveNumber == 0)
            tryFinish();
    }

    @SagaEventHandler(associationProperty = "id", keyName = "orderId")
    @EndSaga
    public void handle(OrderConfirmedEvent event) throws InterruptedException {
        log.info("Order {} is confirmed", event.getId());
        //Thread.sleep(10000);
    }

}
