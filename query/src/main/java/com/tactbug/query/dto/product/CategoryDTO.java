package com.tactbug.query.dto.product;

import com.tactbug.ddd.common.entity.BaseDomain;
import com.tactbug.ddd.product.domain.category.Category;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.Hibernate;
import org.springframework.beans.BeanUtils;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
@Entity
public class CategoryDTO extends BaseDomain {
    private String name;
    private String remark;
    @OneToMany
    @ToString.Exclude
    private List<CategoryDTO> children;
    @ManyToOne
    private CategoryDTO parent;
    private boolean deleted;

    public static CategoryDTO generate(Category category){
        CategoryDTO categoryDTO = new CategoryDTO();
        BeanUtils.copyProperties(category, categoryDTO);
        return categoryDTO;
    }

    public void delete(){
        deleted = true;
    }

    public Category convertToCategory(){
        Category category = new Category();
        BeanUtils.copyProperties(this, category);
        category.setChildrenIds(children.stream().map(CategoryDTO::getId).collect(Collectors.toSet()));
        category.setParentId(parent.id);
        return category;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        CategoryDTO categoryDTO = (CategoryDTO) o;
        return id != null && Objects.equals(id, categoryDTO.id);
    }

    @Override
    public int hashCode() {
        return 0;
    }
}
