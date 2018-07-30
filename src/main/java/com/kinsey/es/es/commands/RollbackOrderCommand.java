package com.kinsey.es.es.commands;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RollbackOrderCommand extends AbstractCommand{

    public RollbackOrderCommand(Long orderId) {
        super(orderId);
    }
}