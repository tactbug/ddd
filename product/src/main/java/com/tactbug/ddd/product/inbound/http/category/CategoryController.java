package com.tactbug.ddd.product.inbound.http.category;

import com.tactbug.ddd.common.entity.Result;
import com.tactbug.ddd.product.aggregate.category.Category;
import com.tactbug.ddd.product.aggregate.category.command.CategoryCommand;
import com.tactbug.ddd.product.inbound.http.category.vo.CategoryVo;
import com.tactbug.ddd.product.service.category.CategoryService;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;
import java.util.Optional;

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

    @PostMapping("/create")
    public Result<CategoryVo> create(@RequestBody CategoryCommand categoryCommand){
        Category category = categoryService.createCategory(categoryCommand.createCategory());
        return Result.success(CategoryVo.generate(category));
    }

    @PutMapping("/name")
    public Result<CategoryVo> updateName(@RequestBody CategoryCommand categoryCommand){
        Category category = categoryService.updateName(categoryCommand.updateName());
        return Result.success(CategoryVo.generate(category));
    }

    @GetMapping("/one")
    public Result<CategoryVo> getOne(Long id){
        Optional<Category> optional = categoryService.getById(id);
        return optional.map(category -> Result.success(CategoryVo.generate(category))).orElseGet(Result::succeed);
    }
}
