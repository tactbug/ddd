package com.tactbug.ddd.product.outbound.repository.traceability.category;

import com.tactbug.ddd.common.entity.Event;
import com.tactbug.ddd.common.utils.IdUtil;
import com.tactbug.ddd.product.TactProductApplication;
import com.tactbug.ddd.product.aggregate.category.Category;
import com.tactbug.ddd.product.assist.exception.TactProductException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

/**
 * @Author tactbug
 * @Email tactbug@Gmail.com
 * @Time 2021/10/5 17:32
 */
@Component
@EnableR2dbcRepositories
@Slf4j
public class CategoryTraceabilityRepository {

    private static final IdUtil CATEGORY_EVENT_ID_UTIL = IdUtil.getOrGenerate(
            TactProductApplication.APPLICATION_NAME, CategoryTraceabilityRepository.class, 50000, 5000, 10000
    );

    @Resource
    private CategorySnapshotRepository snapshotRepository;
    @Resource
    private CategoryEventRepository eventRepository;

    public Mono<Category> getOneById(Long id){
        return getSnapshot(id)
                .doOnNext(c -> eventRepository.eventsForReplay(id, c.getVersion())
                        .doOnNext(Event::check)
                        .collectList()
                        .map(events -> Category.replay(events, c)))
                .switchIfEmpty(eventRepository.eventsForReplay(id, 0)
                            .doOnNext(Event::check)
                            .collectList()
                            .map(events -> Category.replay(events, Category.empty()))
                ).doOnError(e -> {
                    log.error("查询分类失败", e);
                    throw TactProductException.resourceOperateError("分类[" + id + "]查询失败");
                });
    }

    public Mono<Category> create(Category category, Long operator){
        return snapshotRepository.save(category)
                .doOnNext(c -> eventRepository.save(c.createCategory(CATEGORY_EVENT_ID_UTIL.getId(), operator)))
                .doOnError(e -> {
                    log.error("分类创建失败", e);
                    throw TactProductException.resourceOperateError("分类创建失败|-" + e.getMessage());
                });
    }

    private Mono<Category> getSnapshot(Long id){
        return snapshotRepository.findById(id)
                .doOnNext(Category::check)
                .doOnError(e -> {
                    log.error("分类快照查询异常", e);
                    throw TactProductException.resourceOperateError("分类[" + id + "]快照查询异常");
                });
    }
}
