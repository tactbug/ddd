package com.tactbug.ddd.product.query.handler;

import com.tactbug.ddd.product.TactProductApplication;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class EventDispatcher {

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

}
