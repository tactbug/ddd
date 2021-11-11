package com.tactbug.ddd.product.outbound.repository.jpa.category;

import com.tactbug.ddd.common.entity.Event;
import com.tactbug.ddd.product.assist.exception.TactProductException;
import com.tactbug.ddd.product.domain.category.Category;
import com.tactbug.ddd.product.domain.category.event.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.persistence.LockModeType;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Author tactbug
 * @Email tactbug@Gmail.com
 * @Time 2021/10/5 17:32
 */
@Component
@Slf4j
public class CategoryRepository {

    @Resource
    private CategorySnapshotRepository snapshotRepository;
    @Resource
    private CategoryEventRepository eventRepository;

    public void execute(Collection<CategoryEvent> events){
        Map<Long, List<CategoryEvent>> eventMap = events.stream().collect(Collectors.groupingBy(CategoryEvent::getDomainId));
        eventMap.forEach((domainId, eventsGroup) -> {
            List<CategoryEvent> sortedEvents = eventsGroup.stream().sorted().collect(Collectors.toList());
            Event.checkList(sortedEvents);
            sortedEvents.forEach(e -> {
                switch (e){
                    case CategoryCreated c -> create(c);
                    case CategoryNameUpdated c -> updateName(c);
                    case CategoryRemarkUpdated c -> updateRemark(c);
                    case CategoryParentChanged c -> changeParent(c);
                    case CategoryDeleted c -> delete(c);
                    default -> throw new IllegalStateException("Unexpected value: " + e);
                }
            });
        });
    }

    @Lock(value = LockModeType.PESSIMISTIC_WRITE)
    private void create(CategoryCreated categoryCreated){
        if (isExistsSameName(categoryCreated.getDomainId(), categoryCreated.getCategoryName())){
            throw TactProductException.resourceOperateError("分类[" + categoryCreated.getCategoryName() + "]已经存在");
        }
        Category snapshot = new Category();
        snapshot.replay(Collections.singleton(categoryCreated));
        eventRepository.save(categoryCreated);
        snapshotRepository.save(snapshot);
    }

    private void updateName(CategoryNameUpdated categoryNameUpdated){
        if (!isExists(categoryNameUpdated.getDomainId())){
            throw TactProductException.resourceOperateError("分类[" + categoryNameUpdated.getDomainId() + "]不存在");
        }
        if (isExistsSameName(categoryNameUpdated.getDomainId(), categoryNameUpdated.getCategoryName())){
            throw TactProductException.resourceOperateError("分类[" + categoryNameUpdated.getCategoryName() + "]已经存在");
        }
        eventRepository.save(categoryNameUpdated);
    }

    private void updateRemark(CategoryRemarkUpdated categoryRemarkUpdated){
        if (!isExists(categoryRemarkUpdated.getDomainId())){
            throw TactProductException.resourceOperateError("分类[" + categoryRemarkUpdated.getDomainId() + "]不存在");
        }
        eventRepository.save(categoryRemarkUpdated);
    }

    private void changeParent(CategoryParentChanged categoryParentChanged){
        if (!isExists(categoryParentChanged.getDomainId())){
            throw TactProductException.resourceOperateError("分类[" + categoryParentChanged.getDomainId() + "]不存在");
        }
        eventRepository.save(categoryParentChanged);
    }

    private void delete(CategoryDeleted categoryDeleted){
        if (isDelete(categoryDeleted.getDomainId())){
            return;
        }
        Category category = getOne(categoryDeleted.getDomainId())
                .orElseThrow(() -> TactProductException.resourceOperateError("当前分类[" + categoryDeleted.getDomainId() + "]不存在"));
        category.replay(Collections.singleton(categoryDeleted));
        snapshotRepository.save(category);
        eventRepository.save(categoryDeleted);
    }

    public Optional<Category> getOne(Long id){
        Category category = getSnapshot(id).orElse(new Category());
        List<CategoryEvent> events =
                eventRepository.findAllByDomainIdAndDomainVersionGreaterThanOrderByDomainVersionAsc(id, category.getVersion());
        if (isDelete(id)){
            throw TactProductException.resourceOperateError("分类[" + id + "]已被删除");
        }
        if (!isExists(id)){
            throw TactProductException.resourceOperateError("分类[" + id + "]不存在");
        }
        category.replay(events);
        return Optional.of(category);
    }

    private Optional<Category> getSnapshot(Long id){
        return snapshotRepository.findById(id);
    }

    private boolean isDelete(Long id){
        return eventRepository.existsByDomainIdAndEventType(id, CategoryDeleted.class);
    }

    private boolean isExists(Long id){
        Optional<CategoryEvent> optional = eventRepository.findFirstByDomainIdOrderByDomainVersionDesc(id);
        if (optional.isEmpty()){
            return false;
        }
        CategoryEvent categoryEvent = optional.get();
        if (categoryEvent instanceof CategoryDeleted){
            return false;
        }
        return true;
    }

    private boolean isExistsSameName(Long domainId, String name){
        return false;
    }

}
