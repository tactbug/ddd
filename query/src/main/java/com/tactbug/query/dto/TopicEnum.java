package com.tactbug.query.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.EnumSet;

@Getter
@AllArgsConstructor
public enum TopicEnum {
    CATEGORY("category"),
    ;

    private final String name;

    public static TopicEnum get(String name){
        for (TopicEnum t :
                EnumSet.allOf(TopicEnum.class)) {
            if (t.getName().equals(name)){
                return t;
            }
        }
        throw new IllegalStateException("不支持的枚举类型[" + name + "]");
    }
}
