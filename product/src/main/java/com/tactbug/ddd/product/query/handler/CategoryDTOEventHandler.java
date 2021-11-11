package com.tactbug.ddd.product.query.handler;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@KafkaListener(groupId = "categoryDTO", topicPattern = "product")
public class CategoryDTOEventHandler {

}
