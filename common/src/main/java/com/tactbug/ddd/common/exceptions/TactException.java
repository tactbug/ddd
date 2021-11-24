package com.tactbug.ddd.common.exceptions;


/**
 * @Author tactbug
 * @Email tactbug@Gmail.com
 * @Time 2021/10/6 22:59
 */
public class TactException extends RuntimeException{

    private final Throwable exception;

    public TactException(String pre, String info, Throwable e){
        super(pre + "|-" + info);
        this.exception = e;
    }

    public static TactException serializeOperateError(String message, Throwable e){
        return new TactException("序列化异常", message, e);
    }

    public static TactException resourceOperateError(String message, Throwable e){
        return new TactException("资源操作异常", message, e);
    }

    public static TactException replayError(String message, Throwable e){
        return new TactException("对象重放异常", message, e);
    }

    public static TactException eventOperateError(String message, Throwable e){
        return new TactException("事件操作异常", message, e);
    }

    public static TactException unKnowEnumError(String message, Throwable e){
        return new TactException("不支持的枚举类型", message, e);
    }

    public Throwable getException() {
        return exception;
    }
}
