package com.tactbug.query.replay.product;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.tactbug.ddd.common.utils.SerializeUtil;
import com.tactbug.ddd.product.assist.exception.TactProductException;
import com.tactbug.ddd.product.domain.category.Category;
import com.tactbug.ddd.product.domain.category.event.*;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.transaction.Transactional;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Component
public class ProductReplay {

    @Resource
    private EventRecordRepository eventRecordRepository;
    @Resource
    private CategoryDTORepository categoryDTORepository;

    private final static String TOPIC_PATTERN = "product";

    private final static ExecutorService THREAD_POOL = new ThreadPoolExecutor(
            2, 20, 5, TimeUnit.SECONDS, new ArrayBlockingQueue<>(20)
    );

    @KafkaListener(groupId = "product", topicPattern = TOPIC_PATTERN)
    public void dispatcher(
            @Payload String data,
            @Header(name = KafkaHeaders.RECEIVED_TOPIC) String topicName,
            Acknowledgment acknowledgment
    ){
        TopicEnum topic = TopicEnum.get(topicName.replace(TOPIC_PATTERN, ""));
        switch (topic){
            case CATEGORY -> categoryHandler(data, acknowledgment);
        }
    }

    private void categoryHandler(String payload, Acknowledgment acknowledgment){
        try {
            CategoryEvent categoryEvent = SerializeUtil.jsonToObject(payload, new TypeReference<>() {
            });
            EventRecord eventRecord = EventRecord.generate(categoryEvent);
            eventRecordRepository.save(eventRecord);
            acknowledgment.acknowledge();
            THREAD_POOL.execute(() -> categoryReplay(categoryEvent, eventRecord));
        } catch (JsonProcessingException e) {
            throw TactProductException.jsonException(e);
        }
    }

    @Transactional
    public void categoryReplay(CategoryEvent categoryEvent, EventRecord eventRecord){
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
            case CategoryRemarkUpdated c -> c.replay(snapshot);
            case CategoryParentChanged c -> c.replay(snapshot);
            case CategoryChildAdded c -> c.replay(snapshot);
            case CategoryChildRemoved c -> c.replay(snapshot);
            case CategoryDeleted ignored -> {
                CategoryDTO deletedCategory = CategoryDTO.generate(snapshot);
                deletedCategory.delete();
                categoryDTORepository.save(deletedCategory);
                eventRecord.execute();
                eventRecordRepository.save(eventRecord);
                return;
            }
            default -> throw new IllegalStateException("Unexpected value: " + categoryEvent);
        }
        CategoryDTO result = CategoryDTO.generate(snapshot);
        categoryDTORepository.save(result);
        eventRecord.execute();
        eventRecordRepository.save(eventRecord);
    }
}
