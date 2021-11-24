package com.tactbug.ddd.product.outbound.publisher;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.tactbug.ddd.common.entity.BaseDomain;
import com.tactbug.ddd.common.entity.Event;
import com.tactbug.ddd.common.utils.SerializeUtil;
import com.tactbug.ddd.product.TactProductApplication;
import com.tactbug.ddd.common.exceptions.TactException;
import com.tactbug.ddd.product.domain.brand.event.BrandEvent;
import com.tactbug.ddd.product.domain.category.CategoryEvent;
import com.tactbug.ddd.product.outbound.repository.jpa.brand.BrandEventRepository;
import com.tactbug.ddd.product.outbound.repository.jpa.category.CategoryEventRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;

import javax.annotation.Resource;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@Slf4j
public class EventPublisher {

    @Resource
    private KafkaTemplate<String, String> kafkaTemplate;
    @Resource
    private CategoryEventRepository categoryEventRepository;
    @Resource
    private BrandEventRepository brandEventRepository;

    public void publish(Collection<? extends Event<? extends BaseDomain>> events, EventTopics topic){
        Map<Long, List<Event<? extends BaseDomain>>> eventMap = events.stream().collect(Collectors.groupingBy(Event::getDomainId));
        eventMap.forEach((domainId, eventGroup) -> {
            String json;
            try {
                json = SerializeUtil.objectToJson(eventGroup);
            } catch (JsonProcessingException e) {
                throw TactException.serializeOperateError(eventGroup.toString(), e);
            }
            ListenableFuture<SendResult<String, String>> send = kafkaTemplate.send(TactProductApplication.APPLICATION_NAME + "-" + topic.getName(), "" + domainId, json);
            send.addCallback(result -> doPublish(events),
                    ex ->
                    {
                        log.error("事件发布" + eventGroup.toString() + "失败", ex);
                        throw TactException.eventOperateError("事件发布" + eventGroup + "失败", ex);
                    }
            );
        });
    }

    private void doPublish(Collection<? extends Event<? extends BaseDomain>> events){
        Map<Long, List<BrandEvent>> brandEventMap = events.stream()
                .filter(e -> e instanceof BrandEvent)
                .map(e -> (BrandEvent) e)
                .collect(Collectors.groupingBy(BrandEvent::getDomainId));
        Map<Long, List<CategoryEvent>> categoryEventMap = events.stream()
                .filter(e -> e instanceof CategoryEvent)
                .map(e -> (CategoryEvent) e)
                .collect(Collectors.groupingBy(CategoryEvent::getDomainId));
        brandEventPublish(brandEventMap);
        categoryEventPublish(categoryEventMap);
    }

    private void categoryEventPublish(Map<Long, List<CategoryEvent>> categoryEventMap){
        if (categoryEventMap.isEmpty()){
            return;
        }
        categoryEventMap.forEach((domainId, eventGroup) -> {
            List<Long> ids = eventGroup.stream().map(CategoryEvent::getId).collect(Collectors.toList());
            List<CategoryEvent> currentEvents = categoryEventRepository.findAllById(ids);
            if (currentEvents.size() != ids.size()){
                throw TactException.eventOperateError("商品分类待发布事件数量异常, 待发布[" + ids.size() + "]件, 现有[" + currentEvents.size() + "]件", null);
            }
            currentEvents.forEach(Event::publish);
            categoryEventRepository.saveAll(currentEvents);
        });
    }

    private void brandEventPublish(Map<Long, List<BrandEvent>> brandEventMap){
        if (brandEventMap.isEmpty()){
            return;
        }
        brandEventMap.forEach((domainId, eventGroup) -> {
            List<Long> ids = eventGroup.stream().map(BrandEvent::getId).collect(Collectors.toList());
            List<BrandEvent> currentEvents = brandEventRepository.findAllById(ids);
            if (currentEvents.size() != ids.size()){
                throw TactException.eventOperateError("品牌待发布事件数量异常, 待发布[" + ids.size() + "]件, 现有[" + currentEvents.size() + "]件", null);
            }
            currentEvents.forEach(Event::publish);
            brandEventRepository.saveAll(currentEvents);
        });
    }

}
