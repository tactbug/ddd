package com.tactbug.ddd.product.inbound.http.category;

import com.tactbug.ddd.common.entity.Result;
import com.tactbug.ddd.product.domain.category.Category;
import com.tactbug.ddd.product.domain.category.command.CategoryCommand;
import com.tactbug.ddd.product.inbound.http.category.response.CategoryResponse;
import com.tactbug.ddd.product.service.category.CategoryService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @Author tactbug
 * @Email tactbug@Gmail.com
 * @Time 2021/10/7 15:25
 */
@RestController
@RequestMapping("/category")
public class CategoryController {

    @Resource
    private CategoryService categoryService;

    @PostMapping
    public Result<CategoryResponse> create(@RequestBody CategoryCommand categoryCommand){
        Category category = categoryService.createCategory(categoryCommand.createCategory());
        return Result.success(CategoryResponse.generate(category));
    }

    @PutMapping
    public Result<CategoryResponse> update(@RequestBody CategoryCommand categoryCommand){
        Category category = categoryService.updateCategory(categoryCommand);
        return Result.success(CategoryResponse.generate(category));
    }

    @DeleteMapping
    public Result<CategoryResponse> delete(@RequestBody CategoryCommand categoryCommand){
        Category category = categoryService.delete(categoryCommand.deleteCategory());
        return Result.success(CategoryResponse.generate(category));
    }

}
