package com.tactbug.ddd.product.outbound.repository.jpa.category;

import com.tactbug.ddd.product.domain.category.event.CategoryEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * @Author tactbug
 * @Email tactbug@Gmail.com
 * @Time 2021/10/5 18:52
 */
@Repository
public interface CategoryEventRepository extends JpaRepository<CategoryEvent, Long> {
    List<CategoryEvent> findAllByDomainIdAndDomainVersionGreaterThanOrderByDomainVersionAsc(Long domainId, Integer domainVersion);
    Optional<CategoryEvent> findFirstByDomainIdOrderByDomainVersionDesc(Long domainId);
    Collection<CategoryEvent> findAllByCategoryName(String categoryName);
    List<CategoryEvent> findAllByDomainIdIn(Collection<Long> domainId);
    boolean existsByDomainIdAndEventType(Long domainId, Class<? extends CategoryEvent> type);
    boolean existsAllByDomainIdInAndEventTypeNot(Collection<Long> domainId, Class<? extends CategoryEvent> type);
    boolean existsByDomainIdInAndEventType(Collection<Long> domainId, Class<? extends CategoryEvent> type);
}
