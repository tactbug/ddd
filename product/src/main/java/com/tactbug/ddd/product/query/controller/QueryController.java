package com.tactbug.ddd.product.query.controller;

import com.tactbug.ddd.common.entity.Result;
import com.tactbug.ddd.product.query.ProductQuery;
import com.tactbug.ddd.product.query.vo.CategoryVo;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/query")
public class QueryController {

    @Resource
    private ProductQuery productQuery;

    @GetMapping("/category")
    public Result<CategoryVo> getById(@RequestParam Long id){
        return Result.success(productQuery.getCategoryById(id));
    }
}
