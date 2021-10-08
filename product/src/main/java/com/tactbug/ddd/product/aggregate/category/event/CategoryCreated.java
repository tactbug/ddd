package com.tactbug.ddd.product.aggregate.category.event;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.tactbug.ddd.common.entity.Event;
import com.tactbug.ddd.common.entity.EventType;
import com.tactbug.ddd.common.utils.SerializeUtil;
import com.tactbug.ddd.product.aggregate.category.Category;
import com.tactbug.ddd.product.assist.exception.TactProductException;
import org.springframework.data.relational.core.mapping.Table;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @Author tactbug
 * @Email tactbug@Gmail.com
 * @Time 2021/10/3 21:39
 */
@Table("category_event")
public class CategoryCreated extends Event<Category> {

    public CategoryCreated(Long id, Category category, EventType eventType, Long operator) {
        super(id, category, eventType, operator);
        assembleData(category);
        check();
    }

    @Override
    public void assembleData(Category category) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", category.getId());
        map.put("name", category.getName());
        map.put("remark", category.getRemark());
        map.put("parentId", category.getParentId());
        try {
            this.data = SerializeUtil.mapToString(map);
        } catch (JsonProcessingException e) {
            throw TactProductException.jsonException(e);
        }
    }

    public void check(){
        super.check();
        Map<String, Object> data;
        try {
            data = SerializeUtil.jsonToObject(this.data, new TypeReference<>() {
            });
        } catch (JsonProcessingException e) {
            throw new TactProductException("json解析异常", e.getMessage());
        }
        if (Objects.isNull(data.get("id")) || !(data.get("id") instanceof Long)){
            throw new IllegalStateException("商品分类溯源事件[" + getId() + "]聚合ID状态异常");
        }
        if (Objects.isNull(data.get("name")) || data.get("name").toString().isBlank()){
            throw new IllegalStateException("商品分类溯源事件[" + getId() + "]聚合名称不能为空");
        }
        if (Objects.isNull(data.get("remark"))){
            data.put("remark", "");
        }
        if (Objects.isNull(data.get("parentId")) || !SerializeUtil.isNumber(data.get("parentId").toString())){
            throw new IllegalStateException("商品分类溯源事件[" + getId() + "]聚合父分类ID状态异常");
        }
    }
}
