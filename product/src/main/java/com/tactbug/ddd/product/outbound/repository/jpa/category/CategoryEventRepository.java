package com.tactbug.ddd.product.outbound.repository.jpa.category;

import com.tactbug.ddd.product.domain.category.event.CategoryEvent;
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
    boolean existsByDomainIdAndTypeNot(Long domainId, Class<? extends CategoryEvent> type);
    boolean existsByDomainIdAndType(Long domainId, Class<? extends CategoryEvent> type);
    boolean existsAllByDomainIdInAndTypeNot(Collection<Long> domainId, Class<? extends CategoryEvent> type);
    boolean existsByDomainIdInAndType(Collection<Long> domainId, Class<? extends CategoryEvent> type);
    boolean existsByCategoryNameAndTypeNot(String categoryName, Class<? extends CategoryEvent> type);
    boolean existsAllByCategoryNameInAndTypeNot(Collection<String> categoryName, Class<? extends CategoryEvent> type);
}
