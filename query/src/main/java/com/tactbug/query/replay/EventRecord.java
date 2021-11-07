package com.tactbug.query.replay;

import com.tactbug.ddd.common.entity.BaseDomain;
import com.tactbug.ddd.common.entity.Event;
import lombok.*;
import org.hibernate.Hibernate;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.Objects;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
@Entity
public class EventRecord {
    @Id
    private Long eventId;
    private Class<? extends BaseDomain> domainType;
    private Long domainId;
    private Boolean executed;

    public static EventRecord generate(Event<? extends BaseDomain> event){
        EventRecord eventRecord = new EventRecord();
        eventRecord.setEventId(event.getId());
        eventRecord.setDomainType(event.getDomainType());
        eventRecord.setDomainId(event.getDomainId());
        eventRecord.setExecuted(false);
        return eventRecord;
    }

    public void execute(){
        executed = true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        EventRecord that = (EventRecord) o;
        return eventId != null && Objects.equals(eventId, that.eventId);
    }

    @Override
    public int hashCode() {
        return 0;
    }
}
