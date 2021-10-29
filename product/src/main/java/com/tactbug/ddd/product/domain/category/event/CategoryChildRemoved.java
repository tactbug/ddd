package com.tactbug.ddd.product.domain.category.event;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.tactbug.ddd.common.utils.SerializeUtil;
import com.tactbug.ddd.product.assist.exception.TactProductException;
import com.tactbug.ddd.product.domain.category.Category;

import javax.persistence.Entity;
import java.util.*;

@Entity
public class CategoryChildRemoved extends CategoryEvent{
    public CategoryChildRemoved(Long eventId, Category category, Long childId, Long operator) {
        super(eventId, category, operator);
        assembleData(category, childId);
        checkData();
    }

    public CategoryChildRemoved() {
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

    private void assembleData(Category category, Long childId){
        Map<String, Object> map = new HashMap<>();
        map.put("id", category.getId());
        map.put("childId", childId);
        try {
            data = SerializeUtil.mapToString(map);
        } catch (JsonProcessingException e) {
            throw TactProductException.jsonException(e);
        }
    }

    private void checkData(){
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
        if (Objects.isNull(data.get("childId")) || !SerializeUtil.isNumber(data.get("childId").toString())){
            throw new IllegalStateException("childId不正确[" + data.get("childId") + "]");
        }
    }
}
