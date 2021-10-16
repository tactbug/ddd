package com.tactbug.ddd.product.domain.category;

import com.tactbug.ddd.common.utils.IdUtil;
import com.tactbug.ddd.product.TactProductApplication;
import com.tactbug.ddd.product.domain.category.command.CategoryCommand;
import com.tactbug.ddd.product.domain.category.command.CreateCategory;
import com.tactbug.ddd.product.domain.category.event.CategoryEvent;
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

        List<CategoryEvent> events = new ArrayList<>();

        CategoryEvent create = category.createCategory(CATEGORY_ID_UTIL, createCategory.operator());
        categoryCommand.setId(category.getId());

        CategoryEvent updateName = category.updateName(CATEGORY_ID_UTIL, categoryCommand.updateName());

        CategoryEvent updateRemark = category.updateRemark(CATEGORY_ID_UTIL, categoryCommand.updateRemark());

        events.add(create);
        events.add(updateName);
        events.add(updateRemark);

        Category replay = Category.replay(null, events);
        assertEquals(category, replay);
    }
}