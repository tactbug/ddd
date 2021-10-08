package com.tactbug.ddd.common.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;

import java.time.ZonedDateTime;
import java.util.Objects;

/**
 * @Author tactbug
 * @Email tactbug@Gmail.com
 * @Time 2021/9/28 19:41
 */
public class BaseAggregate implements Comparable<BaseAggregate>{

    @Id
    protected Long id;
    protected Integer version;
    @Transient
    protected boolean changed;
    protected ZonedDateTime createTime;
    protected ZonedDateTime updateTime;

    protected BaseAggregate(){
        this.version = 0;
        this.changed = false;
    }

    protected BaseAggregate(Long id){
        this.id = id;
        this.version = 1;
        this.changed = true;
        this.createTime = ZonedDateTime.now();
        this.updateTime = ZonedDateTime.now();
    }

    public void update(){
        this.changed = true;
        this.version += 1;
        this.updateTime = ZonedDateTime.now();
    }

    public void check() {
        if (Objects.isNull(id)){
            throw new IllegalArgumentException("主键不能为空");
        }
        if (Objects.isNull(version)){
            throw new IllegalArgumentException("版本号不能为空");
        }
        if (Objects.isNull(createTime)){
            throw new IllegalArgumentException("创建时间不能为空");
        }
        if (Objects.isNull(updateTime)){
            throw new IllegalArgumentException("更新时间不能为空");
        }
        if (updateTime.isBefore(createTime)){
            throw new IllegalArgumentException("更新时间不能早于创建时间");
        }
    }

    public void replay(Event<? extends BaseAggregate> event) {
        if (Objects.isNull(id)){
            this.id = event.getAggregateId();
        }
        this.version = event.getAggregateVersion();
        if (Objects.isNull(createTime)){
            this.createTime = event.getCreateTime();
        }
        this.updateTime = event.getCreateTime();
    }

    @Override
    public int compareTo(BaseAggregate o) {
        if (updateTime.isEqual(o.getUpdateTime())){
            return 0;
        }else if (updateTime.isBefore(o.getUpdateTime())){
            return -1;
        }else {
            return 1;
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getVersion() {
        return version;
    }

    public ZonedDateTime getCreateTime() {
        return createTime;
    }

    public ZonedDateTime getUpdateTime() {
        return updateTime;
    }

    public void setChanged(boolean changed) {
        this.changed = changed;
    }

    public boolean isChanged(){
        return changed;
    }
}
