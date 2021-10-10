package com.tactbug.ddd.product.outbound.repository.jpa.category;

import com.tactbug.ddd.product.domain.category.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * @Author tactbug
 * @Email tactbug@Gmail.com
 * @Time 2021/10/5 18:47
 */
@Repository
public interface CategorySnapshotRepository extends JpaRepository<Category, Long> {
    Optional<Category> findByIdAndDelFlagIsFalse(Long id);
}
