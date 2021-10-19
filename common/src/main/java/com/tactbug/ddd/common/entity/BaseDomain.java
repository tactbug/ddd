package com.tactbug.ddd.common.entity;

import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
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
    protected ZonedDateTime createTime;
    protected ZonedDateTime updateTime;

    protected BaseDomain(){
        this.version = 0;
    }

    protected BaseDomain(Long id){
        this.id = id;
        this.version = 1;
        this.createTime = ZonedDateTime.now();
        this.updateTime = ZonedDateTime.now();
    }

    public void update(){
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

}
