package com.tactbug.ddd.product.domain.category.event;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.tactbug.ddd.common.utils.SerializeUtil;
import com.tactbug.ddd.product.assist.exception.TactProductException;
import com.tactbug.ddd.product.domain.category.Category;

import javax.persistence.Entity;
import java.util.*;

@Entity
public class CategoryChildrenRemoved extends CategoryEvent{
    public CategoryChildrenRemoved(Long eventId, Category category, Collection<Long> childrenIds, Long operator) {
        super(eventId, category, operator);
        assembleData(category, childrenIds);
        checkData(category);
    }

    public CategoryChildrenRemoved() {
        super();
    }

    public void replay(Category category){
        super.replay(category);
        try {
            HashMap<String, Object> dataMap = SerializeUtil.jsonToObject(data, new TypeReference<>() {
            });
            HashSet<Long> childrenIds = SerializeUtil.jsonToObject(dataMap.get("childrenIds").toString(), new TypeReference<>() {
            });
            category.getChildrenIds().removeAll(childrenIds);
        } catch (Exception e) {
            throw TactProductException.replayError("[" + category.getId() + "]子分类数据异常");
        }
    }

    private void assembleData(Category category, Collection<Long> childrenIds){
        Map<String, Object> map = new HashMap<>();
        map.put("id", category.getId());
        map.put("childrenIds", childrenIds);
        try {
            data = SerializeUtil.mapToString(map);
        } catch (JsonProcessingException e) {
            throw TactProductException.jsonException(e);
        }
    }

    private void checkData(Category category){
        super.check();
        Map<String, Object> data;
        try {
            data = SerializeUtil.jsonToObject(this.data, new TypeReference<>() {
            });
        } catch (JsonProcessingException e) {
            throw TactProductException.jsonException(e);
        }
        if (Objects.isNull(data.get("id")) || !SerializeUtil.isNumber(data.get("id").toString())){
            throw new IllegalStateException("商品分类溯源事件[" + getId() + "]聚合ID状态异常");
        }
        if (Objects.nonNull(data.get("childrenIds"))){
            try {
                HashSet<Long> childrenIds = SerializeUtil.jsonToObject(data.get("childrenIds").toString(), new TypeReference<HashSet<Long>>() {
                });
                if (category.getChildrenIds().stream().anyMatch(childrenIds::contains)){
                    throw new IllegalStateException("子分类状态异常");
                }
            } catch (JsonProcessingException e) {
                throw TactProductException.jsonException(e);
            }
        }
    }
}
