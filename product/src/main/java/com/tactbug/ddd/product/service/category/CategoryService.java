package com.tactbug.ddd.product.service.category;

import com.tactbug.ddd.common.utils.IdUtil;
import com.tactbug.ddd.product.TactProductApplication;
import com.tactbug.ddd.product.assist.exception.TactProductException;
import com.tactbug.ddd.product.domain.category.Category;
import com.tactbug.ddd.product.domain.category.command.CategoryCommand;
import com.tactbug.ddd.product.domain.category.command.CreateCategory;
import com.tactbug.ddd.product.domain.category.command.DeleteCategory;
import com.tactbug.ddd.product.domain.category.event.CategoryEvent;
import com.tactbug.ddd.product.outbound.publisher.EventPublisher;
import com.tactbug.ddd.product.outbound.publisher.EventTopics;
import com.tactbug.ddd.product.outbound.repository.jpa.category.CategoryRepository;
import com.tactbug.ddd.product.query.ProductQuery;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
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
    @Resource
    private ProductQuery productQuery;

    private static final IdUtil ID_UTIL = IdUtil.getOrGenerate(
            TactProductApplication.APPLICATION_NAME, Category.class, 50000, 5000, 10000
    );

    public Category createCategory(CreateCategory createCategory){
        List<CategoryEvent> events = new ArrayList<>();
        Category category = Category.generate(ID_UTIL, createCategory, events);
        if (!category.getParentId().equals(Category.ROOT_CATEGORY_ID)){
            categoryRepository.getOne(category.getParentId());
        }
        categoryRepository.execute(events);
        eventPublisher.publish(events, EventTopics.CATEGORY);
        return category;
    }

    public Category delete(DeleteCategory deleteCategory){
        Category category = categoryRepository.getOne(deleteCategory.dimainId());
        List<CategoryEvent> events = category.delete(ID_UTIL, deleteCategory, productQuery);
        categoryRepository.execute(events);
        eventPublisher.publish(events, EventTopics.CATEGORY);
        return category;
    }

    public Category getById(Long id){
        return categoryRepository.getOne(id);
    }

}
