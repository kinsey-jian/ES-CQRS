package com.kinsey.order.es.jpa;

import org.axonframework.commandhandling.conflictresolution.ConflictResolution;
import org.axonframework.commandhandling.conflictresolution.DefaultConflictResolver;
import org.axonframework.commandhandling.model.Aggregate;
import org.axonframework.commandhandling.model.AggregateNotFoundException;
import org.axonframework.commandhandling.model.LockAwareAggregate;
import org.axonframework.commandhandling.model.LockingRepository;
import org.axonframework.commandhandling.model.inspection.AggregateModel;
import org.axonframework.common.Assert;
import org.axonframework.common.lock.LockFactory;
import org.axonframework.eventsourcing.*;
import org.axonframework.eventsourcing.eventstore.DomainEventStream;
import org.axonframework.messaging.annotation.ParameterResolverFactory;
import org.axonframework.messaging.unitofwork.CurrentUnitOfWork;

import java.time.Instant;
import java.util.concurrent.Callable;

public class CustomEventSourcingRepository<T> extends CustomLockingRepository<T, EventSourcedAggregate<T>> {

    private static final String EVENT_STORE_NULL = "eventStore may not be null";
    private final CustomEmbeddedEventStore eventStore;
    private final SnapshotTriggerDefinition snapshotTriggerDefinition;
    private final AggregateFactory<T> aggregateFactory;

    /**
     * Initializes a repository with the default locking strategy, using a GenericAggregateFactory to create new
     * aggregate instances of given {@code aggregateType}.
     *
     * @param aggregateType The type of aggregate stored in this repository
     * @param eventStore    The event store that holds the event streams for this repository
     *
     * @see LockingRepository#LockingRepository(Class)
     */
    public CustomEventSourcingRepository(final Class<T> aggregateType, CustomEmbeddedEventStore eventStore) {
        this(new GenericAggregateFactory<>(aggregateType), eventStore, NoSnapshotTriggerDefinition.INSTANCE);
    }

    /**
     * Initializes a repository with the default locking strategy, using a GenericAggregateFactory to create new
     * aggregate instances of given {@code aggregateType}.
     *
     * @param aggregateType             The type of aggregate stored in this repository
     * @param eventStore                The event store that holds the event streams for this repository
     * @param snapshotTriggerDefinition The definition describing when to trigger a snapshot
     *
     * @see LockingRepository#LockingRepository(Class)
     */
    public CustomEventSourcingRepository(final Class<T> aggregateType, CustomEmbeddedEventStore eventStore,
                                         SnapshotTriggerDefinition snapshotTriggerDefinition) {
        this(new GenericAggregateFactory<>(aggregateType), eventStore, snapshotTriggerDefinition);
    }

    /**
     * Initializes a repository with the default locking strategy, using the given {@code aggregateFactory} to
     * create new aggregate instances.
     *
     * @param aggregateFactory The factory for new aggregate instances
     * @param eventStore       The event store that holds the event streams for this repository
     *
     * @see LockingRepository#LockingRepository(Class)
     */
    public CustomEventSourcingRepository(final AggregateFactory<T> aggregateFactory, CustomEmbeddedEventStore eventStore) {
        this(aggregateFactory, eventStore, NoSnapshotTriggerDefinition.INSTANCE);
    }

    /**
     * Initializes a repository with the default locking strategy, using the given {@code aggregateFactory} to
     * create new aggregate instances.
     *
     * @param aggregateModel   The meta model describing the aggregate's structure
     * @param aggregateFactory The factory for new aggregate instances
     * @param eventStore       The event store that holds the event streams for this repository
     *
     * @see LockingRepository#LockingRepository(Class)
     */
    public CustomEventSourcingRepository(AggregateModel<T> aggregateModel, AggregateFactory<T> aggregateFactory, CustomEmbeddedEventStore eventStore) {
        this(aggregateModel, aggregateFactory, eventStore, NoSnapshotTriggerDefinition.INSTANCE);
    }

