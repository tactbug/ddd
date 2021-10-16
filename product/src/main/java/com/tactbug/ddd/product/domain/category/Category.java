package com.tactbug.ddd.product.domain.category;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.tactbug.ddd.common.entity.BaseDomain;
import com.tactbug.ddd.common.entity.Event;
import com.tactbug.ddd.common.entity.EventType;
import com.tactbug.ddd.common.utils.IdUtil;
import com.tactbug.ddd.common.utils.SerializeUtil;
import com.tactbug.ddd.product.assist.exception.TactProductException;
import com.tactbug.ddd.product.domain.category.command.*;
import com.tactbug.ddd.product.domain.category.event.*;
import com.tactbug.ddd.product.outbound.repository.jpa.category.CategoryRepository;
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

    public static Category replay(Category snapshot, Collection<CategoryEvent> events) {
        if (Objects.isNull(snapshot)){
            snapshot = new Category();
        }
        if (events.isEmpty()){
            return snapshot;
        }
        events.removeIf(Objects::isNull);
        List<CategoryEvent> sortedEvents = events.stream().sorted().collect(Collectors.toList());
        if (sortedEvents.get(0).getDomainVersion() - snapshot.getVersion() != 1){
            throw new IllegalStateException("快照版本[" + snapshot.getVersion() + "]跟溯源版本[" + sortedEvents.get(0).getDomainVersion() + "]不匹配");
        }
        Category category = doReplay(snapshot, sortedEvents);
        category.check();
        return category;
    }

    public CategoryCreated createCategory(IdUtil idUtil, Long operator) {
        check();
        return new CategoryCreated(idUtil.getId(), this, operator);
    }

    public CategoryNameUpdated updateName(IdUtil idUtil, UpdateName updateName) {
        if (!updateName.name().equals(name)){
            name = updateName.name();
            update();
            check();
            return new CategoryNameUpdated(idUtil.getId(), this, updateName.operator());
        }
        return null;
    }

    public CategoryRemarkUpdated updateRemark(IdUtil idUtil, UpdateRemark updateRemark){
        if (!updateRemark.remark().equals(remark)){
            this.remark = updateRemark.remark();
            update();
            check();
            return new CategoryRemarkUpdated(idUtil.getId(), this, updateRemark.operator());
        }
        return null;
    }

    public List<CategoryEvent> changeParent(IdUtil idUtil, ChangeParent changeParent, CategoryRepository categoryRepository) {
        List<CategoryEvent> events = new ArrayList<>();
        if (changeParent.parentId().equals(parentId)){
            return events;
        }
        if (!changeParent.parentId().equals(0L)){
            if (!parentId.equals(0L)){
                Category parent = categoryRepository.getOne(parentId)
                        .orElseThrow(() -> TactProductException.resourceOperateError("父分类[" + changeParent.parentId() + "]不存在"));
                CategoryChildRemoved categoryChildRemoved = parent.removeChild(idUtil, this, changeParent.operator());
                events.add(categoryChildRemoved);
            }
        }
        parentId = changeParent.parentId();
        update();
        check();
        CategoryParentChanged categoryParentChanged = new CategoryParentChanged(idUtil.getId(), this, changeParent.operator());
        events.add(categoryParentChanged);
        return events;
    }

    public List<CategoryEvent> updateChildrenIds(IdUtil eventIdUtil, UpdateChildren updateChildren, CategoryRepository categoryRepository){
        List<CategoryEvent> events = new ArrayList<>();
        if (childrenIds.equals(updateChildren.childrenIds())){
            return events;
        }

        List<Category> children = categoryRepository.getBatch(updateChildren.childrenIds());

        children.forEach(c -> {
            CategoryCommand childChangeParent = new CategoryCommand();
            childChangeParent.setId(c.id);
            childChangeParent.setParentId(this.id);
            childChangeParent.setOperator(updateChildren.operator());
            List<CategoryEvent> childChangeParentEvents = c.changeParent(eventIdUtil, childChangeParent.changeParent(), categoryRepository);
            events.addAll(childChangeParentEvents);
        });
        return events;
    }

    public List<CategoryEvent> update(CategoryCommand categoryCommand, IdUtil eventIdUtil, CategoryRepository categoryRepository){
        List<CategoryEvent> events = new ArrayList<>();
        if (Objects.nonNull(categoryCommand.getName()) && !categoryCommand.getName().equals(name)){
            events.add(updateName(eventIdUtil, categoryCommand.updateName()));
        }
        if (Objects.nonNull(categoryCommand.getRemark()) && !categoryCommand.getRemark().equals(remark)){
            events.add(updateRemark(eventIdUtil, categoryCommand.updateRemark()));
        }
        if (Objects.nonNull(categoryCommand.getParentId()) && !categoryCommand.getParentId().equals(parentId)){
            ChangeParent changeParent = categoryCommand.changeParent();
            events.addAll(changeParent(eventIdUtil,changeParent, categoryRepository));
        }
        if (Objects.nonNull(categoryCommand.getChildrenIds()) && !categoryCommand.getChildrenIds().equals(childrenIds)){
            UpdateChildren updateChildren = categoryCommand.updateChildren();
            events.addAll(updateChildrenIds(eventIdUtil, updateChildren, categoryRepository));
        }
        check();
        return events;
    }

    public CategoryDeleted delete(Long eventId, DeleteCategory deleteCategory){
        update();
        return new CategoryDeleted(eventId, this, deleteCategory.operator());
    }

    private CategoryChildRemoved removeChild(IdUtil idUtil, Category child, Long operator){
        if (!child.parentId.equals(id) || !childrenIds.contains(child.id)){
            throw new IllegalStateException("父子分类不匹配[" + this + "], [" + child +"]");
        }
        childrenIds.remove(child.getId());
        update();
        check();
        return new CategoryChildRemoved(idUtil.getId(), this, child.getId(), operator);
    }

    private static Category doReplay(Category snapshot, List<CategoryEvent> events) {
        for (int i = 0; i < events.size(); i++) {
            Event<Category> current = events.get(i);
            if (i < events.size() - 1){
                Event<Category> next = events.get(i + 1);
                if (next.getDomainVersion() - current.getDomainVersion() != 1){
                    throw new IllegalStateException("溯源版本顺序错误, " +
                            "current[" + current.getId() + "]版本[" + current.getDomainVersion() + "], " +
                            "next[" + next.getId() + "]版本[" + next.getDomainVersion() +"]");
                }
                if (current instanceof CategoryDeleted){
                    throw new IllegalStateException("溯源删除事件[" + current.getId() + "]后不能有后续事件");
                }
            }
            snapshot.eventsReplay(events.get(i));
        }
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
        replayChildren(data);
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

    private void replayChildren(Map<String, Object> data){
        if (data.containsKey("childrenIds")){
            String json = data.get("childrenIds").toString();
            try {
                childrenIds = SerializeUtil.jsonToObject(json, new TypeReference<>() {
                });
            } catch (JsonProcessingException e) {
                throw TactProductException.jsonException(e);
            }
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
