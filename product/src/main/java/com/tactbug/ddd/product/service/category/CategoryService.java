package com.tactbug.ddd.product.service.category;

import com.tactbug.ddd.common.utils.IdUtil;
import com.tactbug.ddd.product.TactProductApplication;
import com.tactbug.ddd.product.assist.exception.TactProductException;
import com.tactbug.ddd.product.domain.category.Category;
import com.tactbug.ddd.product.domain.category.command.CategoryCommand;
import com.tactbug.ddd.product.domain.category.command.CreateCategory;
import com.tactbug.ddd.product.domain.category.event.CategoryEvent;
import com.tactbug.ddd.product.outbound.publisher.EventPublisher;
import com.tactbug.ddd.product.outbound.publisher.EventTopics;
import com.tactbug.ddd.product.outbound.repository.jpa.category.CategoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * @Author tactbug
 * @Email tactbug@Gmail.com
 * @Time 2021/10/5 17:22
 */
@Service
@Transactional(rollbackFor = Throwable.class)
public class CategoryService {

    @Resource
    private CategoryRepository categoryRepository;
    @Resource
    private EventPublisher eventPublisher;

    private static final IdUtil ID_UTIL = IdUtil.getOrGenerate(
            TactProductApplication.APPLICATION_NAME, Category.class, 50000, 5000, 10000
    );

    public Category createCategory(CreateCategory createCategory){
        Category parent = categoryRepository.getOne(createCategory.parentId())
                .orElseThrow(() -> TactProductException.resourceOperateError("父分类[" + createCategory.parentId() + "]不存在"));
        Category category = Category.generate(ID_UTIL.getId(), createCategory);
        List<CategoryEvent> events = category.createCategory(ID_UTIL, parent, createCategory.operator());
        category.check();
        categoryRepository.create(events, category);
        eventPublisher.publish(events, EventTopics.CATEGORY);
        return category;
    }


    public Optional<Category> delete(CategoryCommand categoryCommand){
        Optional<Category> optional = categoryRepository.getOne(categoryCommand.getId())
                .or(Optional::empty);
        if (optional.isPresent()){
            Category category = optional.get();
            List<CategoryEvent> events = category.delete(ID_UTIL, categoryCommand.deleteCategory(), categoryRepository);
            categoryRepository.delete(category, events);
            optional = Optional.of(category);
            eventPublisher.publish(events, EventTopics.CATEGORY);
        }
        return optional;
    }

    public Optional<Category> getById(Long id){
        return categoryRepository.getOne(id);
    }

}
