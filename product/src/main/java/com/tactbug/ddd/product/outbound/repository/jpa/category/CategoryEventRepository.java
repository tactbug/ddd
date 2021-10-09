package com.tactbug.ddd.product.outbound.repository.jpa.category;

import com.tactbug.ddd.product.aggregate.category.CategoryEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;

/**
 * @Author tactbug
 * @Email tactbug@Gmail.com
 * @Time 2021/10/5 18:52
 */
@Repository
public interface CategoryEventRepository extends JpaRepository<CategoryEvent, Long> {
    Collection<CategoryEvent> findAllByDomainIdAndDomainVersionGreaterThan(Long domainId, Integer domainVersion);
}
