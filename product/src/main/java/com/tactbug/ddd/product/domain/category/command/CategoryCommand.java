package com.tactbug.ddd.product.domain.category.command;

import com.tactbug.ddd.common.entity.BaseCommand;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Objects;

/**
 * @Author tactbug
 * @Email tactbug@Gmail.com
 * @Time 2021/10/7 15:17
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class CategoryCommand extends BaseCommand {

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
        return new UpdateName(id, name, operator);
    }

    public UpdateRemark updateRemark(){
        checkUpdateRemark();
        return new UpdateRemark(id, remark, operator);
    }

    public ChangeParent changeParent(){
        checkChangeParent();
        return new ChangeParent(id, parentId, operator);
    }

    public DeleteCategory deleteCategory(){
        checkDeleteCategory();
        return new DeleteCategory(id, operator);
    }

    private void checkCreateCategory(){
        if (Objects.isNull(name) || name.isBlank()){
            throw new IllegalArgumentException("分类名称不能为空");
        }
    }

    private void checkUpdateName(){
        checkId();
        if (Objects.isNull(name) || name.isBlank()){
            throw new IllegalArgumentException("分类名称不能为空");
        }
    }

    private void checkUpdateRemark(){
        checkId();
        if (Objects.isNull(remark)){
            remark = "";
        }
    }

    private void checkChangeParent(){
        checkId();
        if (Objects.isNull(parentId)){
            throw new IllegalArgumentException("必须指定父分类");
        }
    }

    private void checkDeleteCategory(){
        checkId();
    }

}
