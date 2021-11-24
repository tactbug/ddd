package com.tactbug.ddd.product.domain.category.event;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.tactbug.ddd.common.utils.SerializeUtil;
import com.tactbug.ddd.product.domain.category.Category;
import com.tactbug.ddd.common.exceptions.TactException;
import com.tactbug.ddd.product.domain.category.CategoryEvent;

import javax.persistence.Entity;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @Author tactbug
 * @Email tactbug@Gmail.com
 * @Time 2021/10/3 21:41
 */
@Entity
public class CategoryRemarkUpdated extends CategoryEvent {
    public CategoryRemarkUpdated(Long eventId, Category category, Long operator) {
        super(eventId, category, operator);
        assembleData(category);
        checkData();
    }

    public CategoryRemarkUpdated() {
        super();
    }

    public void replay(Category category){
        super.replay(category);
        try {
            HashMap<String, Object> dataMap = SerializeUtil.jsonToObject(data, new TypeReference<>() {
            });
            category.setRemark(dataMap.get("remark").toString());
            category.setDeleted(false);
        } catch (Exception e) {
            throw TactException.replayError("[" + category.getId() + "]备注数据异常", null);
        }
    }

    private void assembleData(Category category){
        Map<String, Object> map = new HashMap<>();
        map.put("id", category.getId());
        map.put("remark", category.getRemark());
        try {
            data = SerializeUtil.mapToString(map);
        } catch (JsonProcessingException e) {
            throw TactException.serializeOperateError(map.toString(), e);
        }
    }

    private void checkData(){
        super.check();
        Map<String, Object> data;
        try {
            data = SerializeUtil.jsonToObject(this.data, new TypeReference<>() {
            });
        } catch (JsonProcessingException e) {
            throw TactException.serializeOperateError(this.data, e);
        }
        if (Objects.isNull(data.get("id")) || !SerializeUtil.isNumber(data.get("id").toString())){
            throw new IllegalStateException("商品分类溯源事件[" + getId() + "]聚合ID状态异常");
        }
        if (Objects.isNull(data.get("remark"))){
            data.put("remark", "");
        }
    }
}
