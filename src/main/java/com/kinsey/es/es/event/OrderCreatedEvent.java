package com.kinsey.es.es.event;

import com.kinsey.es.common.domain.OrderProduct;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.axonframework.commandhandling.TargetAggregateIdentifier;

import java.util.Map;

/**
 * Created by zj on 2018/7/22
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OrderCreatedEvent {

    @TargetAggregateIdentifier
    private Long orderId;

    private String username;

    private Map<String, OrderProduct> products;

}
