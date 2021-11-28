package com.tactbug.ddd.product.query.vo;

import com.fasterxml.jackson.core.type.TypeReference;
import com.tactbug.ddd.common.base.BaseDomain;
import com.tactbug.ddd.common.utils.SerializeUtil;
import com.tactbug.ddd.common.exceptions.TactException;
import com.tactbug.ddd.product.domain.category.Category;
import com.tactbug.ddd.product.domain.category.event.CategoryNameUpdated;
import com.tactbug.ddd.product.domain.category.event.CategoryParentChanged;
import com.tactbug.ddd.product.domain.category.event.CategoryRemarkUpdated;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.beans.BeanUtils;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
@Entity
public class CategoryVo extends BaseDomain{

    private String name;
    private String remark;
    @OneToMany
    @ToString.Exclude
    private List<CategoryVo> children;
    @ManyToOne
    @ToString.Exclude
    private CategoryVo parent;
    @OneToMany
    @ToString.Exclude
    private List<BrandVo> brandList;
    private boolean deleted;

    public void initBase(Category category){
        BeanUtils.copyProperties(category, this);
    }

    public void acceptNameUpdated(CategoryNameUpdated categoryNameUpdated){
        try {
            HashMap<String, Object> dataMap = SerializeUtil.jsonToObject(categoryNameUpdated.getData(), new TypeReference<>() {
            });
            this.setName(dataMap.get("name").toString());
            this.setVersion(categoryNameUpdated.getDomainVersion());
            this.setUpdateTime(categoryNameUpdated.getCreateTime());
        } catch (Exception e) {
            throw TactException.replayError("[" + categoryNameUpdated.getData() + "]视图基础信息构建异常", e);
        }
    }

    public void acceptRemarkUpdated(CategoryRemarkUpdated categoryRemarkUpdated){
        try {
            HashMap<String, Object> dataMap = SerializeUtil.jsonToObject(categoryRemarkUpdated.getData(), new TypeReference<>() {
            });
            this.setName(dataMap.get("remark").toString());
            this.setVersion(categoryRemarkUpdated.getDomainVersion());
            this.setUpdateTime(categoryRemarkUpdated.getCreateTime());
        } catch (Exception e) {
            throw TactException.replayError("[" + categoryRemarkUpdated.getData() + "]视图基础信息构建异常", e);
        }
    }

    public void acceptParentChanged(CategoryParentChanged categoryParentChanged, CategoryVo parent){
        this.setParent(parent);
        this.setVersion(categoryParentChanged.getDomainVersion());
        this.setUpdateTime(categoryParentChanged.getCreateTime());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CategoryVo that = (CategoryVo) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
