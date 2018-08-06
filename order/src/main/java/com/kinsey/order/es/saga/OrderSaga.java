package com.kinsey.order.es.saga;

import com.kinsey.common.commands.ConfirmOrderCommand;
import com.kinsey.common.commands.ReserveProductCommand;
import com.kinsey.common.commands.RollbackOrderCommand;
import com.kinsey.common.commands.RollbackReservationCommand;
import com.kinsey.common.domain.OrderProduct;
import com.kinsey.common.event.*;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.commandhandling.CommandCallback;
import org.axonframework.commandhandling.CommandMessage;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.eventhandling.saga.EndSaga;
import org.axonframework.eventhandling.saga.SagaEventHandler;
import org.axonframework.eventhandling.saga.StartSaga;
import org.axonframework.spring.stereotype.Saga;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import static org.axonframework.commandhandling.model.AggregateLifecycle.apply;


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
    @SagaEventHandler(associationProperty = "id")
    public void handle(OrderCreatedEvent event) {
        this.orderIdentifier = event.getId();
        this.toReserve = event.getProducts();
        toRollback = new HashMap<>();
        toReserveNumber = new AtomicInteger(toReserve.size());
        event.getProducts().forEach((id, product) -> {
            ReserveProductCommand command = new ReserveProductCommand(orderIdentifier, id, product.getAmount());
            commandGateway.send(command, new CommandCallback<ReserveProductCommand, Object>() {

                @Override
                public void onSuccess(CommandMessage commandMessage, Object result) {
                    log.debug("Send ReserveProductCommand successfully.");
                }

                @Override
                public void onFailure(CommandMessage commandMessage, Throwable cause) {
                    if (commandMessage.getPayload() == null) {
                        log.error("Msg payload is null!", cause);
                        return;
                    }
                    ReserveProductCommand cmd = (ReserveProductCommand) commandMessage.getPayload();
                    ProductReserveFailedEvent failedEvent = new ProductReserveFailedEvent(cmd.getOrderId(), cmd.getProductId());
                    apply(failedEvent);
                }
            });
        });
    }

    @SagaEventHandler(associationProperty = "id")
    public void handle(ProductNotEnoughEvent event) {
        needRollback = true;
        if (toReserveNumber.decrementAndGet() == 0) tryFinish();
    }

    @SagaEventHandler(associationProperty = "orderId")
    public void handle(ProductReserveFailedEvent event){
        log.info("Reserve product {} failed", event.getProductId());
        needRollback=true;
        if(toReserveNumber.decrementAndGet() == 0) tryFinish();
    }

    private void tryFinish() {
        if (needRollback) {
            toReserve.forEach((id, product) -> {
                if (!product.isReserved()) return;
                toRollback.put(id, product);
                commandGateway.send(new RollbackReservationCommand(orderIdentifier, id, product.getAmount()));
            });
            if (toRollback.isEmpty()) commandGateway.send(new RollbackOrderCommand(orderIdentifier));
            return;
        }
        commandGateway.send(new ConfirmOrderCommand(orderIdentifier));
    }

    @SagaEventHandler(associationProperty = "orderId", keyName = "id")
    public void handle(ReserveCancelledEvent event) {
        toRollback.remove(String.valueOf(event.getProductId()));
        if (toRollback.isEmpty())
            commandGateway.send(new RollbackOrderCommand(event.getOrderId()));
    }

    @SagaEventHandler(associationProperty = "id")
    @EndSaga
    public void handle(OrderCancelledEvent event) {
        log.info("Order {} is cancelled", event.getId());
    }

    @SagaEventHandler(associationProperty = "orderId", keyName = "id")
    public void handle(ProductReservedEvent event) {
        OrderProduct reservedProduct = toReserve.get(String.valueOf(event.getProductId()));
        reservedProduct.setReserved(true);
        if (toReserveNumber.decrementAndGet() == 0) tryFinish();
    }

    @SagaEventHandler(associationProperty = "id")
    @EndSaga
    public void handle(OrderConfirmedEvent event) {
        log.info("Order {} is confirmed", event.getId());
    }

}
