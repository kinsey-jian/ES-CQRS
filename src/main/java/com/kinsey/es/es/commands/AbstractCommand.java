package com.kinsey.es.es.commands;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.axonframework.commandhandling.TargetAggregateIdentifier;

@Data
@AllArgsConstructor
@NoArgsConstructor
public abstract class AbstractCommand {

    @TargetAggregateIdentifier
    private Long id;
}