package com.tactbug.ddd.product.aggregate.category.event;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.tactbug.ddd.common.entity.Event;
import com.tactbug.ddd.common.entity.EventType;
import com.tactbug.ddd.common.utils.SerializeUtil;
import com.tactbug.ddd.product.aggregate.category.Category;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author tactbug
 * @Email tactbug@Gmail.com
 * @Time 2021/10/3 21:41
 */
public class RemarkUpdated extends Event<Category> {
    public RemarkUpdated(Long id, Category category, EventType eventType, Long operator) throws JsonProcessingException {
        super(id, category, eventType, operator);
    }

    @Override
    public void assembleData(Category category) throws JsonProcessingException {
        Map<String, Object> map = new HashMap<>();
        map.put("remark", category.getRemark());
        this.data = SerializeUtil.mapToString(map);
    }
}
