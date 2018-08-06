package com.kinsey.product.controller;

import com.kinsey.common.commands.CreateProductCommand;
import com.kinsey.product.model.ProductRequest;
import com.kinsey.product.query.CustomResponseTypes;
import com.kinsey.product.query.ProductEntry;
import com.kinsey.product.utils.UIDGenerator;
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