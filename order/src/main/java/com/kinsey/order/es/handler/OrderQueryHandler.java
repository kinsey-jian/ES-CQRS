package com.kinsey.order.es.handler;

import com.kinsey.order.es.aggregates.OrderAggregate;
import com.kinsey.order.es.jpa.CustomEventSourcingRepository;
import com.kinsey.order.query.OrderEntry;
import com.kinsey.order.query.OrderEntryRepository;
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
