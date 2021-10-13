package com.tactbug.ddd.product.domain.brand;

import com.tactbug.ddd.common.entity.BaseDomain;
import com.tactbug.ddd.common.entity.EventType;
import com.tactbug.ddd.product.assist.exception.TactProductException;
import com.tactbug.ddd.product.domain.brand.command.CreateBrand;
import com.tactbug.ddd.product.domain.brand.event.BrandCreated;
import lombok.Getter;

import java.util.List;
import java.util.Objects;

@Getter
public class Brand extends BaseDomain {

    private String name;
    private String remark;

    private List<Long> categoryIds;

    public Brand(){
        super();
    }

    private Brand(Long id){
        super(id);
    }

    public static Brand generate(Long id, CreateBrand createBrand){
        Brand brand = new Brand(id);
        brand.name = createBrand.name();
        brand.remark = createBrand.remark();
        brand.check();
        return brand;
    }

    public BrandCreated createBrand(Long eventId, Long operator){
        check();
        return new BrandCreated(eventId, this, EventType.CREATED, operator);
    }

    public void check(){
        super.check();
        if (Objects.isNull(name) || name.isBlank()){
            throw new IllegalArgumentException("品牌名称不能为空");
        }
    }
}
