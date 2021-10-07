package com.tactbug.ddd.product.aggregate.category.command;

import lombok.Setter;

import java.util.Objects;

/**
 * @Author tactbug
 * @Email tactbug@Gmail.com
 * @Time 2021/10/7 15:17
 */
@Setter
public class CategoryCommand {

    private String name;
    private String remark;
    private Long parentId;
    private Long operator = 10000L;

    public CreateCategory createCategory(){
        checkCreateCategory();
        return new CreateCategory(name, remark, parentId, operator);
    }

    public UpdateName updateName(){
        checkUpdateName();
        return new UpdateName(name, operator);
    }

    public UpdateRemark updateRemark(){
        checkUpdateRemark();
        return new UpdateRemark(remark, operator);
    }

    public ChangeParent changeParent(){
        checkUpdateRemark();
        return new ChangeParent(parentId, operator);
    }

    private void checkCreateCategory(){
        if (Objects.isNull(name) || name.isBlank()){
            throw new IllegalArgumentException("分类名称不能为空");
        }
    }

    private void checkUpdateName(){
        if (Objects.isNull(name) || name.isBlank()){
            throw new IllegalArgumentException("分类名称不能为空");
        }
    }

    private void checkUpdateRemark(){
        if (Objects.isNull(remark)){
            remark = "";
        }
    }

    private void checkChangeParent(){
        if (Objects.isNull(parentId)){
            throw new IllegalArgumentException("必须指定父分类");
        }
    }
}
