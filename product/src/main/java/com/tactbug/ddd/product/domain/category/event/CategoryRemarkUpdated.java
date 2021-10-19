package com.tactbug.ddd.product.domain.category.event;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.tactbug.ddd.common.entity.EventType;
import com.tactbug.ddd.common.utils.SerializeUtil;
import com.tactbug.ddd.product.domain.category.Category;
import com.tactbug.ddd.product.assist.exception.TactProductException;

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
    public CategoryRemarkUpdated(Long id, Category category, Long operator) {
        super(id, category, operator);
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
        } catch (Exception e) {
            throw TactProductException.replyError("[" + category.getId() + "]备注数据异常");
        }
    }

    private void assembleData(Category category){
        Map<String, Object> map = new HashMap<>();
        map.put("id", category.getId());
        map.put("remark", category.getRemark());
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
        if (Objects.isNull(data.get("remark"))){
            data.put("remark", "");
        }
    }
}
