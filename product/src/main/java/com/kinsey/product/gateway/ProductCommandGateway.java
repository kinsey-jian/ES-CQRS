package com.kinsey.product.gateway;

import com.kinsey.common.commands.AbstractProductCommand;
import com.kinsey.product.es.aggregates.ProductAggregate;
import org.axonframework.commandhandling.gateway.Timeout;
import org.axonframework.messaging.annotation.MetaDataValue;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public interface ProductCommandGateway {

    // fire and forget
    void sendCommand(AbstractProductCommand command);

    // method that will wait for a result for 10 seconds
    @Timeout(value = 6, unit = TimeUnit.SECONDS)
    Long sendCommandAndWaitForAResult(AbstractProductCommand command);


    // method that will wait for a result for 10 seconds
    @Timeout(value = 6, unit = TimeUnit.SECONDS)
    void sendCommandAndWait(AbstractProductCommand command);

    // method that attaches meta data and will wait for a result for 10 seconds
    @Timeout(value = 6, unit = TimeUnit.SECONDS)
    ProductAggregate sendCommandAndWaitForAResult(AbstractProductCommand command,
                                                  @MetaDataValue("userId") String userId);

    // this method will also wait, caller decides how long
    void sendCommandAndWait(AbstractProductCommand command, long timeout, TimeUnit unit) throws TimeoutException, InterruptedException;
}
