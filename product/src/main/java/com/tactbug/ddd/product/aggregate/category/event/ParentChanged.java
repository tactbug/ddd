package com.tactbug.ddd.product.aggregate.category.event;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.tactbug.ddd.common.entity.Event;
import com.tactbug.ddd.common.entity.EventType;
import com.tactbug.ddd.common.utils.SerializeUtil;
import com.tactbug.ddd.product.aggregate.category.Category;
import com.tactbug.ddd.product.aggregate.category.CategoryEvent;
import com.tactbug.ddd.product.assist.exception.TactProductException;

import javax.persistence.Entity;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @Author tactbug
 * @Email tactbug@Gmail.com
 * @Time 2021/10/5 17:20
 */
@Entity
public class ParentChanged extends CategoryEvent {
    public ParentChanged(Long id, Category category, EventType eventType, Long operator) {
        super(id, category, eventType, operator);
        assembleData(category);
        checkData();
    }

    public ParentChanged() {
        super();
    }

    public void assembleData(Category category){
        Map<String, Object> map = new HashMap<>();
        map.put("id", category.getId());
        map.put("parentId", category.getParentId());
        try {
            this.data = SerializeUtil.mapToString(map);
        } catch (JsonProcessingException e) {
            throw TactProductException.jsonException(e);
        }
    }

    public void checkData(){
        super.check();
        Map<String, Object> data;
        try {
            data = SerializeUtil.jsonToObject(this.data, new TypeReference<>() {
            });
        } catch (JsonProcessingException e) {
            throw new TactProductException("json解析异常", e.getMessage());
        }
        if (Objects.isNull(data.get("id")) || !SerializeUtil.isNumber(data.get("id").toString())){
            throw new IllegalStateException("商品分类溯源事件[" + getId() + "]聚合ID状态异常");
        }
        if (Objects.isNull(data.get("parentId")) || !SerializeUtil.isNumber(data.get("parentId").toString())){
            throw new IllegalStateException("商品分类溯源事件[" + getId() + "]聚合父分类ID状态异常");
        }
    }

}
