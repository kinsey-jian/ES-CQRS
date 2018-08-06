package com.kinsey.order.es.jpa;

import org.axonframework.commandhandling.model.Aggregate;
import org.axonframework.commandhandling.model.Repository;

import java.time.Instant;

public interface TimestampRepository<T> extends Repository<T> {

    Aggregate<T> load(String aggregateIdentifier, Instant timestamp);
}
