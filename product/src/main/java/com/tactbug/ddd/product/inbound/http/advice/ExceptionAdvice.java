package com.tactbug.ddd.product.inbound.http.advice;

import com.tactbug.ddd.common.base.Result;
import com.tactbug.ddd.common.exceptions.TactException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;

import java.sql.SQLException;
import java.util.Objects;

/**
 * @Author tactbug
 * @Email tactbug@Gmail.com
 * @Time 2021/10/7 15:30
 */
@RestController
@ControllerAdvice
@Slf4j
public class ExceptionAdvice {

    private static final Integer PRODUCT_EXCEPTION_CODE = 11001;

    @ExceptionHandler(TactException.class)
    public Result<String> tactException(TactException t){
        if (Objects.isNull(t.getException())){
            return Result.fail(PRODUCT_EXCEPTION_CODE, t.getMessage(), t.getMessage());
        }else {
            return Result.fail(PRODUCT_EXCEPTION_CODE, t.getMessage(), t.getException().getMessage());
        }
    }

    @ExceptionHandler(IllegalStateException.class)
    public Result<String> illegalStateException(IllegalStateException is){
        log.error("商品服务异常:", is);
        return Result.fail(PRODUCT_EXCEPTION_CODE, is.getMessage(), is.getMessage());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public Result<String> illegalArgumentException(IllegalArgumentException ia){
        log.error("商品服务异常:", ia);
        return Result.fail(PRODUCT_EXCEPTION_CODE, ia.getMessage(), ia.getMessage());
    }

    @ExceptionHandler(SQLException.class)
    public Result<String> sqlException(SQLException s){
        log.error("商品服务异常:", s);
        return Result.fail(PRODUCT_EXCEPTION_CODE, "数据库操作异常", s.getMessage());
    }

    @ExceptionHandler(Throwable.class)
    public Result<String> baseException(Throwable e){
        log.error("商品服务异常:", e);
        return Result.fail(null, "系统异常", e.getMessage());
    }
}
