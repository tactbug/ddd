package com.tactbug.ddd.common.entity;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import java.util.List;
import java.util.Objects;

/**
 * @Author tactbug
 * @Email tactbug@Gmail.com
 * @Time 2021/9/28 19:47
 */
@MappedSuperclass
public class Event<T extends BaseDomain> extends BaseDomain {

    @Column(name = "domain_id")
    protected Long domainId;
    protected Class<? extends BaseDomain> domain;
    @Column(name = "domain_version")
    protected Integer domainVersion;
    protected EventType eventType;
    protected Long operator;
    protected String data;

    protected Event(Long id, T t, EventType eventType, Long operator) {
        super(id);
        this.domainId = t.getId();
        this.domain = t.getClass();
        this.domainVersion = t.getVersion();
        this.eventType = eventType;
        this.operator = operator;
        check();
    }

    public Event() {
        super();
    }

    public void check() {
        super.check();
        checkNull();
    }

    @Override
    public int compareTo(BaseDomain o) {
        Event<? extends BaseDomain> other = (Event<? extends BaseDomain>) o;
        return this.getDomainVersion() - other.getDomainVersion();
    }

    private void checkNull(){
        if (Objects.isNull(domainId)){
            throw new IllegalArgumentException("聚合ID不能为空");
        }
        if (Objects.isNull(domain)){
            throw new IllegalArgumentException("聚合类型不能为空");
        }
        if (Objects.isNull(domainVersion)){
            throw new IllegalArgumentException("聚合版本不能为空");
        }
        if (Objects.isNull(eventType)){
            throw new IllegalArgumentException("事件类型不能为空");
        }
        if (Objects.isNull(operator)){
            throw new IllegalArgumentException("操作人员不能为空");
        }
    }

    public Long getDomainId() {
        return domainId;
    }

    public Class<? extends BaseDomain> getDomain() {
        return domain;
    }

    public Integer getDomainVersion() {
        return domainVersion;
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
