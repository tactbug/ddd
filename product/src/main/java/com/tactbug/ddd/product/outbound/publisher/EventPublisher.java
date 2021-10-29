package com.tactbug.ddd.product.outbound.publisher;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.tactbug.ddd.common.entity.BaseDomain;
import com.tactbug.ddd.common.entity.Event;
import com.tactbug.ddd.common.utils.SerializeUtil;
import com.tactbug.ddd.product.TactProductApplication;
import com.tactbug.ddd.product.assist.exception.TactProductException;
import com.tactbug.ddd.product.domain.category.event.CategoryEvent;
import com.tactbug.ddd.product.outbound.repository.jpa.category.CategoryEventRepository;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class EventPublisher {

    @Resource
    private KafkaTemplate<String, String> kafkaTemplate;
    @Resource
    private CategoryEventRepository categoryEventRepository;

    public void publish(Collection<? extends Event<? extends BaseDomain>> events, EventTopics topic){
        Map<Long, List<Event<? extends BaseDomain>>> eventMap = events.stream().collect(Collectors.groupingBy(Event::getDomainId));
        eventMap.forEach((domainId, eventGroup) -> {

            String json;
            try {
                json = SerializeUtil.objectToJson(eventGroup);
            } catch (JsonProcessingException e) {
                throw TactProductException.jsonException(e);
            }

            int retryTimes = 3;
            Set<Long> ids = eventGroup.stream().map(Event::getId).collect(Collectors.toSet());
            while (true){
                try {
                    List<CategoryEvent> currentEvents = categoryEventRepository.findAllById(ids);
                    if (currentEvents.size() != ids.size()){
                        throw TactProductException.eventOperateError("待同步事件数量异常, 待同步[" + ids.size() + "]件, 现有[" + currentEvents.size() + "]件");
                    }
                    currentEvents.forEach(Event::publish);
                    categoryEventRepository.saveAll(currentEvents);
                    break;
                }catch (Exception e){
                    retryTimes --;
                    if (retryTimes < 1){
                        throw TactProductException.eventOperateError("同步事件" + eventGroup + "状态失败");
                    }
                }
            }

            kafkaTemplate.send(TactProductApplication.APPLICATION_NAME + EventTopics.CATEGORY.name(), "" + domainId, json);
        });

    }
}
