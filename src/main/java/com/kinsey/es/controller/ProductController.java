package com.kinsey.es.controller;

import com.kinsey.es.es.commands.CreateProductCommand;
import com.kinsey.es.query.CustomResponseTypes;
import com.kinsey.es.query.ProductEntry;
import com.kinsey.es.query.ProductEntryRepository;
import com.kinsey.es.utils.UIDGenerator;
import lombok.AllArgsConstructor;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.queryhandling.QueryGateway;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("/product")
@AllArgsConstructor
public class ProductController {

    private CommandGateway commandGateway;

    private final QueryGateway queryGateway;

    private final UIDGenerator uidGenerator;

    @PostMapping
    public void create(@RequestParam(value = "name") String name,
                       @RequestParam(value = "price") long price,
                       @RequestParam(value = "stock") int stock) {

        CreateProductCommand command = new CreateProductCommand(uidGenerator.getId(), name, price * 100, stock);
        commandGateway.sendAndWait(command);

    }

    @GetMapping
    public List<ProductEntry> queryProduct(){
        return queryGateway.query("111",CustomResponseTypes.listOf(ProductEntry.class)).join();
    }

}