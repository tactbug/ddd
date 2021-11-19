package com.tactbug.ddd.product.query.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.tactbug.ddd.common.entity.Event;
import com.tactbug.ddd.common.utils.SerializeUtil;
import com.tactbug.ddd.product.TactProductApplication;
import com.tactbug.ddd.product.domain.category.Category;
import com.tactbug.ddd.product.domain.category.CategoryEvent;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class EventDispatcher {

    @Resource
    private CategoryHandler categoryHandler;

    private static final String CATEGORY = TactProductApplication.APPLICATION_NAME + "-category";

    @KafkaListener(groupId = "log", topics = CATEGORY)
    public void handler(
            @Header(KafkaHeaders.OFFSET) List<Long> offsets,
            @Header(KafkaHeaders.RECEIVED_MESSAGE_KEY) String key,
            @Payload String data,
            Acknowledgment ack
    ){
        System.out.println("offset: " + offsets.toString());
        System.out.println("domainID: " + key);
        System.out.println("payload: " + data);
        ack.acknowledge();
    }

    @KafkaListener(groupId = "category", topics = CATEGORY)
    public void handleCategory(
            @Payload String data,
            Acknowledgment ack
    ){
        try {
            Collection<CategoryEvent> events = SerializeUtil.jsonToObject(data, new TypeReference<>() {
            });
            events.forEach(e -> categoryHandler.accept(e));
            ack.acknowledge();
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

}
