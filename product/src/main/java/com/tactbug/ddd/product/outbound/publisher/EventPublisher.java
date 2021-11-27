package com.tactbug.ddd.product.outbound.publisher;

import com.tactbug.ddd.common.entity.BaseDomain;
import com.tactbug.ddd.common.entity.Event;
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
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
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
        int retry = 10;
        while (retry > 0){
            Optional<CategoryEvent> optional = categoryEventRepository.findById(categoryEvent.getId());
            if (optional.isPresent()){
                CategoryEvent currentEvent = optional.get();
                currentEvent.publish();
                categoryEventRepository.save(currentEvent);
                return;
            }
            retry --;
        }
        log.error("事件[" + categoryEvent + "]提交异常, 请检查");
    }

    private void brandEventPublish(BrandEvent brandEvent){
        BrandEvent currentEvent = brandEventRepository.findById(brandEvent.getId())
                .orElseThrow(() -> TactException.eventOperateError("品牌事件[" + brandEvent + "]不存在", null));
        currentEvent.publish();
        brandEventRepository.save(currentEvent);
    }

}
