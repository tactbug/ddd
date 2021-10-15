package com.tactbug.ddd.product.outbound.repository.jpa.category;

import com.tactbug.ddd.common.entity.EventType;
import com.tactbug.ddd.common.utils.IdUtil;
import com.tactbug.ddd.product.TactProductApplication;
import com.tactbug.ddd.product.domain.category.Category;
import com.tactbug.ddd.product.domain.category.event.CategoryEvent;
import com.tactbug.ddd.product.domain.category.event.CategoryCreated;
import com.tactbug.ddd.product.assist.exception.TactProductException;
import com.tactbug.ddd.product.domain.category.event.CategoryDeleted;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

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

    public void create(Category category, Long operator){
        if (isExists(category.getId(), category)){
            throw TactProductException.resourceOperateError("分类[" + category + "]已经存在");
        }
        category.check();
        CategoryCreated event = category.createCategory(CATEGORY_EVENT_ID_UTIL.getId(), operator);
        checkEvents(category);
        eventRepository.save(event);
        snapshotRepository.save(category);
    }

    public void update(Category category, List<CategoryEvent> events){
        if (isDelete(category.getId())){
            throw TactProductException.resourceOperateError("分类[" + category + "]已被删除");
        }
        if (!isExists(category.getId(), category)){
            throw TactProductException.resourceOperateError("分类[" + category + "]不存在");
        }
        checkEvents(category);
        events = events.stream().sorted().collect(Collectors.toList());
        eventRepository.saveAll(events);
    }

    public void delete(Category category, CategoryDeleted categoryDeleted){
        if (isDelete(category.getId())){
            return;
        }
        if (!isExists(category.getId(), category)){
            throw TactProductException.resourceOperateError("分类[" + category + "]不存在");
        }
        category.check();
        checkEvents(category);
        snapshotRepository.save(category);
        eventRepository.save(categoryDeleted);
    }

    public Optional<Category> getOne(Long id){
        if (isDelete(id)){
            throw TactProductException.resourceOperateError("分类[" + id + "]已被删除");
        }
        if (!isExists(id, null)){
            throw TactProductException.resourceOperateError("分类[" + id + "]不存在");
        }
        Category snapshot = getSnapshot(id).orElse(new Category());
        List<CategoryEvent> events = eventRepository.findAllByDomainIdAndDomainVersionGreaterThanOrderByDomainVersionAsc(id, snapshot.getVersion());
        Category category = Category.replay(events, snapshot);
        category.check();
        return Optional.of(category);
    }

    public List<Category> getBatch(Collection<Long> ids){
        ids.forEach();
    }

    private Optional<Category> getSnapshot(Long id){
        return snapshotRepository.findById(id);
    }

    private boolean isDelete(Long id){
        return eventRepository.existsByDomainIdAndEventType(id, EventType.DELETED);
    }

    private boolean isExists(Long id, Category category){
        if (!eventRepository.existsByDomainId(id)){
            return false;
        }
        if (eventRepository.existsByDomainIdAndEventType(id, EventType.DELETED)){
            return false;
        }
        if (Objects.nonNull(category) && eventRepository.existsByCategoryNameAndEventType(category.getName(), EventType.DELETED)){
            return false;
        }
        return true;
    }

    private void checkEvents(Category category){
        List<CategoryEvent> exists = eventRepository.findAllByDomainIdAndDomainVersionGreaterThanOrderByDomainVersionAsc(category.getId(), category.getVersion());
        if (!exists.isEmpty()){
            throw TactProductException.resourceOperateError("分类[" + category + "]状态异常");
        }
    }
}
