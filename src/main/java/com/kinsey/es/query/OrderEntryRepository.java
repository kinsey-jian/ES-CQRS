package com.kinsey.es.query;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderEntryRepository extends MongoRepository<OrderEntry, String> {
}