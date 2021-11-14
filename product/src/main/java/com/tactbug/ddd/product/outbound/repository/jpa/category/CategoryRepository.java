package com.tactbug.ddd.product.outbound.repository.jpa.category;

import com.tactbug.ddd.common.entity.Event;
import com.tactbug.ddd.product.assist.exception.TactProductException;
import com.tactbug.ddd.product.domain.category.Category;
import com.tactbug.ddd.product.domain.category.CategoryEvent;
import com.tactbug.ddd.product.domain.category.CategorySituation;
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
    @Resource
    private CategorySituationRepository situationRepository;

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
        Category snapshot = new Category();
        snapshot.replay(Collections.singleton(categoryCreated));
        if (isExistsSameName(snapshot.getName())){
            throw TactProductException.resourceOperateError("分类[" + snapshot.getName() + "]已经存在");
        }
        eventRepository.save(categoryCreated);
        snapshotRepository.save(snapshot);
        syncSituation(categoryCreated, snapshot.getName());
    }

    private void updateName(CategoryNameUpdated categoryNameUpdated){
        checkExists(categoryNameUpdated.getDomainId());
        Category category = getOne(categoryNameUpdated.getDomainId());
        category.replay(Collections.singleton(categoryNameUpdated));
        if (isExistsSameName(category.getName())){
            throw TactProductException.resourceOperateError("分类[" + category.getName() + "]已经存在");
        }
        eventRepository.save(categoryNameUpdated);
        syncSituation(categoryNameUpdated, category.getName());
    }

    private void updateRemark(CategoryRemarkUpdated categoryRemarkUpdated){
        checkExists(categoryRemarkUpdated.getDomainId());
        Category category = getOne(categoryRemarkUpdated.getDomainId());
        category.replay(Collections.singleton(categoryRemarkUpdated));
        eventRepository.save(categoryRemarkUpdated);
        syncSituation(categoryRemarkUpdated, category.getName());
    }

    private void changeParent(CategoryParentChanged categoryParentChanged){
        checkExists(categoryParentChanged.getDomainId());
        Category category = getOne(categoryParentChanged.getDomainId());
        category.replay(Collections.singleton(categoryParentChanged));
        eventRepository.save(categoryParentChanged);
        syncSituation(categoryParentChanged, category.getName());
    }

    private void delete(CategoryDeleted categoryDeleted){
        if (isDelete(categoryDeleted.getDomainId())){
            return;
        }
        Category category = getOne(categoryDeleted.getDomainId());
        category.replay(Collections.singleton(categoryDeleted));
        snapshotRepository.delete(category.getId());
        eventRepository.save(categoryDeleted);
        syncSituation(categoryDeleted, category.getName());
    }

    public Category getOne(Long id){
        if (!isExists(id)){
            throw TactProductException.resourceOperateError("分类[" + id + "]不存在或已删除");
        }
        Category category = getSnapshot(id).orElse(new Category());
        List<CategoryEvent> events =
                eventRepository.findAllByDomainIdAndDomainVersionGreaterThanOrderByDomainVersionAsc(id, category.getVersion());
        category.replay(events);
        return category;
    }

    private Optional<Category> getSnapshot(Long id){
        return snapshotRepository.findById(id);
    }

    private void checkExists(Long id){
        if (isDelete(id)){
            throw TactProductException.resourceOperateError("分类[" + id + "]不存在或已删除");
        }
    }

    private boolean isDelete(Long id){
        Optional<CategorySituation> optional = situationRepository.findById(id);
        return optional.isPresent() && optional.get().isDeleted();
    }

    private boolean isExists(Long id){
        Optional<CategorySituation> optional = situationRepository.findById(id);
        return optional.isPresent() && !optional.get().isDeleted();
    }

    private boolean isExistsSameName(String name){
        return situationRepository.existsByCategoryNameAndDeletedIsFalse(name);
    }

    private void syncSituation(CategoryEvent event, String categoryName){
        if (event instanceof CategoryCreated){
            CategorySituation categorySituation = CategorySituation.generate(event, categoryName);
            situationRepository.save(categorySituation);
            return;
        }
        CategorySituation categorySituation = situationRepository.findById(event.getDomainId())
                .orElseThrow(() -> TactProductException.resourceOperateError("分类[" + event.getDomainId() + "]状态错误"));
        if (event instanceof CategoryDeleted){
            categorySituation.delete(event);
        }else {
            categorySituation.update(event, categoryName);
        }
        situationRepository.save(categorySituation);
    }

}
