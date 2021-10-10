package com.tactbug.ddd.common.entity;

import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;
import java.time.ZonedDateTime;
import java.util.Objects;

/**
 * @Author tactbug
 * @Email tactbug@Gmail.com
 * @Time 2021/9/28 19:41
 */
@MappedSuperclass
public class BaseDomain implements Comparable<BaseDomain>{

    @Id
    protected Long id;
    protected Integer version;
    @Transient
    protected boolean changed;
    protected ZonedDateTime createTime;
    protected ZonedDateTime updateTime;
    protected boolean delFlag;

    protected BaseDomain(){
        this.version = 0;
        this.changed = false;
        this.delFlag = false;
    }

    protected BaseDomain(Long id){
        this.id = id;
        this.version = 1;
        this.changed = true;
        this.createTime = ZonedDateTime.now();
        this.updateTime = ZonedDateTime.now();
        this.delFlag = false;
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

    public void replay(Event<? extends BaseDomain> event) {
        if (Objects.isNull(id)){
            this.id = event.getDomainId();
        }
        this.version = event.getDomainVersion();
        if (Objects.isNull(createTime)){
            this.createTime = event.getCreateTime();
        }
        this.updateTime = event.getCreateTime();
    }

    @Override
    public int compareTo(BaseDomain o) {
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

    public void setVersion(Integer version) {
        this.version = version;
    }

    public boolean isChanged() {
        return changed;
    }

    public void setChanged(boolean changed) {
        this.changed = changed;
    }

    public ZonedDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(ZonedDateTime createTime) {
        this.createTime = createTime;
    }

    public ZonedDateTime getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(ZonedDateTime updateTime) {
        this.updateTime = updateTime;
    }

    public boolean isDelFlag() {
        return delFlag;
    }

    public void setDelFlag(boolean delFlag) {
        this.delFlag = delFlag;
    }
}
