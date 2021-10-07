package com.tactbug.ddd.product.inbound.http.category;

import com.tactbug.ddd.product.aggregate.category.Category;
import com.tactbug.ddd.product.aggregate.category.command.CategoryCommand;
import com.tactbug.ddd.product.service.category.CategoryService;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

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

    @PostMapping("/saveOne")
    public Mono<Category> saveOne(@RequestBody CategoryCommand categoryCommand){
        return categoryService.createCategory(categoryCommand.createCategory());
    }
}
