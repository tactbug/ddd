package com.tactbug.ddd.product.inbound.http.category.response;

import com.tactbug.ddd.product.domain.category.Category;
import lombok.Data;
import org.springframework.beans.BeanUtils;

@Data
public class CategoryResponse {
    private Long id;
    private String name;
    private String remark;
    private Long parentId;
    private Integer version;

    public static CategoryResponse generate(Category category){
        CategoryResponse categoryResponse = new CategoryResponse();
        BeanUtils.copyProperties(category, categoryResponse);
        return categoryResponse;
    }
}
