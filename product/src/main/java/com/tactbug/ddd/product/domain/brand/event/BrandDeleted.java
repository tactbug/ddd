package com.tactbug.ddd.product.domain.brand.event;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.tactbug.ddd.common.entity.EventType;
import com.tactbug.ddd.common.utils.SerializeUtil;
import com.tactbug.ddd.product.assist.exception.TactProductException;
import com.tactbug.ddd.product.domain.brand.Brand;

import javax.persistence.Entity;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @Author tactbug
 * @Email tactbug@Gmail.com
 * @Time 2021/10/13 22:15
 */
@Entity
public class BrandDeleted extends BrandEvent{
    public BrandDeleted(Long id, Brand brand, Long operator){
        super(id, brand, operator);
        assembleData(brand);
        checkData();
    }

    public BrandDeleted() {
        super();
    }

    private void assembleData(Brand brand){
        Map<String, Object> map = new HashMap<>();
        map.put("id", brand.getId());
        try {
            this.data = SerializeUtil.mapToString(map);
        } catch (JsonProcessingException e) {
            throw TactProductException.jsonOperateError(this.data, e);
        }
    }

    public void checkData(){
        super.check();
        Map<String, Object> data;
        try {
            data = SerializeUtil.jsonToObject(this.data, new TypeReference<>() {
            });
        } catch (JsonProcessingException e) {
            throw TactProductException.jsonOperateError(this.data, e);
        }
        if (Objects.isNull(data.get("id")) || !SerializeUtil.isNumber(data.get("id").toString())){
            throw new IllegalStateException("品牌溯源事件[" + getId() + "]聚合ID状态异常");
        }
    }
}
