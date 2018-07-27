package com.kinsey.es.controller;

import com.kinsey.es.es.commands.CreateProductCommand;
import com.kinsey.es.model.ProductRequest;
import com.kinsey.es.query.CustomResponseTypes;
import com.kinsey.es.query.ProductEntry;
import com.kinsey.es.utils.UIDGenerator;
import lombok.AllArgsConstructor;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.queryhandling.QueryGateway;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/product")
@AllArgsConstructor
public class ProductController {

    private CommandGateway commandGateway;

    private final QueryGateway queryGateway;

    private final UIDGenerator uidGenerator;

    @PostMapping
    public void create(@RequestBody @Valid ProductRequest request) {

        CreateProductCommand command = new CreateProductCommand(uidGenerator.getId(), request.getName(), request.getPrice(), request.getStock());
        commandGateway.sendAndWait(command);

    }

    @GetMapping
    public List<ProductEntry> queryProduct(){
        return queryGateway.query("111",CustomResponseTypes.listOf(ProductEntry.class)).join();
    }

}