package com.tactbug.ddd.product.query.handler;

import com.tactbug.ddd.common.avro.product.CategoryCreatedAvro;
import com.tactbug.ddd.common.avro.AvroDeSerialize;
import com.tactbug.ddd.product.TactProductApplication;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class EventDispatcher {

    @Resource
    private CategoryHandler categoryHandler;

    private static final String CATEGORY = TactProductApplication.APPLICATION_NAME + "-category";

//    @KafkaListener(groupId = "log", topics = CATEGORY)
//    public void handler(
//            @Header(KafkaHeaders.OFFSET) List<Long> offsets,
//            @Header(KafkaHeaders.RECEIVED_MESSAGE_KEY) String key,
//            @Payload String data,
//            Acknowledgment ack
//    ){
//        System.out.println("offset: " + offsets.toString());
//        System.out.println("domainID: " + key);
//        CategoryCreatedAvro categoryCreatedAvro = AvroDeSerialize.categoryCreatedAvro(data);
//        System.out.println("payload: " + categoryCreatedAvro);
//        ack.acknowledge();
//    }

    @KafkaListener(groupId = "category", topics = CATEGORY)
    public void handleCategory(
            @Payload String data,
            Acknowledgment ack
    ){
        CategoryCreatedAvro categoryCreatedAvro = AvroDeSerialize.categoryCreatedAvro(data);
        categoryHandler.acceptCreate(categoryCreatedAvro);
        ack.acknowledge();
    }

}
