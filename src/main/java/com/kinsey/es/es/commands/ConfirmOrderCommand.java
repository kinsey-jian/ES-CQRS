package com.kinsey.es.es.commands;

import lombok.Getter;

@Getter
public class ConfirmOrderCommand extends AbstractCommand {

    public ConfirmOrderCommand(Long id) {
        super(id);
    }
}