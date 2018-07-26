package com.kinsey.es.controller;

import com.kinsey.es.es.commands.CreateOrderCommand;
import com.kinsey.es.utils.UIDGenerator;
import lombok.AllArgsConstructor;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/order")
@AllArgsConstructor
public class OrderController {

    private CommandGateway commandGateway;

    private final UIDGenerator uidGenerator;

    @RequestMapping(method = RequestMethod.POST)
    public void create(@RequestBody OrderRequest request){
        CreateOrderCommand command = new CreateOrderCommand(uidGenerator.getId(), request.getUsername(), request.getProducts());
        commandGateway.send(command);
    }

}