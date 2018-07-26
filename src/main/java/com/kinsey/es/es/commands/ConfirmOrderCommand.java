package com.kinsey.es.es.commands;

import lombok.Getter;

@Getter
public class ConfirmOrderCommand {

    private Long id;

    public ConfirmOrderCommand(Long id) {
        this.id = id;
    }
}