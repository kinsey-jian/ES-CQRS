package com.kinsey.order.query;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderEntryRepository extends MongoRepository<OrderEntry, String>, QuerydslPredicateExecutor<OrderEntry> {
}