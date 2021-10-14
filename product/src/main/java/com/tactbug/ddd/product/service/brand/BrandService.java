package com.tactbug.ddd.product.service.brand;

import com.tactbug.ddd.common.utils.IdUtil;
import com.tactbug.ddd.product.TactProductApplication;
import com.tactbug.ddd.product.assist.exception.TactProductException;
import com.tactbug.ddd.product.domain.brand.Brand;
import com.tactbug.ddd.product.domain.brand.command.BrandCommand;
import com.tactbug.ddd.product.domain.brand.command.CreateBrand;
import com.tactbug.ddd.product.domain.brand.event.BrandDeleted;
import com.tactbug.ddd.product.domain.brand.event.BrandEvent;
import com.tactbug.ddd.product.outbound.repository.jpa.brand.BrandRepository;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Optional;

@Service
public class BrandService {
    
    @Resource
    private BrandRepository brandRepository;

    private static final IdUtil BRAND_ID_UTIL = IdUtil.getOrGenerate(
            TactProductApplication.APPLICATION_NAME, Brand.class, 50000, 5000, 10000
    );

    public Brand createBrand(CreateBrand createBrand){
        Brand brand = Brand.generate(BRAND_ID_UTIL.getId(), createBrand);
        brandRepository.create(brand, createBrand.operator());
        return brand;
    }

    public Brand update(BrandCommand brandCommand){
        Brand brand = brandRepository.getOneById(brandCommand.getId())
                .orElseThrow(() -> TactProductException.resourceOperateError("品牌[" + brandCommand.getId() + "]不存在"));
        List<BrandEvent> events = brand.update(BRAND_ID_UTIL, brandCommand);
        if (!events.isEmpty()){
            brandRepository.update(brand, events);
        }
        return brand;
    }

    public Optional<Brand> delete(BrandCommand brandCommand){
        Optional<Brand> optional = brandRepository.getOneById(brandCommand.getId())
                .or(Optional::empty);
        if (optional.isPresent()){
            Brand brand = optional.get();
            BrandDeleted event = brand.deleteBrand(BRAND_ID_UTIL.getId(), brandCommand.deleteBrand());
            brandRepository.delete(brand, event);
            optional = Optional.of(brand);
        }
        return optional;
    }

    public Optional<Brand> getById(Long id){
        return brandRepository.getOneById(id);
    }
}
