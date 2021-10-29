package com.tactbug.gateway.replay.product;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.tactbug.ddd.common.utils.SerializeUtil;
import com.tactbug.ddd.product.assist.exception.TactProductException;
import com.tactbug.ddd.product.domain.category.Category;
import com.tactbug.ddd.product.domain.category.event.CategoryCreated;
import com.tactbug.ddd.product.domain.category.event.CategoryEvent;
import com.tactbug.ddd.product.domain.category.event.CategoryNameUpdated;
import com.tactbug.gateway.dto.TopicEnum;
import com.tactbug.gateway.dto.product.CategoryDTO;
import com.tactbug.gateway.replay.EventRecord;
import com.tactbug.gateway.replay.EventRecordRepository;
import com.tactbug.gateway.replay.product.repository.CategoryDTORepository;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class ProductReplay {

    @Resource
    private EventRecordRepository eventRecordRepository;
    @Resource
    private CategoryDTORepository categoryDTORepository;

    private final String topicPattern = "product";

    @KafkaListener(groupId = "product", topicPattern = "product*")
    public void dispatcher(
            @Payload String data,
            @Header(name = KafkaHeaders.RECEIVED_TOPIC) String topicName
    ){
        TopicEnum topic = TopicEnum.get(topicName.replace(topicPattern, ""));
        switch (topic){
            case CATEGORY -> categoryHandler(data);
        }
    }

    private void categoryHandler(String payload){
        try {
            CategoryEvent categoryEvent = SerializeUtil.jsonToObject(payload, new TypeReference<>() {
            });
            EventRecord eventRecord = EventRecord.generate(categoryEvent);
            eventRecordRepository.save(eventRecord);
        } catch (JsonProcessingException e) {
            throw TactProductException.jsonException(e);
        }
    }

    private void categoryReplay(CategoryEvent categoryEvent){
        Category snapshot;
        if (categoryEvent instanceof CategoryCreated)
        {
            snapshot = new Category();
        }else {
            CategoryDTO categoryDTO = categoryDTORepository.findById(categoryEvent.getDomainId())
                    .orElseThrow(() -> TactProductException.resourceOperateError("分类[" + categoryEvent.getDomainId() + "]不存在"));
            snapshot = categoryDTO.convertToCategory();
        }
        switch (categoryEvent){
            case CategoryCreated c -> c.replay(snapshot);
            case CategoryNameUpdated c -> c.replay(snapshot);
            default -> throw new IllegalStateException("Unexpected value: " + categoryEvent);
        }
    }
}
