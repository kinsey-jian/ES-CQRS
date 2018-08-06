package com.kinsey.order.gateway;

import com.kinsey.common.commands.AbstractOrderCommand;
import com.kinsey.order.es.aggregates.OrderAggregate;
import org.axonframework.commandhandling.gateway.Timeout;
import org.axonframework.messaging.annotation.MetaDataValue;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public interface OrderCommandGateway {

    // fire and forget
    void sendCommand(AbstractOrderCommand command);

    // method that will wait for a result for 10 seconds
    @Timeout(value = 6, unit = TimeUnit.SECONDS)
    Long sendCommandAndWaitForAResult(AbstractOrderCommand command);


    // method that will wait for a result for 10 seconds
    @Timeout(value = 6, unit = TimeUnit.SECONDS)
    void sendCommandAndWait(AbstractOrderCommand command);

    // method that attaches meta data and will wait for a result for 10 seconds
    @Timeout(value = 6, unit = TimeUnit.SECONDS)
    OrderAggregate sendCommandAndWaitForAResult(AbstractOrderCommand command,
                                                @MetaDataValue("userId") String userId);

    // this method will also wait, caller decides how long
    void sendCommandAndWait(AbstractOrderCommand command, long timeout, TimeUnit unit) throws TimeoutException, InterruptedException;
}
