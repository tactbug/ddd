package com.tactbug.ddd.product.outbound.publisher;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.tactbug.ddd.common.entity.BaseDomain;
import com.tactbug.ddd.common.entity.Event;
import com.tactbug.ddd.common.utils.SerializeUtil;
import com.tactbug.ddd.product.assist.exception.TactProductException;
import com.tactbug.ddd.product.domain.category.event.CategoryEvent;
import com.tactbug.ddd.product.outbound.repository.jpa.category.CategoryEventRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaFailureCallback;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
@Slf4j
public class EventPublisher {

    @Resource
    private KafkaTemplate<Long, String> kafkaTemplate;
    @Resource
    private CategoryEventRepository categoryEventRepository;

    public void publishCategoryEvent(Event<? extends BaseDomain> event){
        String json;
        try {
            json = SerializeUtil.objectToJson(event);
        } catch (JsonProcessingException e) {
            throw TactProductException.jsonException(e);
        }
        kafkaTemplate
                .send(event.getClass().getName(), event.getDomainId(), json)
                .addCallback(
                        result -> {
                            int retryTimes = 3;
                            while (retryTimes > 0){
                                try {
                                    CategoryEvent currentEvent = categoryEventRepository.findById(event.getId())
                                            .orElseThrow(() -> TactProductException.eventOperateError("广播事件[" + event.getId() + "]不存在"));
                                    currentEvent.publish();
                                    categoryEventRepository.save(currentEvent);
                                    return;
                                }catch (Exception e){
                                    retryTimes --;
                                }
                            }
                            log.error("广播事件[" + event + "]状态同步失败");
                        },
                        (KafkaFailureCallback<Integer, String>) ex ->
                                log.error("分类变更事件[" + event + "]发布失败", ex)
                );
    }
}
