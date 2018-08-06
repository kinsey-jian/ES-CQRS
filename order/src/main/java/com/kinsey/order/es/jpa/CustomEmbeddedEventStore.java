package com.kinsey.order.es.jpa;

import lombok.extern.slf4j.Slf4j;
import org.axonframework.eventhandling.EventMessage;
import org.axonframework.eventsourcing.DomainEventMessage;
import org.axonframework.eventsourcing.eventstore.DomainEventStream;
import org.axonframework.eventsourcing.eventstore.EmbeddedEventStore;
import org.axonframework.eventsourcing.eventstore.EventStorageEngine;
import org.axonframework.eventsourcing.eventstore.IteratorBackedDomainEventStream;
import org.axonframework.monitoring.MessageMonitor;

import java.time.Instant;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

@Slf4j
public class CustomEmbeddedEventStore extends EmbeddedEventStore {

    public CustomEmbeddedEventStore(EventStorageEngine storageEngine) {
        super(storageEngine);
    }

    public CustomEmbeddedEventStore(EventStorageEngine storageEngine, MessageMonitor<? super EventMessage<?>> monitor) {
        super(storageEngine, monitor);
    }

    public CustomEmbeddedEventStore(EventStorageEngine storageEngine, MessageMonitor<? super EventMessage<?>> monitor, int cachedEvents, long fetchDelay, long cleanupDelay, TimeUnit timeUnit) {
        super(storageEngine, monitor, cachedEvents, fetchDelay, cleanupDelay, timeUnit);
    }

    public DomainEventStream readEvents(String aggregateIdentifier, Instant timestamp) {
        Optional<DomainEventMessage<?>> optionalSnapshot;
        try {
            optionalSnapshot = storageEngine().readSnapshot(aggregateIdentifier);
        } catch (Exception | LinkageError e) {
            log.warn("Error reading snapshot. Reconstructing aggregate from entire event stream.", e);
            optionalSnapshot = Optional.empty();
        }
        DomainEventStream eventStream;
        // 加上时间判断，如果 snapshot 在指定的时间之间，那么可以使用，否则直接读取所有的 events
        if (optionalSnapshot.isPresent() && optionalSnapshot.get().getTimestamp().isBefore(timestamp)) {
            DomainEventMessage<?> snapshot = optionalSnapshot.get();
            eventStream = DomainEventStream.concat(DomainEventStream.of(snapshot),
                storageEngine().readEvents(aggregateIdentifier,
                    snapshot.getSequenceNumber() + 1));
        } else {
            eventStream = storageEngine().readEvents(aggregateIdentifier);
        }

        eventStream = new IteratorBackedDomainEventStream(eventStream.asStream().filter(m -> m.getTimestamp().isBefore(timestamp)).iterator());

        Stream<? extends DomainEventMessage<?>> domainEventMessages = stagedDomainEventMessages(aggregateIdentifier);
        return DomainEventStream.concat(eventStream, DomainEventStream.of(domainEventMessages));
    }

}
