package com.kinsey.order.controller;

import com.kinsey.common.commands.CreateOrderCommand;
import com.kinsey.order.gateway.OrderCommandGateway;
import com.kinsey.order.model.order.OrderProduct;
import com.kinsey.order.model.order.OrderRequest;
import com.kinsey.order.utils.UIDGenerator;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/order")
@AllArgsConstructor
public class OrderController {

    private OrderCommandGateway commandGateway;

    private final UIDGenerator uidGenerator;

    @PostMapping
    public void create(@RequestBody @Valid OrderRequest request){
        Map<String, Integer> products = request.getOrderProducts().stream().collect(Collectors.toMap(OrderProduct::getId, OrderProduct::getNumber));
        CreateOrderCommand command = new CreateOrderCommand(uidGenerator.getId(), request.getUsername(), products);
        commandGateway.sendCommandAndWait(command);
    }

}