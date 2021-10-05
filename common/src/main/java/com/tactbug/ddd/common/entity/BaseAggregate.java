package com.tactbug.ddd.common.entity;

import java.time.ZonedDateTime;
import java.util.Objects;

/**
 * @Author tactbug
 * @Email tactbug@Gmail.com
 * @Time 2021/9/28 19:41
 */
public class BaseAggregate implements Comparable<BaseAggregate>{

    private Long id;
    private Integer version;
    private boolean changed;
    private ZonedDateTime createTime;
    private ZonedDateTime updateTime;

    protected BaseAggregate(Long id){
        this.id = id;
        this.version = 1;
        this.changed = true;
        this.createTime = ZonedDateTime.now();
        this.updateTime = ZonedDateTime.now();
    }

    public void update(){
        this.changed = true;
        this.updateTime = ZonedDateTime.now();
    }

    public boolean isChanged(){
        return changed;
    }

    public void check(){
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

    public Integer getVersion() {
        return version;
    }

    public ZonedDateTime getCreateTime() {
        return createTime;
    }

    public ZonedDateTime getUpdateTime() {
        return updateTime;
    }
}
