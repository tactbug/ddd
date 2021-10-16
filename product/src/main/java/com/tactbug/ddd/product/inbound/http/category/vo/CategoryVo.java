package com.tactbug.ddd.product.inbound.http.category.vo;

import com.tactbug.ddd.product.domain.category.Category;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.util.List;

@Data
public class CategoryVo {
    private Long id;
    private String name;
    private String remark;
    private Long parentId;
    private List<CategoryVo> children;

    public static CategoryVo generate(Category category){
        CategoryVo categoryVo = new CategoryVo();
        BeanUtils.copyProperties(category, categoryVo);
        return categoryVo;
    }
}
