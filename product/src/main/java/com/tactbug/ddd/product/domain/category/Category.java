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

    public static final Long ROOT_CATEGORY_ID = 0L;
    public static final Category ROOT_CATEGORY = new Category(ROOT_CATEGORY_ID);

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

    public List<CategoryEvent> createCategory(IdUtil idUtil, Category parent, Long operator) {
        List<CategoryEvent> events = new ArrayList<>();
        if (!parent.equals(ROOT_CATEGORY)){
            events.addAll(parent.addChild(idUtil, this, ROOT_CATEGORY, operator));
        }
        events.add(new CategoryCreated(idUtil.getId(), this, operator));
        check();
        return events;
    }

    public List<CategoryEvent> updateName(IdUtil idUtil, String name, Long operator) {
        List<CategoryEvent> events = new ArrayList<>();
        if (!this.name.equals(name)){
            this.name = name;
            update();
            check();
            events.add(new CategoryNameUpdated(idUtil.getId(), this, operator));
        }
        return events;
    }

    public List<CategoryEvent> updateRemark(IdUtil idUtil, String remark, Long operator){
        List<CategoryEvent> events = new ArrayList<>();
        if (!this.remark.equals(remark)){
            this.remark = remark;
            update();
            check();
            events.add(new CategoryRemarkUpdated(idUtil.getId(), this, operator));
        }
        return events;
    }

    public List<CategoryEvent> changeParent(IdUtil idUtil, Category oldParent, Category newParent, Long operator) {
        List<CategoryEvent> events = new ArrayList<>();
        if (newParent.getId().equals(parentId)){
            return events;
        }
        // 原来的父分类移除当前子分类
        if (!oldParent.equals(ROOT_CATEGORY)){
            List<CategoryEvent> childrenRemoved = oldParent.removeChild(idUtil, this, operator);
            events.addAll(childrenRemoved);
        }
        if (!newParent.equals(ROOT_CATEGORY)){
            // 新的父分类添加子分类
            events.addAll(newParent.addChild(idUtil, this, oldParent, operator));
        }
        // 更新父分类
        parentId = newParent.getId();
        update();
        check();
        CategoryParentChanged categoryParentChanged = new CategoryParentChanged(idUtil.getId(), this, operator);
        events.add(categoryParentChanged);
        return events;
    }

    private List<CategoryEvent> addChild(IdUtil idUtil, Category child, Category oldParent, Long operator){
        List<CategoryEvent> events = new ArrayList<>();
        if (childrenIds.contains(child.id)){
            return events;
        }
        events.addAll(child.changeParent(idUtil, oldParent, this, operator));
        childrenIds.add(child.id);
        update();
        events.add(new CategoryChildAdded(idUtil.getId(), this, child.id, operator));
        return events;
    }

    private List<CategoryEvent> removeChild(IdUtil idUtil, Category child, Long operator){
        List<CategoryEvent> events = new ArrayList<>();
        if (!child.parentId.equals(id) && !childrenIds.contains(child.id)){
            return events;
        }
        childrenIds.remove(child.id);
        update();
        check();
        events.add(new CategoryChildRemoved(idUtil.getId(), this, child.getId(), operator));
        return events;
    }

    public List<CategoryEvent> delete(IdUtil idUtil, DeleteCategory deleteCategory, CategoryRepository categoryRepository){
        if (!childrenIds.isEmpty()){
            throw new UnsupportedOperationException("该分类下还有未删除的子分类");
        }
        List<CategoryEvent> events = new ArrayList<>();
        if (!parentId.equals(ROOT_CATEGORY_ID)){
            Category parent = categoryRepository.getOne(parentId)
                    .orElseThrow(() -> TactProductException.resourceOperateError("父分类[" + parentId + "]不存在"));
            events.addAll(parent.removeChild(idUtil, this, deleteCategory.operator()));
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
                case CategoryChildAdded c -> c.replay(snapshot);
                case CategoryChildRemoved c -> c.replay(snapshot);
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
