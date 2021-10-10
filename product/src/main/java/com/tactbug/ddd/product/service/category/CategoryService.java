package com.tactbug.ddd.product.service.category;

import com.tactbug.ddd.common.utils.IdUtil;
import com.tactbug.ddd.product.TactProductApplication;
import com.tactbug.ddd.product.domain.category.Category;
import com.tactbug.ddd.product.domain.category.CategoryEvent;
import com.tactbug.ddd.product.domain.category.command.CategoryCommand;
import com.tactbug.ddd.product.domain.category.command.CreateCategory;
import com.tactbug.ddd.product.domain.category.command.UpdateName;
import com.tactbug.ddd.product.domain.category.event.CategoryDeleted;
import com.tactbug.ddd.product.domain.category.event.NameUpdated;
import com.tactbug.ddd.product.assist.exception.TactProductException;
import com.tactbug.ddd.product.outbound.repository.jpa.category.CategoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * @Author tactbug
 * @Email tactbug@Gmail.com
 * @Time 2021/10/5 17:22
 */
@Service
@Transactional
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
        categoryRepository.update(category, Collections.singletonList(nameUpdated));
        return category;
    }

    public Category update(CategoryCommand categoryCommand){
        Category category = categoryRepository.getOneById(categoryCommand.getId())
                .orElseThrow(() -> TactProductException.resourceOperateError("分类[" + categoryCommand.getId() + "]不存在"));
        List<CategoryEvent> events = category.update(categoryCommand, CATEGORY_ID_UTIL);
        if (!events.isEmpty()){
            categoryRepository.update(category, events);
        }
        return category;
    }

    public Optional<Category> delete(CategoryCommand categoryCommand){
        Optional<Category> optional = categoryRepository.getOneById(categoryCommand.getId())
                .or(Optional::empty);
        if (optional.isPresent()){
            Category category = optional.get();
            CategoryDeleted event = category.delete(CATEGORY_ID_UTIL.getId(), categoryCommand.deleteCategory());
            categoryRepository.delete(category, event);
            optional = Optional.of(category);
        }
        return optional;
    }

    public Optional<Category> getById(Long id){
        return categoryRepository.getOneById(id);
    }
}
