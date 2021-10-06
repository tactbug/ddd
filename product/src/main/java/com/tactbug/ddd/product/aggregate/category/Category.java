package com.tactbug.ddd.product.aggregate.category;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.tactbug.ddd.common.entity.BaseAggregate;
import com.tactbug.ddd.common.entity.Event;
import com.tactbug.ddd.common.entity.EventType;
import com.tactbug.ddd.common.utils.SerializeUtil;
import com.tactbug.ddd.product.aggregate.category.event.CategoryCreated;
import com.tactbug.ddd.product.aggregate.category.event.NameUpdated;
import com.tactbug.ddd.product.aggregate.category.event.ParentChanged;
import com.tactbug.ddd.product.aggregate.category.event.RemarkUpdated;
import com.tactbug.ddd.product.assist.exception.TactProductException;
import lombok.Getter;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

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

    private Category(){
        super();
    }

    private Category(Long id) {
        super(id);
    }

    public static Category generate(Long id, String name, String remark, Long parentId){
        Category category = new Category(id);
        category.name = name;
        category.remark = remark;
        return category;
    }

    public static Category replay(Collection<Event<Category>> events, Category snapshot) {
        if (Objects.isNull(snapshot)){
            snapshot = new Category();
        }
        if (events.isEmpty()){
            return snapshot;
        }
        List<Event<Category>> sortedEvents = events.stream().sorted().collect(Collectors.toList());
        if (sortedEvents.get(0).getVersion() - snapshot.getVersion() != 1){
            throw new IllegalStateException("快照版本[" + snapshot.getVersion() + "]跟溯源版本[" + snapshot.getVersion() + "]不匹配");
        }
        return doReplay(snapshot, sortedEvents);
    }

    public Event<Category> createCategory(Long eventId, Long operator) {
        return new CategoryCreated(eventId, this, EventType.CREATED, operator);
    }

    public Event<Category> updateName(Long eventId, Long operator, String name) {
        this.name = name;
        update();
        return new NameUpdated(eventId, this, EventType.UPDATED, operator);
    }

    public Event<Category> updateRemark(Long eventId, Long operator, String remark){
        this.remark = remark;
        update();
        return new RemarkUpdated(eventId, this, EventType.UPDATED, operator);
    }

    public Event<Category> changeParent(Long eventId, Long operator, Category parent) {
        this.parentId = parent.getId();
        update();
        return new ParentChanged(eventId, this, EventType.UPDATED, operator);
    }

    private static Category doReplay(Category snapshot, List<Event<Category>> events) {
        for (int i = 0; i < events.size(); i++) {
            Event<Category> last = events.get(i);
            if (i < events.size() - 1){
                Event<Category> next = events.get(i + 1);
                if (next.getVersion() - last.getVersion() != 1){
                    throw new IllegalStateException("溯源版本顺序错误, " +
                            "last[" + last.getId() + "]版本[" + last.getVersion() + "], " +
                            "next[" + next.getId() + "]版本[" + next.getVersion() +"]");
                }
                if (last.getEventType().equals(EventType.DELETED)){
                    throw new IllegalStateException("溯源删除事件[" + last.getId() + "]后不能有后续事件");
                }
            }
            snapshot.eventsReplay(events.get(i));
        }
        snapshot.check();
        return snapshot;
    }

    private void eventsReplay(Event<Category> event) {
        super.replay(event);
        String json = event.getData();
        Map<String, Object> data;
        try {
            data = SerializeUtil.jsonToObject(json, new TypeReference<>() {});
        } catch (JsonProcessingException e) {
            throw new TactProductException("json解析异常", e.getMessage());
        }
        this.name = data.get("name").toString();
        this.remark = data.get("remark").toString();
        this.parentId = Long.valueOf(data.get("parentId").toString());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Category category = (Category) o;
        return name.equals(category.name) && parentId.equals(category.parentId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, parentId);
    }
}
