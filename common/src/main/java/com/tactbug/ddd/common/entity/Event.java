package com.tactbug.ddd.common.entity;

import java.util.Objects;

/**
 * @Author tactbug
 * @Email tactbug@Gmail.com
 * @Time 2021/9/28 19:47
 */
public abstract class Event<T extends BaseAggregate> extends BaseAggregate{

    protected Long aggregateId;
    protected Class<? extends BaseAggregate> aggregateType;
    protected Integer aggregateVersion;
    protected EventType eventType;
    protected Long operator;
    protected String data;

    protected Event(Long id, T t, EventType eventType, Long operator) {
        super(id);
        this.aggregateId = t.getId();
        this.aggregateType = t.getClass();
        this.aggregateVersion = t.getVersion();
        this.eventType = eventType;
        this.operator = operator;
        assembleData(t);
        check();
    }

    public abstract void assembleData(T t);


    public void check() {
        super.check();
        checkNull();
    }

    @Override
    public int compareTo(BaseAggregate o) {
        Event<? extends BaseAggregate> other = (Event<? extends BaseAggregate>) o;
        return this.getVersion() - other.getVersion();
    }

    private void checkNull(){
        if (Objects.isNull(aggregateId)){
            throw new IllegalArgumentException("聚合ID不能为空");
        }
        if (Objects.isNull(aggregateType)){
            throw new IllegalArgumentException("聚合类型不能为空");
        }
        if (Objects.isNull(aggregateVersion)){
            throw new IllegalArgumentException("聚合版本不能为空");
        }
        if (Objects.isNull(eventType)){
            throw new IllegalArgumentException("事件类型不能为空");
        }
        if (Objects.isNull(operator)){
            throw new IllegalArgumentException("操作人员不能为空");
        }
    }

    public Long getAggregateId() {
        return aggregateId;
    }

    public Class<? extends BaseAggregate> getAggregateType() {
        return aggregateType;
    }

    public Integer getAggregateVersion() {
        return aggregateVersion;
    }

    public EventType getEventType() {
        return eventType;
    }

    public Long getOperator() {
        return operator;
    }

    public String getData() {
        return data;
    }
}
