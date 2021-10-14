package com.tactbug.ddd.product.inbound.http.brand.vo;

import com.tactbug.ddd.product.domain.brand.Brand;
import lombok.Data;
import org.springframework.beans.BeanUtils;

@Data
public class BrandVo {
    private Long id;
    private String name;
    private String remark;

    public static BrandVo generate(Brand brand){
        BrandVo brandVo = new BrandVo();
        BeanUtils.copyProperties(brand, brandVo);
        return brandVo;
    }
}
