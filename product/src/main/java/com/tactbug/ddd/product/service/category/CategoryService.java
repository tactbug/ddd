package com.tactbug.ddd.product.service.category;

import com.tactbug.ddd.common.utils.IdUtil;
import com.tactbug.ddd.product.TactProductApplication;
import com.tactbug.ddd.product.aggregate.category.Category;
import com.tactbug.ddd.product.aggregate.category.command.CreateCategory;
import com.tactbug.ddd.product.aggregate.category.command.UpdateName;
import com.tactbug.ddd.product.aggregate.category.event.NameUpdated;
import com.tactbug.ddd.product.assist.exception.TactProductException;
import com.tactbug.ddd.product.outbound.repository.jpa.category.CategoryRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

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
    private CategoryRepository categoryRepository;

    private static final IdUtil CATEGORY_ID_UTIL = IdUtil.getOrGenerate(
            TactProductApplication.APPLICATION_NAME, Category.class, 50000, 5000, 10000
    );

    public Category createCategory(CreateCategory createCategory){
        Long parentId = Objects.isNull(createCategory.parentId()) ? 0L : createCategory.parentId();
        if (!parentId.equals(0L)){
            categoryRepository.getOneById(parentId)
                    .orElseThrow(() -> TactProductException.resourceOperateError("父分类[" + parentId + "]不存在"));
        }
        Category category = Category.generate(CATEGORY_ID_UTIL.getId(), createCategory);
        categoryRepository.create(category, createCategory.operator());
        return category;
    }

    public Category updateName(UpdateName updateName){
        Category category = categoryRepository.getOneById(updateName.id())
                .orElseThrow(() -> TactProductException.resourceOperateError("分类[" + updateName.id() + "]不存在"));
        NameUpdated nameUpdated = category.updateName(CATEGORY_ID_UTIL.getId(), updateName);
        categoryRepository.update(nameUpdated);
        return category;
    }

    public Optional<Category> getById(Long id){
        return categoryRepository.getOneById(id);
    }
}
