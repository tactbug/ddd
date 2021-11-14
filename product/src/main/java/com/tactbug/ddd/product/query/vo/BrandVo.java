package com.tactbug.ddd.product.query.vo;

import lombok.*;
import org.hibernate.Hibernate;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.time.ZonedDateTime;
import java.util.Objects;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
@Entity
public class BrandVo {
    @Id
    private Long id;
    private String name;
    private String remark;
    private ZonedDateTime createTime;
    private ZonedDateTime updateTime;
    private boolean deleted;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        BrandVo brandDTO = (BrandVo) o;
        return id != null && Objects.equals(id, brandDTO.id);
    }

    @Override
    public int hashCode() {
        return 0;
    }
}
