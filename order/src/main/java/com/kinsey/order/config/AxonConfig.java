package com.kinsey.order.config;

import com.kinsey.order.es.aggregates.OrderAggregate;
import com.kinsey.order.es.jpa.CustomEmbeddedEventStore;
import com.kinsey.order.es.jpa.CustomEventSourcingRepository;
import com.kinsey.order.gateway.OrderCommandGateway;
import org.axonframework.commandhandling.SimpleCommandBus;
import org.axonframework.commandhandling.gateway.CommandGatewayFactory;
import org.axonframework.common.jpa.EntityManagerProvider;
import org.axonframework.config.EventHandlingConfiguration;
import org.axonframework.eventhandling.*;
import org.axonframework.eventhandling.async.AsynchronousEventProcessingStrategy;
import org.axonframework.eventhandling.async.SequentialPerAggregatePolicy;
import org.axonframework.eventhandling.saga.repository.jpa.JpaSagaStore;
import org.axonframework.eventsourcing.*;
import org.axonframework.eventsourcing.eventstore.EventStorageEngine;
import org.axonframework.serialization.Serializer;
import org.axonframework.serialization.json.JacksonSerializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.task.SimpleAsyncTaskExecutor;

import java.util.Arrays;

@Configuration
public class AxonConfig {

    @Bean
    @Primary
    public Serializer serializer() {
        return new JacksonSerializer();
    }

    @Bean
    public OrderCommandGateway getCommandGateway(SimpleCommandBus simpleCommandBus, CommandInterceptor commandInterceptor) {
        CommandGatewayFactory factory = new CommandGatewayFactory(simpleCommandBus, new CommandRetryScheduler(), commandInterceptor);
        return factory.createGateway(OrderCommandGateway.class);
    }

    @Bean
    public CustomEmbeddedEventStore customEmbeddedEventStore(EventStorageEngine storageEngine) {
        return new CustomEmbeddedEventStore(storageEngine);
    }

    @Bean
    public JpaSagaStore sagaStore(Serializer serializer, EntityManagerProvider entityManagerProvider) {
        return new JpaSagaStore(serializer, entityManagerProvider);
    }

    @Bean
    public CustomEventSourcingRepository<OrderAggregate> orderAggregateRepository(CustomEmbeddedEventStore eventStore, SnapshotTriggerDefinition snapshotTriggerDefinition) {
        return new CustomEventSourcingRepository<>(OrderAggregate.class, eventStore, snapshotTriggerDefinition);
    }

    @Bean
    public SnapshotTriggerDefinition snapshotTriggerDefinition(Snapshotter snapshotter) {
        return new EventCountSnapshotTriggerDefinition(snapshotter, 5);
    }

    @Bean
    public AggregateSnapshotter snapShotter(CustomEmbeddedEventStore eventStore) {
        return new AggregateSnapshotter(eventStore,
                Arrays.asList(new GenericAggregateFactory<>(OrderAggregate.class)));
    }

    @Autowired
    public void configure(EventHandlingConfiguration eventHandlingConfiguration) {

        eventHandlingConfiguration.registerEventProcessorFactory((configuration, name, eventHandlers) -> {
            SimpleEventHandlerInvoker simpleEventHandlerInvoker = new SimpleEventHandlerInvoker(
                    eventHandlers,
                    configuration.parameterResolverFactory(),
                    configuration.getComponent(ListenerInvocationErrorHandler.class, LoggingErrorHandler::new)
            );

            return new SubscribingEventProcessor(
                    name,
                    simpleEventHandlerInvoker,
                    configuration.eventBus(),
                    new AsynchronousEventProcessingStrategy(new SimpleAsyncTaskExecutor(), new SequentialPerAggregatePolicy()),
                    PropagatingErrorHandler.INSTANCE
            );

        });
    }
}
