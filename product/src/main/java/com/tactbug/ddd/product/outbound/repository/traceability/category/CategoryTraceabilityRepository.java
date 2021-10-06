package com.tactbug.ddd.product.outbound.repository.traceability.category;

import com.tactbug.ddd.common.entity.Event;
import com.tactbug.ddd.common.utils.IdUtil;
import com.tactbug.ddd.product.TactProductApplication;
import com.tactbug.ddd.product.aggregate.category.Category;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * @Author tactbug
 * @Email tactbug@Gmail.com
 * @Time 2021/10/5 17:32
 */
@Component
@EnableR2dbcRepositories
public class CategoryTraceabilityRepository {

    private static final IdUtil CATEGORY_EVENT_ID_UTIL = IdUtil.getOrGenerate(
            TactProductApplication.APPLICATION_NAME, CategoryTraceabilityRepository.class, 50000, 5000, 10000
    );

    @Resource
    private CategorySnapshotRepository snapshotRepository;
    @Resource
    private CategoryEventRepository eventRepository;

    public Optional<Category> getOneById(Long id){
        Category category = null;
        int startVersion = 0;
        Optional<Category> snapshot = snapshot(id);
        if (snapshot.isPresent()){
            category = snapshot.get();
            startVersion = category.getVersion();
        }
        List<Event<Category>> events = eventRepository.findAllByAggregateIdAndVersionIsAfter(id, startVersion)
                .doOnNext(Event::check)
                .collectList()
                .block();
        Category replay = Category.replay(events, category);
        return replay.empty() ? Optional.empty() : Optional.of(replay);
    }

    @Transactional
    public void create(Category category, Long operator){
        snapshotRepository.save(category);
        Event<Category> event = category.createCategory(CATEGORY_EVENT_ID_UTIL.getId(), operator);
        eventRepository.save(event);
    }

    private Optional<Category> snapshot(Long id){
        Category category = snapshotRepository.findById(id)
                .doOnNext(Category::check)
                .block();
        return Objects.isNull(category) ? Optional.empty() : Optional.of(category);
    }
}
