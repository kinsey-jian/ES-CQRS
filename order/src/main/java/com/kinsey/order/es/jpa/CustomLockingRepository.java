package com.kinsey.order.es.jpa;

import org.axonframework.commandhandling.model.Aggregate;
import org.axonframework.commandhandling.model.ConcurrencyException;
import org.axonframework.commandhandling.model.LockAwareAggregate;
import org.axonframework.commandhandling.model.LockingRepository;
import org.axonframework.commandhandling.model.inspection.AggregateModel;
import org.axonframework.common.Assert;
import org.axonframework.common.lock.Lock;
import org.axonframework.common.lock.LockFactory;
import org.axonframework.common.lock.PessimisticLockFactory;
import org.axonframework.messaging.annotation.ParameterResolverFactory;
import org.axonframework.messaging.unitofwork.CurrentUnitOfWork;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.concurrent.Callable;

public abstract class CustomLockingRepository<T, A extends Aggregate<T>> extends CustomAbstractRepository<T, LockAwareAggregate<T, A>> {

    private static final Logger logger = LoggerFactory.getLogger(LockingRepository.class);
    private static final String LOCKFACTORY_NULL = "LockFactory may not be null";
    private final LockFactory lockFactory;

    /**
     * Initialize a repository with a pessimistic locking strategy.
     *
     * @param aggregateType The type of aggregate stored in this repository
     */
    protected CustomLockingRepository(Class<T> aggregateType) {
        this(aggregateType, new PessimisticLockFactory());
    }

    /**
     * Initialize a repository with a pessimistic locking strategy, using the given {@code aggregateModel}, describing
     * the structure of the aggregate.
     *
     * @param aggregateModel The model describing the structure of the aggregate
     */
    protected CustomLockingRepository(AggregateModel<T> aggregateModel) {
        this(aggregateModel, new PessimisticLockFactory());
    }

    /**
     * Initialize a repository with a pessimistic locking strategy and a parameter resolver factory.
     *
     * @param aggregateType            The type of aggregate stored in this repository
     * @param parameterResolverFactory The parameter resolver factory used to resolve parameters of annotated handlers
     */
    protected CustomLockingRepository(Class<T> aggregateType, ParameterResolverFactory parameterResolverFactory) {
        this(aggregateType, new PessimisticLockFactory(), parameterResolverFactory);
    }

    /**
     * Initialize the repository with the given {@code LockFactory}.
     *
     * @param aggregateType The type of aggregate stored in this repository
     * @param lockFactory   the lock factory to use
     */
    protected CustomLockingRepository(Class<T> aggregateType, LockFactory lockFactory) {
        super(aggregateType);
        Assert.notNull(lockFactory, () -> LOCKFACTORY_NULL);
        this.lockFactory = lockFactory;
    }

    /**
     * Initialize the repository with the given {@code LockFactory} and {@code aggregateModel}
     *
     * @param aggregateModel The model describing the structure of the aggregate
     * @param lockFactory    the lock factory to use
     */
    protected CustomLockingRepository(AggregateModel<T> aggregateModel, LockFactory lockFactory) {
        super(aggregateModel);
        Assert.notNull(lockFactory, () -> LOCKFACTORY_NULL);
        this.lockFactory = lockFactory;
    }

    /**
     * Initialize the repository with the given {@code LockFactory} and {@code ParameterResolverFactory}.
     *
     * @param aggregateType            The type of aggregate stored in this repository
     * @param lockFactory              The lock factory to use
     * @param parameterResolverFactory The parameter resolver factory used to resolve parameters of annotated handlers
     */
    protected CustomLockingRepository(Class<T> aggregateType, LockFactory lockFactory,
                                      ParameterResolverFactory parameterResolverFactory) {
        super(aggregateType, parameterResolverFactory);
        Assert.notNull(lockFactory, () -> LOCKFACTORY_NULL);
        this.lockFactory = lockFactory;
    }

    @Override
    protected LockAwareAggregate<T, A> doCreateNew(Callable<T> factoryMethod) throws Exception {
        A aggregate = doCreateNewForLock(factoryMethod);
        final String aggregateIdentifier = aggregate.identifierAsString();
        Lock lock = lockFactory.obtainLock(aggregateIdentifier);
        try {
            CurrentUnitOfWork.get().onCleanup(u -> lock.release());
        } catch (Exception ex) {
            if (lock != null) {
                logger.debug("Exception occurred while trying to add an aggregate. Releasing lock.", ex);
                lock.release();
            }
            throw ex;
        }
        return new LockAwareAggregate<>(aggregate, lock);
    }