    /**
     * Initializes a repository with the default locking strategy, using the given {@code aggregateFactory} to
     * create new aggregate instances and triggering snapshots using the given {@code snapshotTriggerDefinition}
     *
     * @param aggregateFactory          The factory for new aggregate instances
     * @param eventStore                The event store that holds the event streams for this repository
     * @param snapshotTriggerDefinition The definition describing when to trigger a snapshot
     *
     * @see LockingRepository#LockingRepository(Class)
     */
    public CustomEventSourcingRepository(final AggregateFactory<T> aggregateFactory, CustomEmbeddedEventStore eventStore,
                                         SnapshotTriggerDefinition snapshotTriggerDefinition) {
        super(aggregateFactory.getAggregateType());
        Assert.notNull(eventStore, () -> EVENT_STORE_NULL);
        this.aggregateFactory = aggregateFactory;
        this.eventStore = eventStore;
        this.snapshotTriggerDefinition = snapshotTriggerDefinition;
    }

    /**
     * Initializes a repository with the default locking strategy, using the given {@code aggregateFactory} to
     * create new aggregate instances and triggering snapshots using the given {@code snapshotTriggerDefinition}
     *
     * @param aggregateModel            The meta model describing the aggregate's structure
     * @param aggregateFactory          The factory for new aggregate instances
     * @param eventStore                The event store that holds the event streams for this repository
     * @param snapshotTriggerDefinition The definition describing when to trigger a snapshot
     *
     * @see LockingRepository#LockingRepository(Class)
     */
    public CustomEventSourcingRepository(AggregateModel<T> aggregateModel, AggregateFactory<T> aggregateFactory,
                                         CustomEmbeddedEventStore eventStore, SnapshotTriggerDefinition snapshotTriggerDefinition) {
        super(aggregateModel);
        Assert.notNull(eventStore, () -> EVENT_STORE_NULL);
        this.aggregateFactory = aggregateFactory;
        this.eventStore = eventStore;
        this.snapshotTriggerDefinition = snapshotTriggerDefinition;
    }


    /**
     * Initializes a repository with the default locking strategy, using the given {@code aggregateFactory} to
     * create new aggregate instances.
     *
     * @param aggregateFactory          The factory for new aggregate instances
     * @param eventStore                The event store that holds the event streams for this repository
     * @param parameterResolverFactory  The parameter resolver factory used to resolve parameters of annotated handlers
     * @param snapshotTriggerDefinition The definition describing when to trigger a snapshot
     *
     * @see LockingRepository#LockingRepository(Class)
     */
    public CustomEventSourcingRepository(AggregateFactory<T> aggregateFactory, CustomEmbeddedEventStore eventStore,
                                         ParameterResolverFactory parameterResolverFactory,
                                         SnapshotTriggerDefinition snapshotTriggerDefinition) {
        super(aggregateFactory.getAggregateType(), parameterResolverFactory);
        Assert.notNull(eventStore, () -> EVENT_STORE_NULL);
        this.snapshotTriggerDefinition = snapshotTriggerDefinition;
        this.eventStore = eventStore;
        this.aggregateFactory = aggregateFactory;
    }

    /**
     * Initialize a repository with the given locking strategy.
     *
     * @param aggregateFactory          The factory for new aggregate instances
     * @param eventStore                The event store that holds the event streams for this repository
     * @param lockFactory               the locking strategy to apply to this repository
     * @param snapshotTriggerDefinition The definition describing when to trigger a snapshot
     */
    public CustomEventSourcingRepository(AggregateFactory<T> aggregateFactory, CustomEmbeddedEventStore eventStore, LockFactory lockFactory,
                                         SnapshotTriggerDefinition snapshotTriggerDefinition) {
        super(aggregateFactory.getAggregateType(), lockFactory);
        Assert.notNull(eventStore, () -> EVENT_STORE_NULL);
        this.eventStore = eventStore;
        this.aggregateFactory = aggregateFactory;
        this.snapshotTriggerDefinition = snapshotTriggerDefinition;
    }

    /**
     * Initialize a repository with the given locking strategy and parameter resolver factory.
     *
     * @param aggregateFactory          The factory for new aggregate instances
     * @param eventStore                The event store that holds the event streams for this repository
     * @param lockFactory               The locking strategy to apply to this repository
     * @param parameterResolverFactory  The parameter resolver factory used to resolve parameters of annotated handlers
     * @param snapshotTriggerDefinition The definition describing when to trigger a snapshot
     */
    public CustomEventSourcingRepository(AggregateFactory<T> aggregateFactory, CustomEmbeddedEventStore eventStore, LockFactory lockFactory,
                                         ParameterResolverFactory parameterResolverFactory,
                                         SnapshotTriggerDefinition snapshotTriggerDefinition) {
        super(aggregateFactory.getAggregateType(), lockFactory, parameterResolverFactory);
        Assert.notNull(eventStore, () -> EVENT_STORE_NULL);
        this.eventStore = eventStore;
        this.aggregateFactory = aggregateFactory;
        this.snapshotTriggerDefinition = snapshotTriggerDefinition;
    }

