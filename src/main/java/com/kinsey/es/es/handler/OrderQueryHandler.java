package com.kinsey.es.es.handler;

import com.kinsey.es.es.aggregates.OrderAggregate;
import com.kinsey.es.es.jpa.CustomEventSourcingRepository;
import com.kinsey.es.query.OrderEntry;
import com.kinsey.es.query.OrderEntryRepository;
import lombok.AllArgsConstructor;
import org.axonframework.queryhandling.QueryHandler;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by zj on 2018/7/26
 */
@Component
@AllArgsConstructor
public class OrderQueryHandler {

    private final OrderEntryRepository orderEntryRepository;

    private final CustomEventSourcingRepository<OrderAggregate> orderAggregateRepository;

    @QueryHandler
    private List<OrderEntry> query(){
         return orderEntryRepository.findAll();
    }
}
