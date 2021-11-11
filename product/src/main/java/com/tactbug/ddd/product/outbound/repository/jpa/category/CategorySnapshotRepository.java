package com.tactbug.ddd.product.outbound.repository.jpa.category;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.tactbug.ddd.common.entity.BaseDomain;
import com.tactbug.ddd.common.utils.SerializeUtil;
import com.tactbug.ddd.product.assist.exception.TactProductException;
import com.tactbug.ddd.product.domain.category.Category;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.*;

/**
 * @Author tactbug
 * @Email tactbug@Gmail.com
 * @Time 2021/10/5 18:47
 */
@Component
public class CategorySnapshotRepository {

    @Resource
    private RedisTemplate<Class<? extends BaseDomain>, Category> redisTemplate;

    public Optional<Category> findById(Long id){
        Object hashValue = redisTemplate.opsForHash().get(Category.class, id);
        if (Objects.nonNull(hashValue)){
            try {
                Category category = SerializeUtil.jsonToObject(hashValue.toString(), new TypeReference<>() {
                });
                return Objects.nonNull(category) ? Optional.of(category) : Optional.empty();
            }catch (JacksonException j){
                throw TactProductException.jsonException(j);
            }
        }
        return Optional.empty();
    }

    public void save(Category category){
        try {
            String value = SerializeUtil.objectToJson(category);
            redisTemplate.opsForHash().put(Category.class, category.getId(), value);
        } catch (JsonProcessingException e) {
            throw TactProductException.jsonException(e);
        }
    }

    public void delete(Long id){
        redisTemplate.opsForHash().delete(Category.class, id);
    }
}
