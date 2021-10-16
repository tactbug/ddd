package com.tactbug.ddd.product.outbound.repository.jpa.brand;

import com.tactbug.ddd.common.entity.EventType;
import com.tactbug.ddd.common.utils.IdUtil;
import com.tactbug.ddd.product.TactProductApplication;
import com.tactbug.ddd.product.assist.exception.TactProductException;
import com.tactbug.ddd.product.domain.brand.Brand;
import com.tactbug.ddd.product.domain.brand.event.BrandCreated;
import com.tactbug.ddd.product.domain.brand.event.BrandDeleted;
import com.tactbug.ddd.product.domain.brand.event.BrandEvent;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class BrandRepository {
    private static final IdUtil BRAND_EVENT_ID_UTIL = IdUtil.getOrGenerate(
            TactProductApplication.APPLICATION_NAME, Brand.class, 50000, 5000, 10000
    );

    @Resource
    private BrandSnapshotRepository snapshotRepository;
    @Resource
    private BrandEventRepository eventRepository;

    public Optional<Brand> getOneById(Long id){
        if (isDelete(id)){
            throw TactProductException.resourceOperateError("品牌[" + id + "]已被删除");
        }
        if (!isExists(id, null)){
            throw TactProductException.resourceOperateError("品牌[" + id + "]不存在");
        }
        Brand snapshot = getSnapshot(id).orElse(new Brand());
        List<BrandEvent> events = eventRepository.findAllByDomainIdAndDomainVersionGreaterThanOrderByDomainVersionAsc(id, snapshot.getVersion());
        Brand brand = Brand.replay(events, snapshot);
        brand.check();
        return Optional.of(brand);
    }

    public void create(Brand brand, Long operator){
        if (isExists(brand.getId(), brand)){
            throw TactProductException.resourceOperateError("品牌[" + brand + "]已经存在");
        }
        brand.check();
        BrandCreated event = brand.createBrand(BRAND_EVENT_ID_UTIL.getId(), operator);
        checkEvents(brand);
        eventRepository.save(event);
        snapshotRepository.save(brand);
    }

    public void update(Brand brand, List<BrandEvent> events){
        if (isDelete(brand.getId())){
            throw TactProductException.resourceOperateError("品牌[" + brand + "]已被删除");
        }
        if (!isExists(brand.getId(), brand)){
            throw TactProductException.resourceOperateError("品牌[" + brand + "]不存在");
        }
        checkEvents(brand);
        events = events.stream().sorted().collect(Collectors.toList());
        eventRepository.saveAll(events);
    }

    public void delete(Brand brand, BrandDeleted brandDeleted){
        if (isDelete(brand.getId())){
            return;
        }
        if (!isExists(brand.getId(), brand)){
            throw TactProductException.resourceOperateError("品牌[" + brand + "]不存在");
        }
        brand.check();
        checkEvents(brand);
        snapshotRepository.save(brand);
        eventRepository.save(brandDeleted);
    }

    private Optional<Brand> getSnapshot(Long id){
        return snapshotRepository.findById(id);
    }

    private boolean isDelete(Long id){
        return eventRepository.existsByDomainIdAndType(id, BrandDeleted.class);
    }

    private boolean isExists(Long id, Brand brand){
        if (!eventRepository.existsByDomainId(id)){
            return false;
        }
        if (eventRepository.existsByDomainIdAndType(id, BrandDeleted.class)){
            return false;
        }
        if (Objects.nonNull(brand) && eventRepository.existsByBrandNameAndType(brand.getName(), BrandDeleted.class)){
            return false;
        }
        return true;
    }

    private void checkEvents(Brand brand){
        List<BrandEvent> exists = eventRepository.findAllByDomainIdAndDomainVersionGreaterThanOrderByDomainVersionAsc(brand.getId(), brand.getVersion());
        if (!exists.isEmpty()){
            throw TactProductException.resourceOperateError("品牌[" + brand + "]状态异常");
        }
    }
}
