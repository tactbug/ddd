package com.tactbug.ddd.product.outbound.repository.jpa.category;

import com.tactbug.ddd.common.entity.Event;
import com.tactbug.ddd.common.utils.IdUtil;
import com.tactbug.ddd.product.TactProductApplication;
import com.tactbug.ddd.product.aggregate.category.Category;
import com.tactbug.ddd.product.aggregate.category.CategoryEvent;
import com.tactbug.ddd.product.aggregate.category.event.CategoryCreated;
import com.tactbug.ddd.product.assist.exception.TactProductException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Collection;
import java.util.Optional;

/**
 * @Author tactbug
 * @Email tactbug@Gmail.com
 * @Time 2021/10/5 17:32
 */
@Component
@Slf4j
public class CategoryRepository {

    private static final IdUtil CATEGORY_EVENT_ID_UTIL = IdUtil.getOrGenerate(
            TactProductApplication.APPLICATION_NAME, Category.class, 50000, 5000, 10000
    );

    @Resource
    private CategorySnapshotRepository snapshotRepository;
    @Resource
    private CategoryEventRepository eventRepository;

    @Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED)
    public Optional<Category> getOneById(Long id){
        Category snapshot = getSnapshot(id).orElse(new Category());
        if (snapshot.isDel()){
            return Optional.empty();
        }
        Collection<CategoryEvent> events = eventRepository.findAllByDomainIdAndDomainVersionGreaterThan(snapshot.getId(), snapshot.getVersion());
        Category category = Category.replay(events, snapshot);
        category.check();
        return Optional.of(category);
    }

    @Transactional
    public void create(Category category, Long operator){
        category.check();
        CategoryCreated event = category.createCategory(CATEGORY_EVENT_ID_UTIL.getId(), operator);
        Collection<CategoryEvent> events = eventRepository.findAllByDomainIdAndDomainVersionGreaterThan(category.getId(), category.getVersion());
        if (!events.isEmpty()){
            throw TactProductException.resourceOperateError("分类状态异常[" + event +"]");
        }
        eventRepository.save(event);
        snapshotRepository.save(category);
    }

    @Transactional
    public void update(CategoryEvent categoryEvent){
        eventRepository.save(categoryEvent);
    }

    private Optional<Category> getSnapshot(Long id){
        return snapshotRepository.findById(id);
    }
}
