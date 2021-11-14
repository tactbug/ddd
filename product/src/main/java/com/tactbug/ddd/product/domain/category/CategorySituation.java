package com.tactbug.ddd.product.domain.category;

import com.tactbug.ddd.common.entity.BaseSituation;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;

@Entity
@Table(
        name = "category_situation",
        indexes = {@Index(columnList = "category_name")}
)
@Getter
@Setter
public class CategorySituation extends BaseSituation {

    @Column(name = "category_name")
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

    public void update(CategoryEvent categoryEvent, String categoryName){
        super.update(categoryEvent);
        this.categoryName = categoryName;
    }

    public void delete(CategoryEvent categoryEvent){
        super.delete(categoryEvent);
    }
}
