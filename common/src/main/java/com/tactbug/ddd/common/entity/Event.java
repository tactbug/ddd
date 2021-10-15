package com.tactbug.ddd.common.entity;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
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
    @Column(name = "domain_version")
    protected Integer domainVersion;
    protected Long operator;
    protected String data;

    protected Event(Long id, T t, Long operator) {
        super(id);
        this.domainId = t.getId();
        this.domainVersion = t.getVersion();
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
        if (Objects.isNull(domainVersion)){
            throw new IllegalArgumentException("聚合版本不能为空");
        }
        if (Objects.isNull(operator)){
            throw new IllegalArgumentException("操作人员不能为空");
        }
    }

    public Long getDomainId() {
        return domainId;
    }

    public Integer getDomainVersion() {
        return domainVersion;
    }


    public Long getOperator() {
        return operator;
    }

    public String getData() {
        return data;
    }
}
