package com.kinsey.product.config;

import com.kinsey.common.commands.AbstractProductCommand;
import com.kinsey.common.commands.CreateProductCommand;
import com.kinsey.product.es.metadata.MetaDataUser;
import com.kinsey.product.es.metadata.MetaDataUserInterface;
import com.kinsey.product.utils.UIDGenerator;
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
    public BiFunction<Integer, GenericCommandMessage<AbstractProductCommand>, GenericCommandMessage<AbstractProductCommand>> handle(List messages) {
        return (index, message) -> {
            // create command 自动生成 ID
            if (message.getPayload() instanceof CreateProductCommand) {
                CreateProductCommand payload = (CreateProductCommand) message.getPayload();
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
