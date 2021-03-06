package com.tactbug.ddd.product.domain.category;

import com.tactbug.ddd.common.base.Event;
import lombok.Getter;

import javax.persistence.*;

@Entity
@Table(name = "category_event",
        uniqueConstraints = {@UniqueConstraint(columnNames = {"domain_id", "domain_version"})},
        indexes = {@Index(columnList = "domain_id")}
)
@Getter
public class CategoryEvent extends Event<Category> {

    public CategoryEvent(Long eventId, Category category, Long operator){
        super(eventId, category, operator);
    }

    public CategoryEvent() {

    }

}
