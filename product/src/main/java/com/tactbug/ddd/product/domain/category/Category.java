package com.tactbug.ddd.product.domain.category;

import com.tactbug.ddd.common.entity.BaseDomain;
import com.tactbug.ddd.common.entity.Event;
import com.tactbug.ddd.common.utils.IdUtil;
import com.tactbug.ddd.product.assist.exception.TactProductException;
import com.tactbug.ddd.product.domain.category.command.*;
import com.tactbug.ddd.product.domain.category.event.*;
import com.tactbug.ddd.product.outbound.repository.jpa.category.CategoryRepository;
import lombok.Data;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @Author tactbug
 * @Email tactbug@Gmail.com
 * @Time 2021/9/28 19:15
 */
@Data
public class Category extends BaseDomain {

    private String name;
    private String remark;
    private Long parentId;

    private Set<Long> childrenIds = new HashSet<>();
    private Set<Long> brandIds = new HashSet<>();

    private static final Long ROOT_CATEGORY = 0L;

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

    public List<CategoryEvent> createCategory(IdUtil idUtil, CategoryRepository categoryRepository, Long operator) {
        List<CategoryEvent> events = new ArrayList<>();
        if (!parentId.equals(ROOT_CATEGORY)){
            Category parent = categoryRepository.getOne(parentId)
                    .orElseThrow(() -> TactProductException.resourceOperateError("当前父分类[" + parentId + "]不存在"));
            events.addAll(parent.addChildren(idUtil, Collections.singletonList(this), operator, categoryRepository));
        }
        events.add(new CategoryCreated(idUtil.getId(), this, operator));
        check();
        return events;
    }

    public CategoryNameUpdated updateName(IdUtil idUtil, CategoryUpdateName categoryUpdateName) {
        if (!categoryUpdateName.name().equals(name)){
            name = categoryUpdateName.name();
            update();
            check();
            return new CategoryNameUpdated(idUtil.getId(), this, categoryUpdateName.operator());
        }
        return null;
    }

    public CategoryRemarkUpdated updateRemark(IdUtil idUtil, CategoryUpdateRemark categoryUpdateRemark){
        if (!categoryUpdateRemark.remark().equals(remark)){
            this.remark = categoryUpdateRemark.remark();
            update();
            check();
            return new CategoryRemarkUpdated(idUtil.getId(), this, categoryUpdateRemark.operator());
        }
        return null;
    }

    public List<CategoryEvent> changeParent(IdUtil idUtil, CategoryChangeParent categoryChangeParent, CategoryRepository categoryRepository) {
        List<CategoryEvent> events = new ArrayList<>();
        if (categoryChangeParent.parentId().equals(parentId)){
            return events;
        }
        // 原来的父分类移除当前子分类
        if (!parentId.equals(ROOT_CATEGORY)){
            Category currentParent = categoryRepository.getOne(parentId)
                    .orElseThrow(() -> TactProductException.resourceOperateError("父分类[" + categoryChangeParent.parentId() + "]不存在"));
            List<CategoryEvent> childrenRemoved = currentParent.removeChildren(idUtil, Collections.singleton(this), categoryChangeParent.operator());
            events.addAll(childrenRemoved);
        }
        parentId = categoryChangeParent.parentId();
        if (!categoryChangeParent.parentId().equals(ROOT_CATEGORY)){
            // 新的父分类添加子分类
            Category parent = categoryRepository.getOne(categoryChangeParent.parentId())
                    .orElseThrow(() -> TactProductException.resourceOperateError("父分类[" + categoryChangeParent.parentId() + "]不存在"));
            events.addAll(
                    parent.addChildren(
                            idUtil,
                            Arrays.asList(this),
                            categoryChangeParent.operator(),
                            categoryRepository
                    )
            );
        }
        update();
        check();
        CategoryParentChanged categoryParentChanged = new CategoryParentChanged(idUtil.getId(), this, categoryChangeParent.operator());
        events.add(categoryParentChanged);
        return events;
    }

    private List<CategoryEvent> addChildren(IdUtil idUtil, Collection<Category> children, Long operator, CategoryRepository categoryRepository){
        List<CategoryEvent> events = new ArrayList<>();
        if (!childrenIds.isEmpty()){
            children.removeIf(c -> childrenIds.contains(c.getId()));
        }
        if (!children.isEmpty()){
            children.forEach(c -> {
                CategoryChangeParent categoryChangeParent = new CategoryChangeParent(c.id, id, operator);
                events.addAll(c.changeParent(idUtil, categoryChangeParent, categoryRepository));
                childrenIds.add(c.id);
                update();
                events.add(new CategoryChildrenAdded(idUtil.getId(), this, Collections.singletonList(c.id), operator));
            });
        }
        return events;
    }

    private List<CategoryEvent> removeChildren(IdUtil idUtil, Collection<Category> children, Long operator){
        List<CategoryEvent> events = new ArrayList<>();
        if (children.isEmpty()){
            return events;
        }
        if (children.stream().map(Category::getParentId).distinct().count() != 1){
            throw new IllegalStateException("子分类状态异常");
        }
        List<Long> removeChildrenIds = new ArrayList<>();
        children.forEach(c -> {
            if (!c.parentId.equals(id) || !childrenIds.contains(c.id)){
                throw new IllegalStateException("父子分类不匹配[" + this + "], [" + c +"]");
            }
            childrenIds.remove(c.id);
            removeChildrenIds.add(c.id);
        });
        update();
        check();
        return Collections.singletonList(new CategoryChildrenRemoved(idUtil.getId(), this, removeChildrenIds, operator));
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
            CategoryChangeParent categoryChangeParent = categoryCommand.changeParent();
            events.addAll(changeParent(eventIdUtil, categoryChangeParent, categoryRepository));
        }
        check();
        return events;
    }

    public List<CategoryEvent> delete(IdUtil idUtil, DeleteCategory deleteCategory, CategoryRepository categoryRepository){
        if (!childrenIds.isEmpty()){
            throw new UnsupportedOperationException("该分类下还有未删除的子分类");
        }
        List<CategoryEvent> events = new ArrayList<>();
        if (!parentId.equals(ROOT_CATEGORY)){
            Category parent = categoryRepository.getOne(parentId)
                    .orElseThrow(() -> TactProductException.resourceOperateError("父分类[" + parentId + "]不存在"));
            events.addAll(parent.removeChildren(idUtil, Collections.singleton(this), deleteCategory.operator()));
        }
        update();
        events.add(new CategoryDeleted(idUtil.getId(), this, deleteCategory.operator()));
        return events;
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
            switch (events.get(i)){
                case CategoryCreated c -> c.replay(snapshot);
                case CategoryChildrenAdded c -> c.replay(snapshot);
                case CategoryChildrenRemoved c -> c.replay(snapshot);
                case CategoryNameUpdated c -> c.replay(snapshot);
                case CategoryRemarkUpdated c -> c.replay(snapshot);
                case CategoryParentChanged c -> c.replay(snapshot);
                default -> throw new IllegalStateException("Unexpected value: " + events.get(i));
            }
        }
        return snapshot;
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
