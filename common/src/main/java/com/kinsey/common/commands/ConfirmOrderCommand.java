package com.kinsey.common.commands;

import lombok.Getter;

@Getter
public class ConfirmOrderCommand extends AbstractOrderCommand {

    public ConfirmOrderCommand(Long id) {
        super(id);
    }
}