package com.tactbug.ddd.product.outbound.repository.traceability.category;

import com.tactbug.ddd.common.entity.Event;
import com.tactbug.ddd.product.aggregate.category.Category;
import com.tactbug.ddd.product.assist.exception.TactProductException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;
import java.util.Objects;

import static org.springframework.data.relational.core.query.Criteria.where;
import static org.springframework.data.relational.core.query.Query.query;

/**
 * @Author tactbug
 * @Email tactbug@Gmail.com
 * @Time 2021/10/5 18:52
 */
@Repository
@Slf4j
public class CategoryEventRepository{

    @Resource
    private R2dbcEntityTemplate r2dbcEntityTemplate;

    public Mono<Event<Category>> save(Event<Category> event){
        return r2dbcEntityTemplate.select(Event.class)
                .from("category_event")
                .matching(query(
                        where("aggregate_id")
                                .is(event.getAggregateId())
                        ).sort(Sort.by(Sort.Direction.DESC, "aggregate_version"))
                ).first()
                .map(e -> (Event<Category>) e)
                .doOnNext(c -> {
                    if (Objects.nonNull(c) && event.getAggregateVersion() - c.getAggregateVersion() != 1){
                        log.error("分类事件版本冲突, 当前[" + c.getAggregateVersion() + "], 待添加[" + event.getAggregateVersion() + "]");
                        throw TactProductException.resourceOperateError("分类事件版本冲突, 当前[" + c.getAggregateVersion() + "], 待添加[" + event.getAggregateVersion() + "]");
                    }
                })
                .doOnError(e -> {
                    log.error("分类事件保存失败", e);
                    throw TactProductException.resourceOperateError("分类事件[" + event + "]保存失败");
                });
    }

    public Flux<Event<Category>> eventsForReplay(Long aggregateId, Integer version){
        return r2dbcEntityTemplate.select(Event.class)
                .from("category_event")
                .matching(query(
                        where("aggregate_id")
                                .is(aggregateId)
                                .and("aggregate_version").greaterThan(version))
                        .sort(Sort.by(Sort.Direction.DESC, "aggregate_version"))
                ).all()
                .flatMap(event -> {
                    Event<Category> e = (Event<Category>) event;
                    return Mono.just(e);
                });
    }

}
