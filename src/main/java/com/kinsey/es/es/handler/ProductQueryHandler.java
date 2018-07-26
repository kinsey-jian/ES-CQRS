package com.kinsey.es.es.handler;

import com.kinsey.es.query.ProductEntry;
import com.kinsey.es.query.ProductEntryRepository;
import lombok.AllArgsConstructor;
import org.axonframework.queryhandling.QueryHandler;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by zj on 2018/7/26
 */
@Component
@AllArgsConstructor
public class ProductQueryHandler {

    private final ProductEntryRepository productEntryRepository;

    @QueryHandler
    public List<ProductEntry> query(String s){
        return productEntryRepository.findAll();
    }
}
