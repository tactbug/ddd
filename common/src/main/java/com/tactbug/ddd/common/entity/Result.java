package com.tactbug.ddd.common.entity;

import java.util.Objects;

public record Result<T> (Integer code, Boolean success, String msg, String errorMsg, T data) {

    private static final Integer SUCCESS_CODE = 0;
    private static final String SUCCESS_MSG = "操作成功";

    public static <T> Result<T> succeed(){
        return new Result<>(SUCCESS_CODE, true, SUCCESS_MSG, "", null);
    }

    public static <T> Result<T> success(T t){
        return new Result<>(SUCCESS_CODE, true, SUCCESS_MSG, "", t);
    }

    public static <T> Result<T> fail(Integer code, String msg, String errorMsg){
        code = Objects.isNull(code) ? 9999 : code;
        return new Result<>(code, false, msg, errorMsg, null);
    }
}
