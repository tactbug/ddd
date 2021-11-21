package com.tactbug.ddd.product.outbound.repository.jpa.brand;

import com.tactbug.ddd.product.domain.brand.event.BrandEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BrandEventRepository extends JpaRepository<BrandEvent, Long> {
    List<BrandEvent> findAllByDomainIdAndDomainVersionGreaterThanOrderByDomainVersionAsc(Long domainId, Integer domainVersion);
    boolean existsByDomainIdAndType(Long domainId, Class<? extends BrandEvent> type);
    boolean existsByDomainId(Long id);
    boolean existsByBrandNameAndType(String brandName, Class<? extends BrandEvent> type);
}
