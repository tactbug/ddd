package com.tactbug.ddd.product.query.vo;

import com.tactbug.ddd.common.entity.BaseDomain;
import com.tactbug.ddd.common.entity.Event;
import com.tactbug.ddd.product.domain.category.event.CategoryCreated;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.Hibernate;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import java.time.ZonedDateTime;
import java.util.Collection;
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
    private CategoryVo parent;
    @OneToMany
    @ToString.Exclude
    private List<BrandVo> brandList;
    private boolean deleted;

    public void accept(Collection<? extends Event<? extends BaseDomain>> events){

    }

    private void acceptCategoryCreated(CategoryCreated categoryCreated){
        CategoryVo categoryVo = new CategoryVo();
        categoryVo.setId(categoryCreated.getDomainId());
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
