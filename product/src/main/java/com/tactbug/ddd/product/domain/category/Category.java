package com.tactbug.ddd.product.domain.category;

import com.tactbug.ddd.common.entity.BaseDomain;
import com.tactbug.ddd.common.entity.Event;
import com.tactbug.ddd.common.utils.IdUtil;
import com.tactbug.ddd.product.assist.exception.TactProductException;
import com.tactbug.ddd.product.domain.category.command.*;
import com.tactbug.ddd.product.domain.category.event.*;
import com.tactbug.ddd.product.outbound.repository.jpa.category.CategoryRepository;
import com.tactbug.ddd.product.query.ProductQuery;
import com.tactbug.ddd.product.query.dto.CategoryDTO;
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
        events.removeIf(Objects::isNull);
        List<CategoryEvent> sortedEvents = events.stream().sorted().collect(Collectors.toList());
        Event.checkList(sortedEvents);
        doReplay(sortedEvents);
        check();
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

    public List<CategoryEvent> changeParent(IdUtil idUtil, Category parent, Long operator) {
        List<CategoryEvent> events = new ArrayList<>();
        if (parent.getId().equals(parentId)){
            return events;
        }
        parentId = parent.getId();
        update();
        check();
        CategoryParentChanged categoryParentChanged = new CategoryParentChanged(idUtil.getId(), this, operator);
        events.add(categoryParentChanged);
        return events;
    }

    public List<CategoryEvent> delete(IdUtil idUtil, DeleteCategory deleteCategory, ProductQuery productQuery){
        List<CategoryEvent> events = new ArrayList<>();
        CategoryDTO categoryDTO = productQuery.getCategoryById(id);
        if (Objects.isNull(categoryDTO)){
            return events;
        }
        if (!categoryDTO.getChildren().isEmpty()){
            throw TactProductException.resourceOperateError("当前分类还有子分类, 不能删除");
        }
        if (!categoryDTO.getBrandList().isEmpty()){
            throw TactProductException.resourceOperateError("当前分类下还有绑定品牌, 不能删除");
        }
        update();
        events.add(new CategoryDeleted(idUtil.getId(), this, deleteCategory.operator()));
        return events;
    }

    private void doReplay(List<CategoryEvent> events) {
        for (int i = 0; i < events.size(); i++) {
            Event<Category> current = events.get(i);
            if (current instanceof CategoryDeleted){
                i = events.size();
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
