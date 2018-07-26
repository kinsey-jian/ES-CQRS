package com.kinsey.es.common.exception;

import org.axonframework.common.AxonException;

public class OrderCreateFailedException extends AxonException {

    public OrderCreateFailedException(String message) {
        super(message);
    }
}