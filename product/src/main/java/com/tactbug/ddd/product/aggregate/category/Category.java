package com.tactbug.ddd.product.aggregate.category;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.tactbug.ddd.common.entity.BaseAggregate;
import com.tactbug.ddd.common.entity.Event;
import com.tactbug.ddd.common.entity.EventType;
import com.tactbug.ddd.product.aggregate.category.event.CategoryCreated;
import com.tactbug.ddd.product.aggregate.category.event.NameUpdated;
import com.tactbug.ddd.product.aggregate.category.event.RemarkUpdated;
import lombok.Getter;

import java.util.List;

/**
 * @Author tactbug
 * @Email tactbug@Gmail.com
 * @Time 2021/9/28 19:15
 */
@Getter
public class Category extends BaseAggregate {

    private String name;
    private String remark;
    private Long parentId;

    private List<Long> childrenIds;
    private List<Long> brandIds;

    private Category(Long id) {
        super(id);
    }

    public static Category generate(Long id, String name, String remark, Long parentId){
        Category category = new Category(id);
        category.name = name;
        category.remark = remark;
        return category;
    }

    public Event<Category> createCategory(Long eventId, Long operator) throws JsonProcessingException {
        return new CategoryCreated(eventId, this, EventType.CREATED, operator);
    }

    public Event<Category> updateName(Long eventId, Long operator, String name) throws JsonProcessingException {
        this.name = name;
        update();
        return new NameUpdated(eventId, this, EventType.UPDATED, operator);
    }

    public Event<Category> updateRemark(Long eventId, Long operator, String remark) throws JsonProcessingException {
        this.remark = remark;
        update();
        return new RemarkUpdated(eventId, this, EventType.UPDATED, operator);
    }

}
