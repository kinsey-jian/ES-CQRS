package com.kinsey.es.controller;

import com.kinsey.es.es.commands.CreateOrderCommand;
import com.kinsey.es.model.order.OrderRequest;
import com.kinsey.es.model.order.OrderProduct;
import com.kinsey.es.utils.UIDGenerator;
import lombok.AllArgsConstructor;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/order")
@AllArgsConstructor
public class OrderController {

    private CommandGateway commandGateway;

    private final UIDGenerator uidGenerator;

    @PostMapping
    public void create(@RequestBody @Valid OrderRequest request){
        Map<String, Integer> products = request.getOrderProducts().stream().collect(Collectors.toMap(OrderProduct::getId, OrderProduct::getNumber));
        CreateOrderCommand command = new CreateOrderCommand(uidGenerator.getId(), request.getUsername(), products);
        commandGateway.send(command);
    }

}