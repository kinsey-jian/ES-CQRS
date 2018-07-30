package com.kinsey.es.gateway;

import com.kinsey.es.es.aggregates.OrderAggregate;
import com.kinsey.es.es.commands.AbstractCommand;
import org.axonframework.commandhandling.gateway.Timeout;
import org.axonframework.messaging.annotation.MetaDataValue;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public interface OrderCommandGateway {

    // fire and forget
    void sendCommand(AbstractCommand command);

    // method that will wait for a result for 10 seconds
    @Timeout(value = 6, unit = TimeUnit.SECONDS)
    Long sendCommandAndWaitForAResult(AbstractCommand command);


    // method that will wait for a result for 10 seconds
    @Timeout(value = 6, unit = TimeUnit.SECONDS)
    void sendCommandAndWait(AbstractCommand command);

    // method that attaches meta data and will wait for a result for 10 seconds
    @Timeout(value = 6, unit = TimeUnit.SECONDS)
    OrderAggregate sendCommandAndWaitForAResult(AbstractCommand command,
                                                @MetaDataValue("userId") String userId);

    // this method will also wait, caller decides how long
    void sendCommandAndWait(AbstractCommand command, long timeout, TimeUnit unit) throws TimeoutException, InterruptedException;
}
