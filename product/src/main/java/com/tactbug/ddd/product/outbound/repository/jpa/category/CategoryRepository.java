package com.tactbug.ddd.product.outbound.repository.jpa.category;

import com.tactbug.ddd.product.assist.exception.TactProductException;
import com.tactbug.ddd.product.domain.category.Category;
import com.tactbug.ddd.product.domain.category.event.CategoryDeleted;
import com.tactbug.ddd.product.domain.category.event.CategoryEvent;
import com.tactbug.ddd.product.domain.category.event.CategoryNameUpdated;
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

    @Lock(value = LockModeType.PESSIMISTIC_WRITE)
    public void create(Collection<CategoryEvent> events, Category category){
        if (isExistsSameName(category.getId(), category.getName())){
            throw TactProductException.resourceOperateError("分类[" + category + "]已经存在");
        }
        events.removeIf(Objects::isNull);
        checkEvents(events);
        eventRepository.saveAll(events);
        snapshotRepository.save(category);
    }

    public void update(Collection<CategoryEvent> events){
        Set<Long> idSet = events.stream().map(CategoryEvent::getDomainId).collect(Collectors.toSet());
        if (isDeleteAny(idSet)){
            throw TactProductException.resourceOperateError("" + idSet + "存在已被删除的分类");
        }
        if (!isExistsBatch(idSet)){
            throw TactProductException.resourceOperateError("" + idSet + "中有分类不存在");
        }
        checkEvents(events);
        events.stream()
                .collect(Collectors.groupingBy(CategoryEvent::getEventType))
                .forEach((type, eventGroup) -> {
                    if (CategoryNameUpdated.class.equals(type)) {
                        updateName(eventGroup);
                    }
                });
        eventRepository.saveAll(events);
    }

    public void delete(Category category, List<CategoryEvent> events){
        if (isDelete(category.getId())){
            return;
        }
        if (!isExists(category.getId())){
            throw TactProductException.resourceOperateError("分类[" + category + "]不存在");
        }
        category.check();
        checkEvents(events);
        snapshotRepository.save(category);
        eventRepository.saveAll(events);
    }

    public Optional<Category> getOne(Long id){
        if (id.equals(Category.ROOT_CATEGORY_ID)){
            return Optional.of(Category.ROOT_CATEGORY);
        }
        if (isDelete(id)){
            throw TactProductException.resourceOperateError("分类[" + id + "]已被删除");
        }
        if (!isExists(id)){
            throw TactProductException.resourceOperateError("分类[" + id + "]不存在");
        }
        Category snapshot = getSnapshot(id).orElse(new Category());
        List<CategoryEvent> events = eventRepository.findAllByDomainIdAndDomainVersionGreaterThanOrderByDomainVersionAsc(id, snapshot.getVersion());
        Category category = Category.replay(snapshot, events);
        category.check();
        return Optional.of(category);
    }

    public List<Category> getBatch(Collection<Long> ids){
        if (isDeleteAny(ids)){
            throw TactProductException.resourceOperateError("" + ids + "里有已经删除的分类");
        }
        if (!isExistsBatch(ids)){
            throw TactProductException.resourceOperateError("" + ids + "里有不存在的分类");
        }
        Map<Long, List<Category>> snapshotMap = getSnapshotBatch(ids).stream().collect(Collectors.groupingBy(Category::getId));
        Map<Category, List<CategoryEvent>> eventMap = new HashMap<>();
        snapshotMap.forEach((id, snapshot) -> {
            Category currentSnapshot = snapshot.get(0);
            List<CategoryEvent> currentEvents = eventRepository.findAllByDomainIdAndDomainVersionGreaterThanOrderByDomainVersionAsc(id, currentSnapshot.getVersion());
            eventMap.put(currentSnapshot, currentEvents);
        });
        ids.removeAll(snapshotMap.keySet());
        ids.forEach(i -> {
            List<CategoryEvent> currentEvents = eventRepository.findAllByDomainIdAndDomainVersionGreaterThanOrderByDomainVersionAsc(i, 0);
            eventMap.put(new Category(), currentEvents);
        });
        if (eventMap.size() != ids.size()){
            throw TactProductException.resourceOperateError("" + ids + "分类查询异常");
        }
        List<Category> categoryList = new ArrayList<>();
        eventMap.forEach((snapshot, events) -> categoryList.add(Category.replay(snapshot, events)));
        return categoryList;
    }

    @Lock(value = LockModeType.PESSIMISTIC_WRITE)
    public void updateName(Collection<CategoryEvent> categoryEvents){
        Map<Long, List<CategoryEvent>> eventMap = categoryEvents.stream().collect(Collectors.groupingBy(CategoryEvent::getDomainId));
        eventMap.forEach((id, events) -> {
            if (!isExists(id)){
                throw TactProductException.resourceOperateError("分类[" + id + "]不存在");
            }
            Collections.sort(events);
            for (CategoryEvent c :
                    events) {
                if (isExistsSameName(id, c.getCategoryName())){
                    throw TactProductException.resourceOperateError("分类[" + c.getCategoryName() + "]已经存在");
                }
            }
            eventRepository.saveAll(events);
        });
    }

    private Optional<Category> getSnapshot(Long id){
        return snapshotRepository.findById(id);
    }

    private Collection<Category> getSnapshotBatch(Collection<Long> ids){
        return snapshotRepository.findByIds(ids);
    }

    private boolean isDelete(Long id){
        return eventRepository.existsByDomainIdAndEventType(id, CategoryDeleted.class);
    }

    private boolean isDeleteAny(Collection<Long> ids){
        return eventRepository.existsByDomainIdInAndEventType(ids, CategoryDeleted.class);
    }

    private boolean isExists(Long id){
        Optional<CategoryEvent> optional = eventRepository.findFirstByDomainIdOrderByDomainVersionDesc(id);
        if (optional.isEmpty()){
            return false;
        }
        CategoryEvent categoryEvent = optional.get();
        if (categoryEvent.getEventType().equals(CategoryDeleted.class)){
            return false;
        }
        return true;
    }

    private boolean isExistsBatch(Collection<Long> ids){
        List<CategoryEvent> all = eventRepository.findAllByDomainIdIn(ids);
        if (all.stream().map(CategoryEvent::getDomainId).distinct().count() != ids.size()){
            return false;
        }
        Map<Long, List<CategoryEvent>> map = all.stream().collect(Collectors.groupingBy(CategoryEvent::getDomainId));
        for (List<CategoryEvent> eventGroup :
                map.values()) {
            Collections.sort(eventGroup);
            CategoryEvent lastEvent = eventGroup.get(eventGroup.size() - 1);
            if (lastEvent.getEventType().equals(CategoryDeleted.class)){
                return false;
            }
        }
        return true;
    }

    private boolean isExistsSameName(Long domainId, String name){
        Collection<CategoryEvent> allEvents = eventRepository.findAllByCategoryName(name);
        if (allEvents.isEmpty()){
            return false;
        }
        Set<Long> beCheckedIds = allEvents.stream().map(CategoryEvent::getDomainId).filter(id -> !id.equals(domainId)).collect(Collectors.toSet());
        Map<Long, List<CategoryEvent>> eventMap = eventRepository.findAllByDomainIdIn(beCheckedIds).stream().collect(Collectors.groupingBy(CategoryEvent::getDomainId));

        for (Map.Entry<Long, List<CategoryEvent>> entry:
             eventMap.entrySet()) {
            List<CategoryEvent> sortedList = entry.getValue().stream().sorted().collect(Collectors.toList());
            CategoryEvent lastEvent = sortedList.get(sortedList.size() - 1);
            if (!lastEvent.getEventType().equals(CategoryDeleted.class) && lastEvent.getCategoryName().equals(name)){
                return true;
            }
        }
        return false;
    }

    private void checkEvents(Collection<CategoryEvent> events){
        Map<Long, List<CategoryEvent>> eventMap = events.stream().collect(Collectors.groupingBy(CategoryEvent::getDomainId));
        eventMap.forEach((domainId, eventList) -> {
            List<CategoryEvent> sortedList = eventList.stream().sorted().collect(Collectors.toList());
            List<CategoryEvent> exists = eventRepository.findAllByDomainIdAndDomainVersionGreaterThanOrderByDomainVersionAsc(domainId, sortedList.get(0).getDomainVersion() - 1);
            if (!exists.isEmpty()){
                throw TactProductException.resourceOperateError("分类[" + domainId + "]状态已经改变, 请重新确认");
            }
        });
    }
}
