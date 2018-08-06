package com.kinsey.common.commands;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.axonframework.commandhandling.TargetAggregateIdentifier;

@Data
@AllArgsConstructor
@NoArgsConstructor
public abstract class AbstractOrderCommand {

    @TargetAggregateIdentifier
    private Long id;
}