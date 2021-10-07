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
        Category category = null;
        int startVersion = 0;
        Optional<Category> snapshot = snapshot(id);
        if (snapshot.isPresent()){
            category = snapshot.get();
            startVersion = category.getVersion();
        }
        Category finalCategory = category;
        return eventRepository.findAllByAggregateIdAndVersionIsAfter(id, startVersion)
                .doOnNext(Event::check)
                .collectList()
                .map(events -> Category.replay(events, finalCategory));
    }

    public Mono<Category> create(Category category, Long operator){
        return snapshotRepository.save(category)
                .doOnError(e -> {
                    log.error("快照[" + category + "]保存失败", e);
                    throw TactProductException.resourceOperateError("快照[" + category + "]保存失败");
                })
                .doOnNext(c -> eventRepository.save(c.createCategory(CATEGORY_EVENT_ID_UTIL.getId(), operator)));
    }

    private Optional<Category> snapshot(Long id){
        Category category = snapshotRepository.findById(id)
                .doOnNext(Category::check)
                .block();
        return Objects.isNull(category) ? Optional.empty() : Optional.of(category);
    }
}
