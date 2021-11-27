package com.tactbug.ddd.product.query;

import com.tactbug.ddd.product.query.vo.CategoryVo;
import com.tactbug.ddd.product.query.vo.repository.CategoryVoRepository;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class ProductQuery {

    @Resource
    private CategoryVoRepository categoryVoRepository;

    public CategoryVo getCategoryById(Long id){
        return categoryVoRepository.findById(id)
                .orElse(new CategoryVo());
    }
}
