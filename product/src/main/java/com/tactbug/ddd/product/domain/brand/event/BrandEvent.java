package com.tactbug.ddd.product.domain.brand.event;

import com.tactbug.ddd.common.entity.Event;
import com.tactbug.ddd.common.entity.EventType;
import com.tactbug.ddd.product.domain.brand.Brand;
import com.tactbug.ddd.product.domain.category.event.CategoryEvent;

import javax.persistence.*;
import java.time.ZonedDateTime;

@Entity
@Table(name = "brand_event",
        indexes = {@Index(columnList = "brand_name")},
        uniqueConstraints = {@UniqueConstraint(columnNames = {"domain_id", "domain_version"})}
)
public class BrandEvent extends Event<Brand> {

    @Column(name = "brand_name")
    private String brandName;
    @Column(name = "type")
    protected Class<? extends BrandEvent> type;

    public BrandEvent(Long id, Brand brand, Long operator){
        super(id, brand, operator);
        brandName = brand.getName();
        type = this.getClass();
    }

    public BrandEvent(){}
}
