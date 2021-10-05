package com.tactbug.ddd.common.entity;

import com.fasterxml.jackson.core.JsonProcessingException;

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

    protected Event(Long id, T t, EventType eventType, Long operator) throws JsonProcessingException {
        super(id);
        this.aggregateId = t.getId();
        this.aggregateType = t.getClass();
        this.aggregateVersion = t.getVersion();
        this.eventType = eventType;
        this.operator = operator;
        assembleData(t);
        check();
    }

    public abstract void assembleData(T t) throws JsonProcessingException;


    public void check(){
        super.check();
        checkNull();
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
}
