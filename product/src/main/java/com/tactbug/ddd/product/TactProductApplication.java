package com.tactbug.ddd.product;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.annotation.EnableKafka;

/**
 * @Author tactbug
 * @Email tactbug@Gmail.com
 * @Time 2021/10/7 0:09
 */
@SpringBootApplication
@EnableKafka
public class TactProductApplication {

    public static final String APPLICATION_NAME = "tact-product";

    public static void main(String[] args) {
        SpringApplication.run(TactProductApplication.class, args);
    }
}
