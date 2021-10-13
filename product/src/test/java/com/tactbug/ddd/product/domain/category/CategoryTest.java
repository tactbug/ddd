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

        Long createId = CATEGORY_ID_UTIL.getId();
        CategoryEvent create = category.createCategory(createId, createCategory.operator());
        categoryCommand.setId(category.getId());

        Long updateNameId = CATEGORY_ID_UTIL.getId();
        CategoryEvent updateName = category.updateName(updateNameId, categoryCommand.updateName());

        Long updateRemarkId = CATEGORY_ID_UTIL.getId();
        CategoryEvent updateRemark = category.updateRemark(updateRemarkId, categoryCommand.updateRemark());

        Long changeParentId = CATEGORY_ID_UTIL.getId();
        CategoryEvent changeParent = category.changeParent(changeParentId, categoryCommand.changeParent());

        events.add(create);
        events.add(updateName);
        events.add(updateRemark);
        events.add(changeParent);

        Category replay = Category.replay(events, null);
        assertEquals(category, replay);
    }
}