package com.tactbug.ddd.common.entity;

import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
public class BaseSituation {

    @Id
    protected Long domainId;
    protected Long lastEventId;
    protected Integer lastVersion;

    protected BaseSituation(){}

    protected BaseSituation(Event<? extends BaseDomain> event){
        domainId = event.getDomainId();
        lastEventId = event.getId();
        lastVersion = event.getDomainVersion();
    }

    protected void update(Event<? extends BaseDomain> event){
        lastEventId = event.getId();
        lastVersion = event.getDomainVersion();
    }


    public Long getDomainId() {
        return domainId;
    }

    public void setDomainId(Long domainId) {
        this.domainId = domainId;
    }

    public Long getLastEventId() {
        return lastEventId;
    }

    public void setLastEventId(Long lastEventId) {
        this.lastEventId = lastEventId;
    }

    public Integer getLastVersion() {
        return lastVersion;
    }

    public void setLastVersion(Integer lastVersion) {
        this.lastVersion = lastVersion;
    }
}
