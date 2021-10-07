package com.tactbug.ddd.product.aggregate.category;

import com.tactbug.ddd.common.entity.Event;
import com.tactbug.ddd.common.utils.IdUtil;
import com.tactbug.ddd.product.TactProductApplication;
import com.tactbug.ddd.product.aggregate.category.command.CategoryCommand;
import com.tactbug.ddd.product.aggregate.category.command.CreateCategory;
import com.tactbug.ddd.product.outbound.repository.traceability.category.CategoryTraceabilityRepository;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @Author tactbug
 * @Email tactbug@Gmail.com
 * @Time 2021/10/7 15:38
 */
class CategoryTest {

    private static final IdUtil CATEGORY_ID_UTIL = IdUtil.getOrGenerate(
            TactProductApplication.APPLICATION_NAME, Category.class, null, null, null
    );
    private static final IdUtil CATEGORY_EVENT_ID_UTIL = IdUtil.getOrGenerate(
            TactProductApplication.APPLICATION_NAME, CategoryTraceabilityRepository.class, null, null, null
    );


    @Test
    public void createTest(){

        CategoryCommand categoryCommand = new CategoryCommand();
        categoryCommand.setName("测试名称");
        categoryCommand.setRemark("测试备注");
        categoryCommand.setParentId(0L);
        categoryCommand.setOperator(999L);

        Long id = CATEGORY_ID_UTIL.getId();
        CreateCategory createCategory = categoryCommand.createCategory();
        Category category = Category.generate(id, createCategory);

        List<Event<Category>> events = new ArrayList<>();

        Long createId = CATEGORY_EVENT_ID_UTIL.getId();
        Event<Category> create = category.createCategory(createId, createCategory.operator());

        Long updateNameId = CATEGORY_EVENT_ID_UTIL.getId();
        Event<Category> updateName = category.updateName(updateNameId, categoryCommand.updateName());

        Long updateRemarkId = CATEGORY_EVENT_ID_UTIL.getId();
        Event<Category> updateRemark = category.updateRemark(updateRemarkId, categoryCommand.updateRemark());

        Long changeParentId = CATEGORY_EVENT_ID_UTIL.getId();
        Event<Category> changeParent = category.changeParent(changeParentId, categoryCommand.changeParent());

        events.add(create);
        events.add(updateName);
        events.add(updateRemark);
        events.add(changeParent);

        Category replay = Category.replay(events, null);
        assertEquals(category, replay);
    }
}