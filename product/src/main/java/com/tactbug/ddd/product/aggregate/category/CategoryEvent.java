package com.tactbug.ddd.product.aggregate.category;

import com.tactbug.ddd.common.entity.Event;
import com.tactbug.ddd.common.entity.EventType;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity
@Table(name = "category_event", uniqueConstraints = {@UniqueConstraint(columnNames = {"domain_id", "domain_version"})})
public class CategoryEvent extends Event<Category> {
    public CategoryEvent(Long id, Category category, EventType eventType, Long operator){
        super(id, category, eventType, operator);
    }

    public CategoryEvent() {

    }

}
