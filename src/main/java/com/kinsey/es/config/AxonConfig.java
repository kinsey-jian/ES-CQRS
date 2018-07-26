package com.kinsey.es.config;

import com.kinsey.es.es.aggregates.OrderAggregate;
import com.kinsey.es.es.aggregates.ProductAggregate;
import com.kinsey.es.es.jpa.CustomEmbeddedEventStore;
import com.kinsey.es.es.jpa.CustomEventSourcingRepository;
import org.axonframework.common.jpa.EntityManagerProvider;
import org.axonframework.config.EventHandlingConfiguration;
import org.axonframework.eventhandling.*;
import org.axonframework.eventhandling.async.AsynchronousEventProcessingStrategy;
import org.axonframework.eventhandling.async.SequentialPerAggregatePolicy;
import org.axonframework.eventhandling.saga.repository.jpa.JpaSagaStore;
import org.axonframework.eventsourcing.eventstore.EventStorageEngine;
import org.axonframework.serialization.Serializer;
import org.axonframework.serialization.json.JacksonSerializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary ;
import org.springframework.core.task.SimpleAsyncTaskExecutor;

@Configuration
public class AxonConfig {

    @Bean
    @Primary
    public Serializer serializer() {
        return new JacksonSerializer();
    }


    @Bean
    public CustomEmbeddedEventStore customEmbeddedEventStore(EventStorageEngine storageEngine) {
        return new CustomEmbeddedEventStore(storageEngine);
    }

    @Bean
    public CustomEventSourcingRepository<OrderAggregate> orderAggregateRepository(CustomEmbeddedEventStore eventStore) {
        return new CustomEventSourcingRepository<>(OrderAggregate.class, eventStore);
    }

    @Bean
    public CustomEventSourcingRepository<ProductAggregate> contractAggregateRepository(CustomEmbeddedEventStore eventStore) {
        return new CustomEventSourcingRepository<>(ProductAggregate.class, eventStore);
    }

    @Bean
    public JpaSagaStore sagaStore(Serializer serializer, EntityManagerProvider entityManagerProvider) {
        return new JpaSagaStore(serializer, entityManagerProvider);
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
