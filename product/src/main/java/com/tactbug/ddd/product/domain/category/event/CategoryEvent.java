package com.tactbug.ddd.product.domain.category.event;

import com.tactbug.ddd.common.entity.Event;
import com.tactbug.ddd.common.entity.EventType;
import com.tactbug.ddd.product.domain.category.Category;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity
@Table(name = "category_event",
        uniqueConstraints = {@UniqueConstraint(columnNames = {"domain_id", "domain_version"})},
        indexes = {@Index(columnList = "domain_id")}
)
public class CategoryEvent extends Event<Category> {

    private String categoryName;

    public CategoryEvent(Long id, Category category, Long operator){
        super(id, category, operator);
        this.categoryName = category.getName();
    }

    public CategoryEvent() {

    }

}