    /**
     * Creates a new aggregate instance using the given {@code factoryMethod}. Implementations should assume that this
     * method is only called if a UnitOfWork is currently active.
     *
     * @param factoryMethod The method to create the aggregate's root instance
     *
     * @return an Aggregate instance describing the aggregate's state
     * @throws Exception when the factoryMethod throws an exception
     */
    protected abstract A doCreateNewForLock(Callable<T> factoryMethod) throws Exception;

    /**
     * Perform the actual loading of an aggregate. The necessary locks have been obtained.
     *
     * @param aggregateIdentifier the identifier of the aggregate to load
     * @param expectedVersion     The expected version of the aggregate
     *
     * @return the fully initialized aggregate
     * @throws AggregateNotFoundException if aggregate with given id cannot be found
     */
    @Override
    protected LockAwareAggregate<T, A> doLoad(String aggregateIdentifier, Long expectedVersion) {
        Lock lock = lockFactory.obtainLock(aggregateIdentifier);
        try {
            final A aggregate = doLoadWithLock(aggregateIdentifier, expectedVersion);
            CurrentUnitOfWork.get().onCleanup(u -> lock.release());
            return new LockAwareAggregate<>(aggregate, lock);
        } catch (Exception ex) {
            logger.debug("Exception occurred while trying to load an aggregate. Releasing lock.", ex);
            lock.release();
            throw ex;
        }
    }

    @Override
    protected LockAwareAggregate<T, A> doLoad(String aggregateIdentifier, Instant timestamp) {
        Lock lock = lockFactory.obtainLock(aggregateIdentifier);
        try {
            final A aggregate = doLoadWithLock(aggregateIdentifier, timestamp);
            CurrentUnitOfWork.get().onCleanup(u -> lock.release());
            return new LockAwareAggregate<>(aggregate, lock);
        } catch (Exception ex) {
            logger.debug("Exception occurred while trying to load an aggregate. Releasing lock.", ex);
            lock.release();
            throw ex;
        }
    }


    @Override
    protected void prepareForCommit(LockAwareAggregate<T, A> aggregate) {
        Assert.state(aggregate.isLockHeld(), () -> "An aggregate is being used for which a lock is no longer held");
        super.prepareForCommit(aggregate);
    }

    /**
     * Verifies whether all locks are valid and delegates to
     * {@link #doSaveWithLock(Aggregate)} to perform actual storage.
     *
     * @param aggregate the aggregate to store
     */
    @Override
    protected void doSave(LockAwareAggregate<T, A> aggregate) {
        if (aggregate.version() != null && !aggregate.isLockHeld()) {
            throw new ConcurrencyException(String.format(
                "The aggregate of type [%s] with identifier [%s] could not be " +
                    "saved, as a valid lock is not held. Either another thread has saved an aggregate, or " +
                    "the current thread had released its lock earlier on.",
                aggregate.getClass().getSimpleName(), aggregate.identifierAsString()));
        }
        doSaveWithLock(aggregate.getWrappedAggregate());
    }

    /**
     * Verifies whether all locks are valid and delegates to
     * {@link #doDeleteWithLock(Aggregate)} to perform actual deleting.
     *
     * @param aggregate the aggregate to delete
     */
    @Override
    protected final void doDelete(LockAwareAggregate<T, A> aggregate) {
        if (aggregate.version() != null && !aggregate.isLockHeld()) {
            throw new ConcurrencyException(String.format(
                "The aggregate of type [%s] with identifier [%s] could not be " +
                    "saved, as a valid lock is not held. Either another thread has saved an aggregate, or " +
                    "the current thread had released its lock earlier on.",
                aggregate.getClass().getSimpleName(), aggregate.identifierAsString()));
        }
        doDeleteWithLock(aggregate.getWrappedAggregate());
    }

    /**
     * Perform the actual saving of the aggregate. All necessary locks have been verified.
     *
     * @param aggregate the aggregate to store
     */
    protected abstract void doSaveWithLock(A aggregate);

    /**
     * Perform the actual deleting of the aggregate. All necessary locks have been verified.
     *
     * @param aggregate the aggregate to delete
     */
    protected abstract void doDeleteWithLock(A aggregate);

    /**
     * Loads the aggregate with the given aggregateIdentifier. All necessary locks have been obtained.
     *
     * @param aggregateIdentifier the identifier of the aggregate to load
     * @param expectedVersion     The expected version of the aggregate to load
     *
     * @return a fully initialized aggregate
     * @throws AggregateNotFoundException if the aggregate with given identifier does not exist
     */
    protected abstract A doLoadWithLock(String aggregateIdentifier, Long expectedVersion);

    protected abstract A doLoadWithLock(String aggregateIdentifier, Instant timestamp);

}
