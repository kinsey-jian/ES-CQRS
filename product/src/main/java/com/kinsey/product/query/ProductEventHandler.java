package com.kinsey.product.query;

import com.kinsey.common.event.ProductCreatedEvent;
import com.kinsey.common.event.ProductReservedEvent;
import com.kinsey.common.event.ReserveCancelledEvent;
import lombok.AllArgsConstructor;
import org.axonframework.eventhandling.EventHandler;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor
public class ProductEventHandler {

    private final ProductEntryRepository repository;

    @EventHandler
    public void on(ProductCreatedEvent event) {
        ProductEntry productEntry = new ProductEntry(String.valueOf(event.getId()), event.getName(), event.getPrice(), event.getStock());
        repository.insert(productEntry);
    }

    @EventHandler
    public void on(ProductReservedEvent event) {
        Optional<ProductEntry> product = repository.findById(String.valueOf(event.getProductId()));
        product.ifPresent(e -> {
            e.setStock(e.getStock() - event.getAmount());
            repository.save(e);
        });
    }

    @EventHandler
    public void on(ReserveCancelledEvent event) {
        Optional<ProductEntry> product = repository.findById(String.valueOf(event.getProductId()));
        product.ifPresent(e -> {
            e.setStock(e.getStock() + event.getAmount());
            repository.save(e);
        });
    }
}
