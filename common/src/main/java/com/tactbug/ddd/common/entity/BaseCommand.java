package com.tactbug.ddd.common.entity;

import java.util.Objects;

/**
 * @Author tactbug
 * @Email tactbug@Gmail.com
 * @Time 2021/9/28 19:47
 */
public class BaseCommand {
    protected Long id;

    protected void checkId(){
        if (Objects.isNull(id)){
            throw new IllegalArgumentException("id不能为空");
        }
    }
}
