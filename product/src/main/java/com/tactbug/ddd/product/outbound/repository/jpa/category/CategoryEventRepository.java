package com.tactbug.ddd.product.outbound.repository.jpa.category;

import com.tactbug.ddd.common.entity.EventType;
import com.tactbug.ddd.product.domain.category.CategoryEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

/**
 * @Author tactbug
 * @Email tactbug@Gmail.com
 * @Time 2021/10/5 18:52
 */
@Repository
public interface CategoryEventRepository extends JpaRepository<CategoryEvent, Long> {
    List<CategoryEvent> findAllByDomainIdAndDomainVersionGreaterThanOrderByDomainVersionAsc(Long domainId, Integer domainVersion);
    boolean existsByDomainIdAndEventType(Long id, EventType eventType);
    boolean existsByDomainId(Long id);
    boolean existsByCategoryNameAndEventType(String name, EventType eventType);
}
