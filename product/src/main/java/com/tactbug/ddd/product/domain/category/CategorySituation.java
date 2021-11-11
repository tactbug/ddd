package com.tactbug.ddd.product.domain.category;

import com.tactbug.ddd.common.entity.BaseSituation;
import com.tactbug.ddd.product.domain.category.event.CategoryEvent;

import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;

@Entity
@Table(
        name = "category_situation",
        indexes = {@Index(columnList = "category_name")}
)
public class CategorySituation extends BaseSituation {

    private String categoryName;

    private CategorySituation(CategoryEvent event, String categoryName) {
        super(event);
        this.categoryName = categoryName;
    }

    public CategorySituation() {

    }

    public static CategorySituation generate(CategoryEvent event, String categoryName){
        return new CategorySituation(event, categoryName);
    }
}
