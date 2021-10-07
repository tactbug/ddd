package com.tactbug.ddd.product.service.category;

import com.tactbug.ddd.common.utils.IdUtil;
import com.tactbug.ddd.product.TactProductApplication;
import com.tactbug.ddd.product.aggregate.category.Category;
import com.tactbug.ddd.product.aggregate.category.command.CreateCategory;
import com.tactbug.ddd.product.assist.exception.TactProductException;
import com.tactbug.ddd.product.outbound.repository.traceability.category.CategoryTraceabilityRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;
import java.util.Objects;

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

    public Mono<Category> createCategory(CreateCategory createCategory){
        Long parentId = createCategory.parentId();
        parentId = Objects.isNull(parentId) ? 0L : parentId;
        if (!parentId.equals(0L)){
            Long finalParentId = parentId;
            categoryTraceabilityRepository.getOneById(finalParentId)
                    .subscribe(
                            r -> {
                                if (r.equals(Category.EMPTY)) {
                                    throw TactProductException.resourceOperateError("上级分类[" + finalParentId + "]不存在");
                                }
                            },
                            e -> {
                                throw TactProductException.resourceOperateError("分类[" + finalParentId + "]查询异常");
                            }
                    );
        }
        Long id = CATEGORY_ID_UTIL.getId();
        Category category = Category.generate(id, createCategory);
        return categoryTraceabilityRepository.create(category, createCategory.operator());
    }
}
