package com.tactbug.ddd.product.query.handler;

import com.fasterxml.jackson.core.type.TypeReference;
import com.tactbug.ddd.common.utils.SerializeUtil;
import com.tactbug.ddd.product.assist.exception.TactProductException;
import com.tactbug.ddd.product.domain.category.Category;
import com.tactbug.ddd.product.domain.category.CategoryEvent;
import com.tactbug.ddd.product.domain.category.event.CategoryCreated;
import com.tactbug.ddd.product.domain.category.event.CategoryNameUpdated;
import com.tactbug.ddd.product.domain.category.event.CategoryParentChanged;
import com.tactbug.ddd.product.domain.category.event.CategoryRemarkUpdated;
import com.tactbug.ddd.product.outbound.repository.jpa.category.CategoryRepository;
import com.tactbug.ddd.product.query.vo.CategoryVo;
import com.tactbug.ddd.product.query.vo.repository.CategoryVoRepository;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

@Component
public class CategoryHandler {

    @Resource
    private CategoryVoRepository categoryVoRepository;
    @Resource
    private CategoryRepository categoryRepository;

    public void accept(CategoryEvent categoryEvent){
        switch (categoryEvent){
            case CategoryCreated c -> acceptCreate(c);
            case CategoryNameUpdated c -> acceptUpdateName(c);
            case CategoryRemarkUpdated c -> acceptRemarkUpdated(c);
            case CategoryParentChanged c -> acceptParentChanged(c);
            default -> throw new IllegalStateException("Unexpected value: " + categoryEvent);
        }
    }

    private void acceptCreate(CategoryCreated categoryCreated){
        Long parentId;
        try {
            HashMap<String, Object> dataMap = SerializeUtil.jsonToObject(categoryCreated.getData(), new TypeReference<>() {
            });
            parentId = Long.valueOf(dataMap.get("parentId").toString());
        } catch (Exception e) {
            throw TactProductException.replayError("[" + categoryCreated.getData() + "]视图基础信息构建异常", e);
        }
        CategoryVo categoryVo = new CategoryVo();
        categoryCreated.doAccept(categoryVo);
        CategoryVo parentVo = new CategoryVo();
        if (!parentId.equals(Category.ROOT_CATEGORY_ID)){
            Category parent = categoryRepository.getOne(parentId);
            parentVo.initBase(parent);
        }else {
            parentVo.initBase(Category.ROOT_CATEGORY);
        }
        categoryVo.setParent(parentVo);
        categoryVoRepository.save(categoryVo);
    }

    private void acceptUpdateName(CategoryNameUpdated categoryNameUpdated){
        CategoryVo categoryVo = categoryVoRepository.findById(categoryNameUpdated.getDomainId())
                .orElseThrow(() -> TactProductException.resourceOperateError("分类视图[" + categoryNameUpdated.getDomainId() + "]不存在", null));
        categoryVo.acceptNameUpdated(categoryNameUpdated);
        categoryVoRepository.save(categoryVo);
    }

    private void acceptRemarkUpdated(CategoryRemarkUpdated categoryRemarkUpdated){
        CategoryVo categoryVo = categoryVoRepository.findById(categoryRemarkUpdated.getDomainId())
                .orElseThrow(() -> TactProductException.resourceOperateError("分类视图[" + categoryRemarkUpdated.getDomainId() + "]不存在", null));
        categoryVo.acceptRemarkUpdated(categoryRemarkUpdated);
        categoryVoRepository.save(categoryVo);
    }

    private void acceptParentChanged(CategoryParentChanged categoryParentChanged){
        CategoryVo categoryVo = categoryVoRepository.findById(categoryParentChanged.getDomainId())
                .orElseThrow(() -> TactProductException.resourceOperateError("分类视图[" + categoryParentChanged.getDomainId() + "]不存在", null));
        try {
            Map<String, Object> data = SerializeUtil.jsonToObject(categoryParentChanged.getData(), new TypeReference<>() {
            });
            Long parentId = Long.valueOf(data.get("parentId").toString());
            CategoryVo parent;
            if (Category.ROOT_CATEGORY_ID.equals(parentId)){
                parent = new CategoryVo();
                parent.initBase(Category.ROOT_CATEGORY);
            }else {
                parent = categoryVoRepository.findById(parentId)
                        .orElseThrow(() -> TactProductException.resourceOperateError("父分类视图[" + parentId + "]已删除或不存在", null));
            }
            categoryVo.acceptParentChanged(categoryParentChanged, parent);
            categoryVoRepository.save(categoryVo);
        } catch (Exception e) {
            throw TactProductException.replayError("[" + categoryParentChanged.getData() + "]视图基础信息构建异常", e);
        }
    }
}
