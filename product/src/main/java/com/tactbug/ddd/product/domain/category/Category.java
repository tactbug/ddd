package com.tactbug.ddd.product.domain.category;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.tactbug.ddd.common.entity.BaseDomain;
import com.tactbug.ddd.common.entity.Event;
import com.tactbug.ddd.common.entity.EventType;
import com.tactbug.ddd.common.utils.IdUtil;
import com.tactbug.ddd.common.utils.SerializeUtil;
import com.tactbug.ddd.product.domain.category.command.*;
import com.tactbug.ddd.product.domain.category.event.*;
import com.tactbug.ddd.product.assist.exception.TactProductException;
import com.tactbug.ddd.product.service.category.CategoryService;
import lombok.Getter;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @Author tactbug
 * @Email tactbug@Gmail.com
 * @Time 2021/9/28 19:15
 */
@Getter
public class Category extends BaseDomain {

    private String name;
    private String remark;
    private Long parentId;

    private Set<Long> childrenIds;
    private Set<Long> brandIds;

    private Category(Long id) {
        super(id);
    }

    public Category() {
        super();
    }

    public static Category generate(Long id, CreateCategory createCategory){
        Category category = new Category(id);
        category.name = createCategory.name();
        category.remark = createCategory.remark();
        category.parentId = createCategory.parentId();
        return category;
    }

    public static Category replay(Collection<CategoryEvent> events, Category snapshot) {
        if (Objects.isNull(snapshot)){
            snapshot = new Category();
        }
        if (events.isEmpty()){
            return snapshot;
        }
        List<Event<Category>> sortedEvents = events.stream().sorted().collect(Collectors.toList());
        if (sortedEvents.get(0).getDomainVersion() - snapshot.getVersion() != 1){
            throw new IllegalStateException("快照版本[" + snapshot.getVersion() + "]跟溯源版本[" + sortedEvents.get(0).getDomainVersion() + "]不匹配");
        }
        return doReplay(snapshot, sortedEvents);
    }

    public CategoryCreated createCategory(Long eventId, Long operator) {
        check();
        return new CategoryCreated(eventId, this, EventType.CREATED, operator);
    }

    public CategoryNameUpdated updateName(Long eventId, UpdateName updateName) {
        this.name = updateName.name();
        update();
        check();
        return new CategoryNameUpdated(eventId, this, EventType.UPDATED, updateName.operator());
    }

    public CategoryRemarkUpdated updateRemark(Long eventId, UpdateRemark updateRemark){
        this.remark = updateRemark.remark();
        update();
        check();
        return new CategoryRemarkUpdated(eventId, this, EventType.UPDATED, updateRemark.operator());
    }

    public CategoryParentChanged changeParent(Long eventId, ChangeParent changeParent) {
        this.parentId = changeParent.parentId();
        update();
        check();
        return new CategoryParentChanged(eventId, this, EventType.UPDATED, changeParent.operator());
    }

    public List<CategoryEvent> updateChildrenIds(UpdateChildren updateChildren, IdUtil eventIdUtil, Collection<Category> children){
        List<CategoryEvent> events = new ArrayList<>();
        childrenIds = new HashSet<>(updateChildren.childrenIds());
        update();
        check();
        events.add(new CategoryChildrenUpdated(eventIdUtil.getId(), this, EventType.UPDATED, updateChildren.operator()));

        children.forEach(c -> {
            CategoryParentChanged categoryParentChanged = c.changeParent(eventIdUtil.getId(), new ChangeParent(c.getId(), this.id, updateChildren.operator()));
            events.add(categoryParentChanged);
        });

        return events;
    }

    public List<CategoryEvent> update(CategoryCommand categoryCommand, IdUtil eventIdUtil){
        List<CategoryEvent> events = new ArrayList<>();
        if (Objects.nonNull(categoryCommand.getName()) && !categoryCommand.getName().equals(name)){
            events.add(updateName(eventIdUtil.getId(), categoryCommand.updateName()));
        }
        if (Objects.nonNull(categoryCommand.getRemark()) && !categoryCommand.getRemark().equals(remark)){
            events.add(updateRemark(eventIdUtil.getId(), categoryCommand.updateRemark()));
        }
        if (Objects.nonNull(categoryCommand.getParentId()) && !categoryCommand.getParentId().equals(parentId)){
            events.add(changeParent(eventIdUtil.getId(), categoryCommand.changeParent()));
        }
        check();
        return events;
    }

    public CategoryDeleted delete(Long eventId, DeleteCategory deleteCategory){
        update();
        return new CategoryDeleted(eventId, this, EventType.DELETED, deleteCategory.operator());
    }

    private static Category doReplay(Category snapshot, List<Event<Category>> events) {
        for (int i = 0; i < events.size(); i++) {
            Event<Category> current = events.get(i);
            if (i < events.size() - 1){
                Event<Category> next = events.get(i + 1);
                if (next.getDomainVersion() - current.getDomainVersion() != 1){
                    throw new IllegalStateException("溯源版本顺序错误, " +
                            "current[" + current.getId() + "]版本[" + current.getDomainVersion() + "], " +
                            "next[" + next.getId() + "]版本[" + next.getDomainVersion() +"]");
                }
                if (current.getEventType().equals(EventType.DELETED)){
                    throw new IllegalStateException("溯源删除事件[" + current.getId() + "]后不能有后续事件");
                }
            }
            snapshot.eventsReplay(events.get(i));
        }
        snapshot.check();
        return snapshot;
    }

    private void eventsReplay(Event<Category> event) {
        super.replay(event);
        String json = event.getData();
        Map<String, Object> data;
        try {
            data = SerializeUtil.jsonToObject(json, new TypeReference<>() {});
        } catch (JsonProcessingException e) {
            throw new TactProductException("json解析异常", e.getMessage());
        }
        replayAttr(data);
    }

    private void replayAttr(Map<String, Object> data){
        replayName(data);
        replayRemark(data);
        replayParentId(data);
    }

    private void replayName(Map<String, Object> data){
        if (data.containsKey("name")){
            this.name = data.get("name").toString();
        }
    }

    private void replayRemark(Map<String, Object> data){
        if (data.containsKey("remark")){
            this.remark = data.get("remark").toString();
        }
    }

    private void replayParentId(Map<String, Object> data){
        if (data.containsKey("parentId")){
            this.parentId = Long.valueOf(data.get("parentId").toString());
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Category category = (Category) o;
        return name.equals(category.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public String toString() {
        return "Category{" +
                "id=" + id +
                ", version=" + version +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                ", name='" + name + '\'' +
                ", remark='" + remark + '\'' +
                ", parentId=" + parentId +
                ", childrenIds=" + childrenIds +
                ", brandIds=" + brandIds +
                '}';
    }

}
