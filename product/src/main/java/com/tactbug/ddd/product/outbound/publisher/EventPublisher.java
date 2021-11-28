package com.tactbug.ddd.product.outbound.publisher;

import com.tactbug.ddd.common.base.BaseDomain;
import com.tactbug.ddd.common.base.Event;
import com.tactbug.ddd.common.exceptions.TactException;
import com.tactbug.ddd.product.TactProductApplication;
import com.tactbug.ddd.product.domain.brand.event.BrandEvent;
import com.tactbug.ddd.product.domain.category.CategoryEvent;
import com.tactbug.ddd.product.domain.category.event.CategoryCreated;
import com.tactbug.ddd.product.outbound.repository.jpa.brand.BrandEventRepository;
import com.tactbug.ddd.product.outbound.repository.jpa.category.CategoryEventRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

@Component
@Slf4j
public class EventPublisher {

    @Resource
    private KafkaTemplate<String, byte[]> bytesKafkaTemplate;
    @Resource
    private CategoryEventRepository categoryEventRepository;
    @Resource
    private BrandEventRepository brandEventRepository;

    public void publish(Collection<? extends Event<? extends BaseDomain>> events, EventTopics topic){

        // 暂时使用自旋方式判断事务是否提交, 其实最好的方法是使用异步响应式方法获取事务提交结果
        int retry = 10;
        boolean committed = false;
        while (retry > 0){
            committed = committed(events);
            if (committed){
                break;
            }else {
                retry --;
            }
        }


        if (!committed){
            log.error("事件[" + events + "]事务提交异常!");
        }
        Map<Long, List<Event<? extends BaseDomain>>> eventMap = events.stream().collect(Collectors.groupingBy(Event::getDomainId));
        eventMap.forEach((domainId, eventGroup) -> {
            eventGroup
                    .stream()
                    .sorted()
                    .forEach(e -> {
                        byte[] data = serializeToAvro(e);
                        bytesKafkaTemplate.send(TactProductApplication.APPLICATION_NAME + "-" + topic.getName(), "" + domainId, data)
                                .addCallback(
                                        result -> doPublish(e),
                                        ex -> {
                                            log.error("事件发布" + e + "失败", ex);
                                            throw TactException.eventOperateError("事件发布" + e + "失败", ex);
                                        }
                                );
                    });
        });
    }

    private boolean committed(Collection<? extends Event<? extends BaseDomain>> events){
        List<BrandEvent> brandEvents = events.stream()
                .filter(e -> e instanceof BrandEvent)
                .map(e -> (BrandEvent) e)
                .collect(Collectors.toList());
        List<CategoryEvent> categoryEvents = events.stream()
                .filter(e -> e instanceof CategoryEvent)
                .map(e -> (CategoryEvent) e)
                .collect(Collectors.toList());
        return categoryCommitted(categoryEvents) && brandCommitted(brandEvents);
    }

    private boolean categoryCommitted(List<CategoryEvent> categoryEvents){
        if (categoryEvents.isEmpty()){
            return true;
        }
        Set<Long> ids = categoryEvents.stream().map(CategoryEvent::getId).collect(Collectors.toSet());
        return categoryEventRepository.existsAllByIdIn(ids);
    }

    private boolean brandCommitted(List<BrandEvent> brandEvents){
        if (brandEvents.isEmpty()){
            return true;
        }
        Set<Long> ids = brandEvents.stream().map(BrandEvent::getId).collect(Collectors.toSet());
        return brandEventRepository.existsAllByIdIn(ids);
    }

    private byte[] serializeToAvro(Event<? extends BaseDomain> event){
        return switch (event){
            case CategoryEvent c -> categoryDispatch(c);
            default -> throw new IllegalStateException("Unexpected value: " + event);
        };
    }

    private byte[] categoryDispatch(CategoryEvent categoryEvent){
        return switch (categoryEvent){
            case CategoryCreated c -> c.serialize();
            default -> throw new IllegalStateException("Unexpected value: " + categoryEvent);
        };
    }

    private void doPublish(Event<? extends BaseDomain> event){
        switch (event){
            case BrandEvent b -> brandEventPublish(b);
            case CategoryEvent c -> categoryEventPublish(c);
            default -> throw new IllegalStateException("Unexpected value: " + event);
        }
    }

    private void categoryEventPublish(CategoryEvent categoryEvent){
        CategoryEvent currentEvent = categoryEventRepository.findById(categoryEvent.getId())
                .orElseThrow(() -> TactException.eventOperateError("分类事件[" + categoryEvent + "]不存在", null));
        currentEvent.publish();
        categoryEventRepository.save(currentEvent);
    }

    private void brandEventPublish(BrandEvent brandEvent){
        BrandEvent currentEvent = brandEventRepository.findById(brandEvent.getId())
                .orElseThrow(() -> TactException.eventOperateError("品牌事件[" + brandEvent + "]不存在", null));
        currentEvent.publish();
        brandEventRepository.save(currentEvent);
    }

}
