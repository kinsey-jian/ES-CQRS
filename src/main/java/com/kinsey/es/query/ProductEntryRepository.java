package com.kinsey.es.query;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductEntryRepository extends MongoRepository<ProductEntry, String> {
}