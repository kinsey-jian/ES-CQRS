package com.kinsey.es.common.domain;

import org.axonframework.common.Assert;
import org.axonframework.common.IdentifierFactory;

import java.io.Serializable;

/**
 * Created by zj on 2018/7/22
 */
public class OrderId implements Serializable {

    private final String identifier;
    private final int hashCode;

    public OrderId() {
        this.identifier = IdentifierFactory.getInstance().generateIdentifier();
        this.hashCode = identifier.hashCode();
    }

    public OrderId(String identifier) {
        Assert.notNull(identifier, () -> "Identifier may not be null");
        this.identifier = identifier;
        this.hashCode = identifier.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof OrderId)) return false;

        OrderId orderId = (OrderId) o;

        return identifier.equals(orderId.identifier);
    }

    @Override
    public int hashCode() {
        return identifier.hashCode();
    }

    @Override
    public String toString() {
        return this.identifier;
    }

    public String getIdentifier() {
        return identifier;
    }
}
