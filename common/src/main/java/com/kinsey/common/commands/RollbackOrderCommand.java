package com.kinsey.common.commands;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RollbackOrderCommand extends AbstractOrderCommand {

    public RollbackOrderCommand(Long orderId) {
        super(orderId);
    }
}