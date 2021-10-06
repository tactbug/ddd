package com.tactbug.ddd.product.service.category;

import com.tactbug.ddd.common.utils.IdUtil;
import com.tactbug.ddd.product.TactProductApplication;
import com.tactbug.ddd.product.aggregate.category.Category;
import com.tactbug.ddd.product.assist.exception.TactProductException;
import com.tactbug.ddd.product.outbound.repository.traceability.category.CategoryTraceabilityRepository;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Objects;
import java.util.Optional;

/**
 * @Author tactbug
 * @Email tactbug@Gmail.com
 * @Time 2021/10/5 17:22
 */
@Service
public class CategoryService {

    @Resource
    private CategoryTraceabilityRepository categoryTraceabilityRepository;

    private static final IdUtil CATEGORY_ID_UTIL = IdUtil.getOrGenerate(
            TactProductApplication.APPLICATION_NAME, Category.class, 50000, 5000, 10000
    );

    public Category createCategory(String name, String remark, Long parentId, Long operator){
        parentId = Objects.isNull(parentId) || parentId.equals(0L) ? 0L : parentId;
        if (!parentId.equals(0L)){
            Optional<Category> parentOptional = categoryTraceabilityRepository.getOneById(parentId);
            if (parentOptional.isEmpty()){
                throw TactProductException.resourceNotExists("父分类[" + parentId + "]不存在");
            }
        }
        Long id = CATEGORY_ID_UTIL.getId();
        Category category = Category.generate(id, name, remark, parentId);
        categoryTraceabilityRepository.create(category, operator);
        return category;
    }
}
