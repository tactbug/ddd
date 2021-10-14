package com.tactbug.ddd.product.domain.brand.event;

import com.tactbug.ddd.common.entity.Event;
import com.tactbug.ddd.common.entity.EventType;
import com.tactbug.ddd.product.domain.brand.Brand;

import javax.persistence.*;

@Entity
@Table(name = "brand_event",
        indexes = {@Index(columnList = "brand_name")},
        uniqueConstraints = {@UniqueConstraint(columnNames = {"domain_id", "domain_version"})}
)
public class BrandEvent extends Event<Brand> {

    @Column(name = "brand_name")
    private String brandName;

    public BrandEvent(Long id, Brand brand, EventType eventType, Long operator){
        super(id, brand, eventType, operator);
        brandName = brand.getName();
    }

    public BrandEvent(){}
}