    /**
     * Perform the actual loading of an aggregate. The necessary locks have been obtained.
     *
     * @param aggregateIdentifier the identifier of the aggregate to load
     * @param expectedVersion     The expected version of the loaded aggregate
     *
     * @return the fully initialized aggregate
     * @throws AggregateDeletedException  in case an aggregate existed in the past, but has been deleted
     * @throws AggregateNotFoundException when an aggregate with the given identifier does not exist
     */
    @Override
    protected EventSourcedAggregate<T> doLoadWithLock(String aggregateIdentifier, Long expectedVersion) {
        DomainEventStream eventStream = eventStore.readEvents(aggregateIdentifier);
        SnapshotTrigger trigger = snapshotTriggerDefinition.prepareTrigger(aggregateFactory.getAggregateType());
        if (!eventStream.hasNext()) {
            throw new AggregateNotFoundException(aggregateIdentifier, "The aggregate was not found in the event store");
        }
        EventSourcedAggregate<T> aggregate = EventSourcedAggregate
            .initialize(aggregateFactory.createAggregateRoot(aggregateIdentifier, eventStream.peek()),
                aggregateModel(), eventStore, trigger);
        aggregate.initializeState(eventStream);
        if (aggregate.isDeleted()) {
            throw new AggregateDeletedException(aggregateIdentifier);
        }
        return aggregate;
    }

    @Override
    protected EventSourcedAggregate<T> doLoadWithLock(String aggregateIdentifier, Instant timestamp) {
        DomainEventStream eventStream = eventStore.readEvents(aggregateIdentifier, timestamp);

        SnapshotTrigger trigger = snapshotTriggerDefinition.prepareTrigger(aggregateFactory.getAggregateType());
        if (!eventStream.hasNext()) {
            throw new AggregateNotFoundException(aggregateIdentifier, "The aggregate was not found in the event store");
        }
        EventSourcedAggregate<T> aggregate = EventSourcedAggregate
            .initialize(aggregateFactory.createAggregateRoot(aggregateIdentifier, eventStream.peek()),
                aggregateModel(), eventStore, trigger);
        aggregate.initializeState(eventStream);
        if (aggregate.isDeleted()) {
            throw new AggregateDeletedException(aggregateIdentifier);
        }
        return aggregate;
    }

    @Override
    protected void validateOnLoad(Aggregate<T> aggregate, Long expectedVersion) {
        if (expectedVersion != null && expectedVersion < aggregate.version()) {
            DefaultConflictResolver conflictResolver =
                new DefaultConflictResolver(eventStore, aggregate.identifierAsString(), expectedVersion,
                    aggregate.version());
            ConflictResolution.initialize(conflictResolver);
            CurrentUnitOfWork.get().onPrepareCommit(uow -> conflictResolver.ensureConflictsResolved());
        } else {
            super.validateOnLoad(aggregate, expectedVersion);
        }
    }

    @Override
    protected void reportIllegalState(LockAwareAggregate<T, EventSourcedAggregate<T>> aggregate) {
        // event sourcing repositories are able to reconstruct the current state
    }

    @Override
    protected EventSourcedAggregate<T> doCreateNewForLock(Callable<T> factoryMethod) throws Exception {
        return EventSourcedAggregate.initialize(factoryMethod, aggregateModel(), eventStore,
            snapshotTriggerDefinition.prepareTrigger(getAggregateType()));
    }

    @Override
    protected void doSaveWithLock(EventSourcedAggregate<T> aggregate) {
        // do nothing
    }

    @Override
    protected void doDeleteWithLock(EventSourcedAggregate<T> aggregate) {
        // do nothing
    }

    /**
     * Returns the factory used by this repository.
     *
     * @return the factory used by this repository
     */
    public AggregateFactory<T> getAggregateFactory() {
        return aggregateFactory;
    }
}
