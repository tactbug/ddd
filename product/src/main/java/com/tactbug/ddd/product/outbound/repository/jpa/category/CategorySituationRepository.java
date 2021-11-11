package com.tactbug.ddd.product.outbound.repository.jpa.category;

import com.tactbug.ddd.product.domain.category.CategorySituation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CategorySituationRepository extends JpaRepository<CategorySituation, Long> {
    Boolean existsByCategoryNameAndDeletedIsTrue(String categoryName);
}
