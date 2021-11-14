package com.tactbug.ddd.product.query.vo;

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
import java.util.List;
import java.util.Objects;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
@Entity
public class CategoryVo {
    @Id
    private Long id;
    private Integer version;
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
    private ZonedDateTime createTime;
    private ZonedDateTime updateTime;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        CategoryVo that = (CategoryVo) o;
        return id != null && Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return 0;
    }
}
