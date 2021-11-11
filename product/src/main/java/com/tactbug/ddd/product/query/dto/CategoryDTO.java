package com.tactbug.ddd.product.query.dto;

import com.tactbug.ddd.product.domain.category.Category;
import lombok.*;
import org.hibernate.Hibernate;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Objects;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
@Entity
public class CategoryDTO {
    @Id
    private Long id;
    private Integer version;
    private String name;
    private String remark;
    @OneToMany
    @ToString.Exclude
    private List<CategoryDTO> children;
    @ManyToOne
    private CategoryDTO parent;
    @OneToMany
    @ToString.Exclude
    private List<BrandDTO> brandList;
    private boolean deleted;
    private ZonedDateTime createTime;
    private ZonedDateTime updateTime;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        CategoryDTO that = (CategoryDTO) o;
        return id != null && Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return 0;
    }
}
