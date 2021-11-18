package com.tactbug.ddd.product.assist.exception;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * @Author tactbug
 * @Email tactbug@Gmail.com
 * @Time 2021/10/6 22:59
 */
@Getter
public class TactProductException extends RuntimeException{

    private final Throwable exception;

    public TactProductException(String pre, String info, Throwable e){
        super(pre + "|-" + info);
        this.exception = e;
    }

    public static TactProductException jsonOperateError(String message, Throwable e){
        return new TactProductException("对象序列化异常", message, e);
    }

    public static TactProductException resourceOperateError(String message, Throwable e){
        return new TactProductException("资源操作异常", message, e);
    }

    public static TactProductException replayError(String message, Throwable e){
        return new TactProductException("对象重放异常", message, e);
    }

    public static TactProductException eventOperateError(String message, Throwable e){
        return new TactProductException("事件操作异常", message, e);
    }

    public static TactProductException unKnowEnumError(String message, Throwable e){
        return new TactProductException("不支持的枚举类型", message, e);
    }
}
