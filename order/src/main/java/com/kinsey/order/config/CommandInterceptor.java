package com.kinsey.order.config;

import com.kinsey.common.commands.AbstractOrderCommand;
import com.kinsey.common.commands.CreateOrderCommand;
import com.kinsey.order.es.metadata.MetaDataUser;
import com.kinsey.order.es.metadata.MetaDataUserInterface;
import com.kinsey.order.utils.UIDGenerator;
import lombok.AllArgsConstructor;
import org.axonframework.commandhandling.GenericCommandMessage;
import org.axonframework.messaging.MessageDispatchInterceptor;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

@AllArgsConstructor
@Configuration
public class CommandInterceptor implements MessageDispatchInterceptor {

    private final UIDGenerator uidGenerator;

    @Override
    public BiFunction<Integer, GenericCommandMessage<AbstractOrderCommand>, GenericCommandMessage<AbstractOrderCommand>> handle(List messages) {
        return (index, message) -> {
            // create command 自动生成 ID
            if (message.getPayload() instanceof CreateOrderCommand) {
                CreateOrderCommand payload = (CreateOrderCommand) message.getPayload();
                payload.setId(uidGenerator.getId());
            }
            // 添加 user info 作为 MetaData
            Map<String, MetaDataUserInterface> map = new HashMap<>();
//            Optional.of(SecurityContextHolder.getContext())
//                .flatMap(c -> Optional.of(c.getAuthentication()))
//                .ifPresent(o -> {});

            map.put("user", MetaDataUser.builder().customerId(1L).name("Test").userId(2L).build());
            return map.isEmpty() ? message : message.andMetaData(map);
        };
    }
}
