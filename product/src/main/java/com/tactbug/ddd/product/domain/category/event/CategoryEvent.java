package com.tactbug.ddd.product.domain.category.event;

import com.tactbug.ddd.common.entity.Event;
import com.tactbug.ddd.product.domain.category.Category;

import javax.persistence.*;

@Entity
@Table(name = "category_event",
        uniqueConstraints = {@UniqueConstraint(columnNames = {"domain_id", "domain_version"})},
        indexes = {@Index(columnList = "domain_id")}
)
public class CategoryEvent extends Event<Category> {

    private String categoryName;
    @Column(name = "type")
    protected Class<? extends CategoryEvent> type;

    public CategoryEvent(Long id, Category category, Long operator){
        super(id, category, operator);
        this.categoryName = category.getName();
        this.type = this.getClass();
    }

    public CategoryEvent() {

    }

}
