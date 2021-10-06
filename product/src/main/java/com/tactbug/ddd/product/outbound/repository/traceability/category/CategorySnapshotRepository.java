package com.tactbug.ddd.product.outbound.repository.traceability.category;

import com.tactbug.ddd.product.aggregate.category.Category;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * @Author tactbug
 * @Email tactbug@Gmail.com
 * @Time 2021/10/5 18:47
 */
@Repository
public interface CategorySnapshotRepository extends ReactiveCrudRepository<Category, Long> {
}
