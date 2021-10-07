package com.tactbug.ddd.product.outbound.repository.traceability.category;

import com.tactbug.ddd.common.entity.Event;
import com.tactbug.ddd.product.aggregate.category.Category;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;

/**
 * @Author tactbug
 * @Email tactbug@Gmail.com
 * @Time 2021/10/5 18:52
 */
@Repository
public class CategoryEventRepository{

    @Resource
    private R2dbcEntityTemplate r2dbcEntityTemplate;

    public Mono<Category> save(Event<Category> event){

    }

}
