package com.kinsey.es.es.jpa;

import org.axonframework.commandhandling.model.AbstractRepository;
import org.axonframework.commandhandling.model.Aggregate;
import org.axonframework.commandhandling.model.inspection.AggregateModel;
import org.axonframework.messaging.annotation.ParameterResolverFactory;
import org.axonframework.messaging.unitofwork.CurrentUnitOfWork;
import org.axonframework.messaging.unitofwork.UnitOfWork;

import java.time.Instant;
import java.util.Map;

public abstract class CustomAbstractRepository<T, A extends Aggregate<T>> extends AbstractRepository<T, A> implements TimestampRepository<T> {

    protected CustomAbstractRepository(Class<T> aggregateType) {
        super(aggregateType);
    }

    protected CustomAbstractRepository(Class<T> aggregateType, ParameterResolverFactory parameterResolverFactory) {
        super(aggregateType, parameterResolverFactory);
    }

    protected CustomAbstractRepository(AggregateModel<T> aggregateModel) {
        super(aggregateModel);
    }

    public A load(String aggregateIdentifier, Instant timestamp) {

        if (timestamp == null) {
            return this.load(aggregateIdentifier);
        }

        UnitOfWork<?> uow = CurrentUnitOfWork.get();
        Map<String, A> aggregates = managedAggregates(uow);
        A aggregate = aggregates.computeIfAbsent(aggregateIdentifier,
            s -> doLoad(aggregateIdentifier, timestamp));
        uow.onRollback(u -> aggregates.remove(aggregateIdentifier));
        prepareForCommit(aggregate);

        return aggregate;
    }

    protected abstract A doLoad(String aggregateIdentifier, Instant timestamp);

}
