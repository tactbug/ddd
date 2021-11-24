package com.tactbug.ddd.product.domain.category;

import com.tactbug.ddd.common.entity.BaseDomain;
import com.tactbug.ddd.common.entity.Event;
import com.tactbug.ddd.common.utils.IdUtil;
import com.tactbug.ddd.common.exceptions.TactException;
import com.tactbug.ddd.product.domain.category.command.*;
import com.tactbug.ddd.product.domain.category.event.*;
import com.tactbug.ddd.product.query.ProductQuery;
import com.tactbug.ddd.product.query.vo.CategoryVo;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @Author tactbug
 * @Email tactbug@Gmail.com
 * @Time 2021/9/28 19:15
 */
@Data
@Slf4j
public class Category extends BaseDomain {

    private String name;
    private String remark;
    private Long parentId;
    private boolean deleted;

    public static final Long ROOT_CATEGORY_ID = 0L;
    public static final Category ROOT_CATEGORY = new Category(ROOT_CATEGORY_ID);

    public Category() {
        super();
    }

    private Category(Long id) {
        super(id);
    }

    public static Category generate(IdUtil idUtil, CreateCategory createCategory, List<CategoryEvent> events){
        Category category = new Category(idUtil.getId());
        category.name = createCategory.name();
        category.remark = createCategory.remark();
        category.parentId = createCategory.parentId();
        category.update();
        category.check();
        category.setDeleted(false);
        events.add(new CategoryCreated(idUtil.getId(), category, createCategory.operator()));
        return category;
    }

    public void replay(Collection<CategoryEvent> events) {
        List<CategoryEvent> enableEvents = events.stream().filter(Objects::nonNull).collect(Collectors.toList());
        List<CategoryEvent> sortedEvents = enableEvents.stream().sorted().collect(Collectors.toList());
        Event.checkList(sortedEvents);
        doReplay(sortedEvents);
        check();
    }

    public List<CategoryEvent> update(IdUtil idUtil, CategoryCommand categoryCommand){
        List<CategoryEvent> events = new ArrayList<>();

        CategoryUpdateName categoryUpdateName = categoryCommand.updateName();
        events.addAll(updateName(idUtil, categoryUpdateName.name(), categoryUpdateName.operator()));

        CategoryUpdateRemark categoryUpdateRemark = categoryCommand.updateRemark();
        events.addAll(updateRemark(idUtil, categoryUpdateRemark.remark(), categoryUpdateRemark.operator()));

        CategoryChangeParent categoryChangeParent = categoryCommand.changeParent();
        events.addAll(changeParent(idUtil, categoryChangeParent.parentId(), categoryChangeParent.operator()));

        return events;
    }

    private List<CategoryEvent> updateName(IdUtil idUtil, String name, Long operator) {
        List<CategoryEvent> events = new ArrayList<>();
        if (!this.name.equals(name)){
            this.name = name;
            update();
            check();
            events.add(new CategoryNameUpdated(idUtil.getId(), this, operator));
        }
        return events;
    }

    private List<CategoryEvent> updateRemark(IdUtil idUtil, String remark, Long operator){
        List<CategoryEvent> events = new ArrayList<>();
        if (!this.remark.equals(remark)){
            this.remark = remark;
            update();
            check();
            events.add(new CategoryRemarkUpdated(idUtil.getId(), this, operator));
        }
        return events;
    }

    private List<CategoryEvent> changeParent(IdUtil idUtil, Long parentId, Long operator) {
        List<CategoryEvent> events = new ArrayList<>();
        if (parentId.equals(this.parentId)){
            return events;
        }
        this.parentId = parentId;
        update();
        check();
        CategoryParentChanged categoryParentChanged = new CategoryParentChanged(idUtil.getId(), this, operator);
        events.add(categoryParentChanged);
        return events;
    }

    public List<CategoryEvent> delete(IdUtil idUtil, DeleteCategory deleteCategory, ProductQuery productQuery){
        List<CategoryEvent> events = new ArrayList<>();
        CategoryVo categoryVo = productQuery.getCategoryById(id);
        if (Objects.isNull(categoryVo)){
            return events;
        }
        if (!categoryVo.getChildren().isEmpty()){
            throw TactException.resourceOperateError("当前分类还有子分类, 不能删除", null);
        }
        if (!categoryVo.getBrandList().isEmpty()){
            throw TactException.resourceOperateError("当前分类下还有绑定品牌, 不能删除", null);
        }
        update();
        events.add(new CategoryDeleted(idUtil.getId(), this, deleteCategory.operator()));
        return events;
    }

    private void doReplay(List<CategoryEvent> events) {
        for (int i = 0; i < events.size(); i++) {
            Event<Category> current = events.get(i);
            if (current instanceof CategoryDeleted){
                i = events.size() - 1;
            }
            switch (events.get(i)){
                case CategoryCreated c -> c.replay(this);
                case CategoryNameUpdated c -> c.replay(this);
                case CategoryRemarkUpdated c -> c.replay(this);
                case CategoryParentChanged c -> c.replay(this);
                case CategoryDeleted c -> c.replay(this);
                default -> throw new IllegalStateException("Unexpected value: " + events.get(i));
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
                '}';
    }

}
