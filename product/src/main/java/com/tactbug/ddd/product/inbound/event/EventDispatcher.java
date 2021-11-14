package com.tactbug.ddd.product.inbound.event;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.tactbug.ddd.common.utils.SerializeUtil;
import com.tactbug.ddd.product.TactProductApplication;
import com.tactbug.ddd.product.assist.exception.TactProductException;
import com.tactbug.ddd.product.domain.category.CategoryEvent;
import com.tactbug.ddd.product.outbound.publisher.EventTopics;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class EventDispatcher {

    @KafkaListener(topicPattern = TactProductApplication.APPLICATION_NAME)
    public void dispatcher(
            @Payload String info,
            @Header(name = KafkaHeaders.RECEIVED_TOPIC) String topicName,
            @Header(name = KafkaHeaders.RECEIVED_MESSAGE_KEY) Long domainId
    ){
        System.out.println("开始处理领域事件");
        EventTopics topic = EventTopics.valueOf(topicName);
        switch (topic){
            case CATEGORY -> categoryHandler(domainId, info);
            default -> throw TactProductException.eventOperateError("不支持的topic类型[" + topic + "]");
        }
    }

    private void categoryHandler(Long domainId, String payload){
        // 这里应该将payload反序列化为event存储后返回ack应答
        List<CategoryEvent> events;
        try {
            events = SerializeUtil.jsonToObject(payload, new TypeReference<List<CategoryEvent>>() {
            });
        } catch (JsonProcessingException e) {
            throw TactProductException.jsonException(e);
        }
        System.out.println(domainId + "事件" + events + "存入成功");
    }

}
