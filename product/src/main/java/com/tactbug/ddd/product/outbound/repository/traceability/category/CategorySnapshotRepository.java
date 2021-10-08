package com.tactbug.ddd.product.outbound.repository.traceability.category;

import com.tactbug.ddd.product.aggregate.category.Category;
import com.tactbug.ddd.product.assist.exception.TactProductException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;

import static org.springframework.data.relational.core.query.Criteria.where;
import static org.springframework.data.relational.core.query.Query.query;

/**
 * @Author tactbug
 * @Email tactbug@Gmail.com
 * @Time 2021/10/5 18:47
 */
@Repository
@Slf4j
public class CategorySnapshotRepository {
    @Resource
    private R2dbcEntityTemplate r2dbcEntityTemplate;

    public Mono<Category> save(Category category){
        return findById(category.getId())
                .doOnNext(c -> r2dbcEntityTemplate.update(category))
                .switchIfEmpty(r2dbcEntityTemplate.insert(category))
                .doOnError(e -> {
                    log.error("快照[" + category + "]保存失败", e);
                    throw TactProductException.resourceOperateError("快照[" + category + "]保存失败");
                });
    }

    public Mono<Category> findById(Long id){
        return r2dbcEntityTemplate.select(Category.class)
                .from("category_snapshot")
                .matching(query(
                        where("id")
                                .is(id)
                                .and("delFlag").isFalse()
                        )
                )
                .one();
    }
}
