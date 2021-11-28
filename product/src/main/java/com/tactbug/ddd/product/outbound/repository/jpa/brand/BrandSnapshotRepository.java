package com.tactbug.ddd.product.outbound.repository.jpa.brand;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.tactbug.ddd.common.base.BaseDomain;
import com.tactbug.ddd.common.utils.SerializeUtil;
import com.tactbug.ddd.common.exceptions.TactException;
import com.tactbug.ddd.product.domain.brand.Brand;
import com.tactbug.ddd.product.domain.category.Category;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Objects;
import java.util.Optional;

@Component
public class BrandSnapshotRepository {
    @Resource
    private RedisTemplate<Class<? extends BaseDomain>, Brand> redisTemplate;

    public Optional<Brand> findById(Long id){
        Object hashValue = redisTemplate.opsForHash().get(Brand.class, id);
        if (Objects.nonNull(hashValue)){
            try {
                Brand brand = SerializeUtil.jsonToObject(hashValue.toString(), new TypeReference<>() {
                });
                return Objects.nonNull(brand) ? Optional.of(brand) : Optional.empty();
            }catch (JacksonException j){
                throw TactException.serializeOperateError(hashValue.toString(), j);
            }
        }
        return Optional.empty();
    }

    public void save(Brand brand){
        try {
            String value = SerializeUtil.objectToJson(brand);
            redisTemplate.opsForHash().put(Category.class, brand.getId(), value);
        } catch (JsonProcessingException e) {
            throw TactException.serializeOperateError(brand.toString(), e);
        }
    }
}
