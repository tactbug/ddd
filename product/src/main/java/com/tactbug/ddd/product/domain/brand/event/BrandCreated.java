package com.tactbug.ddd.product.domain.brand.event;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.tactbug.ddd.common.utils.SerializeUtil;
import com.tactbug.ddd.common.exceptions.TactException;
import com.tactbug.ddd.product.domain.brand.Brand;

import javax.persistence.Entity;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Entity
public class BrandCreated extends BrandEvent{

    public BrandCreated(Long eventId, Brand brand, Long operator) {
        super(eventId, brand, operator);
        assembleData(brand);
        checkData();
    }

    public BrandCreated(){}

    private void assembleData(Brand brand){
        Map<String, Object> map = new HashMap<>();
        map.put("name", brand.getName());
        map.put("remark", brand.getRemark());
        try {
            this.data = SerializeUtil.mapToString(map);
        } catch (JsonProcessingException e) {
            throw TactException.serializeOperateError(this.data, e);
        }
    }

    public void checkData(){
        super.check();
        Map<String, Object> data;
        try {
            data = SerializeUtil.jsonToObject(this.data, new TypeReference<>() {
            });
        } catch (JsonProcessingException e) {
            throw TactException.serializeOperateError(this.data, e);
        }
        if (Objects.isNull(data.get("name")) || data.get("name").toString().isBlank()){
            throw new IllegalStateException("品牌溯源事件[" + getId() + "]聚合名称不能为空");
        }
        if (Objects.isNull(data.get("remark"))){
            data.put("remark", "");
        }
    }
}
