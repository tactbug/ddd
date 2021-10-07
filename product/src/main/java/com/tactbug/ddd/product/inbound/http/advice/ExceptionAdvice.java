package com.tactbug.ddd.product.inbound.http.advice;

import com.tactbug.ddd.product.assist.exception.TactProductException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

/**
 * @Author tactbug
 * @Email tactbug@Gmail.com
 * @Time 2021/10/7 15:30
 */
@RestController
@ControllerAdvice
public class ExceptionAdvice {

    @ExceptionHandler(TactProductException.class)
    public Mono<String> tactException(TactProductException t){
        return Mono.just(t.getMessage());
    }

    @ExceptionHandler(IllegalStateException.class)
    public Mono<String> illegalStateException(IllegalStateException is){
        return Mono.just("数据异常:" + is.getMessage());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public Mono<String> illegalArgumentException(IllegalArgumentException ia){
        return Mono.just("参数异常:" + ia.getMessage());
    }
}
