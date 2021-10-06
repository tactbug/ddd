package com.tactbug.ddd.product.aggregate.category.event;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.tactbug.ddd.common.entity.Event;
import com.tactbug.ddd.common.entity.EventType;
import com.tactbug.ddd.common.utils.SerializeUtil;
import com.tactbug.ddd.product.aggregate.category.Category;
import com.tactbug.ddd.product.assist.exception.TactProductException;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author tactbug
 * @Email tactbug@Gmail.com
 * @Time 2021/10/5 17:20
 */
public class ParentChanged extends Event<Category> {
    public ParentChanged(Long id, Category category, EventType eventType, Long operator) {
        super(id, category, eventType, operator);
    }

    @Override
    public void assembleData(Category category){
        Map<String, Object> map = new HashMap<>();
        map.put("parentId", category.getParentId());
        try {
            this.data = SerializeUtil.mapToString(map);
        } catch (JsonProcessingException e) {
            throw TactProductException.jsonException(e);
        }
    }
}
