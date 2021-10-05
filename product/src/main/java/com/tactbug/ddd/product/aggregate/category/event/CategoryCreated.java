package com.tactbug.ddd.product.aggregate.category.event;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.tactbug.ddd.common.entity.BaseAggregate;
import com.tactbug.ddd.common.entity.Event;
import com.tactbug.ddd.common.entity.EventType;
import com.tactbug.ddd.common.utils.SerializeUtil;
import com.tactbug.ddd.product.aggregate.category.Category;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author tactbug
 * @Email tactbug@Gmail.com
 * @Time 2021/10/3 21:39
 */
public class CategoryCreated extends Event<Category> {

    public CategoryCreated(Long id, Category category, EventType eventType, Long operator) throws JsonProcessingException {
        super(id, category, eventType, operator);
        assembleData(category);
        check();
    }

    @Override
    public void assembleData(Category category) throws JsonProcessingException {
        Map<String, Object> map = new HashMap<>();
        map.put("id", category.getId());
        map.put("name", category.getName());
        map.put("remark", category.getRemark());
        this.data = SerializeUtil.mapToString(map);
    }
}
