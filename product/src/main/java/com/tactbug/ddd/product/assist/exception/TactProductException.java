package com.tactbug.ddd.product.assist.exception;

import com.fasterxml.jackson.core.JacksonException;

/**
 * @Author tactbug
 * @Email tactbug@Gmail.com
 * @Time 2021/10/6 22:59
 */
public class TactProductException extends RuntimeException{
    public TactProductException(String pre, String info){
        super(pre + "|-" + info);
    }

    public static TactProductException jsonException(JacksonException jacksonException){
        return new TactProductException("json解析异常", jacksonException.getMessage());
    }

    public static TactProductException resourceOperateError(String message){
        return new TactProductException("资源操作异常", message);
    }

    public static TactProductException replyError(String message){
        return new TactProductException("对象重放异常", message);
    }
}
