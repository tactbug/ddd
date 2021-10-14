package com.tactbug.ddd.product.domain.brand.command;

import com.tactbug.ddd.common.entity.BaseCommand;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Objects;

@Data
@EqualsAndHashCode(callSuper = true)
public class BrandCommand extends BaseCommand {

    private String name;
    private String remark;
    private Long operator = 10000L;

    public CreateBrand createBrand(){
        checkCreateBrand();
        return new CreateBrand(name, remark, operator);
    }

    public UpdateBrandName updateName(){
        checkUpdateName();
        return new UpdateBrandName(id, name, operator);
    }

    public UpdateBrandRemark updateRemark(){
        checkUpdateRemark();
        return new UpdateBrandRemark(id, remark, operator);
    }

    public DeleteBrand deleteBrand(){
        checkId();
        return new DeleteBrand(id, operator);
    }

    private void checkCreateBrand(){
        checkRemark();
        if (Objects.isNull(name) || name.isBlank()){
            throw new IllegalArgumentException("品牌名称不能为空");
        }
    }

    private void checkUpdateName(){
        checkId();
        if (Objects.isNull(name) || name.isBlank()){
            throw new IllegalArgumentException("品牌名称不能为空");
        }
    }

    private void checkUpdateRemark(){
        checkId();
        checkRemark();
    }

    private void checkRemark(){
        remark = Objects.isNull(remark) ? "" : remark;
    }
}
