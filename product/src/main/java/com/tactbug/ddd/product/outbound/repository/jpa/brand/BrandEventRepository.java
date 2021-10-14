package com.tactbug.ddd.product.outbound.repository.jpa.brand;

import com.tactbug.ddd.common.entity.EventType;
import com.tactbug.ddd.product.domain.brand.event.BrandEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BrandEventRepository extends JpaRepository<BrandEvent, Long> {
    List<BrandEvent> findAllByDomainIdAndDomainVersionGreaterThanOrderByDomainVersionAsc(Long domainId, Integer domainVersion);
    boolean existsByDomainIdAndEventType(Long id, EventType eventType);
    boolean existsByDomainId(Long id);
    boolean existsByBrandNameAndEventType(String name, EventType eventType);
}
