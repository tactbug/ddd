package com.tactbug.ddd.common.base;

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
    @Column(name = "domain_version")
    protected Integer domainVersion;
    protected Class<?> eventType;
    protected Long operator;
    protected String data;
    protected boolean published;

    protected Event(Long id, T t, Long operator) {
        super(id);
        this.domainId = t.getId();
        this.domainVersion = t.getVersion();
        this.operator = operator;
        this.eventType = this.getClass();
        check();
    }

    public Event() {
        super();
    }

    public static void checkList(List<? extends Event<? extends BaseDomain>> list){
        for (int i = 1; i < list.size(); i++) {
            if (list.get(i).getDomainVersion() != list.get(i - 1).getDomainVersion() + 1){
                throw new IllegalStateException("事件版本不匹配[" + list.get(i - 1).getDomainVersion() + "] -> [" + list.get(i).getDomainVersion() + "]");
            }
        }
    }

    public void check() {
        super.check();
        checkNull();
    }

    public void replay(BaseDomain baseDomain){
        baseDomain.setId(domainId);
        baseDomain.setVersion(domainVersion);
        if (Objects.isNull(baseDomain.createTime)){
            baseDomain.setCreateTime(createTime);
        }
        baseDomain.setUpdateTime(createTime);
    }

    public void doAccept(BaseDomain baseDomain){
        replay(baseDomain);
    }

    public void publish(){
        if (!published){
            published = true;
            update();
        }
    }

    @Override
    public int compareTo(BaseDomain o) {
        if (Objects.isNull(o)){
            return -1;
        }
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

    public Class<?> getEventType() {
        return eventType;
    }

    public void setEventType(Class<?> eventType) {
        this.eventType = eventType;
    }

    @Override
    public String toString() {
        return "Event{" +
                "id=" + id +
                ", version=" + version +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                ", domainId=" + domainId +
                ", domainVersion=" + domainVersion +
                ", eventType=" + eventType +
                ", operator=" + operator +
                ", data='" + data + '\'' +
                ", published=" + published +
                '}';
    }
}
