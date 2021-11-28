package com.tactbug.ddd.common.base;

import java.util.Objects;

/**
 * @Author tactbug
 * @Email tactbug@Gmail.com
 * @Time 2021/9/28 19:47
 */
public class BaseCommand {
    protected Long id;
    protected Integer version;

    protected void checkForUpdate(){
        checkId();
        if (Objects.isNull(version)){
            throw new IllegalArgumentException("版本不能为空");
        }
    }

    private void checkId(){
        if (Objects.isNull(id)){
            throw new IllegalArgumentException("id不能为空");
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
}
