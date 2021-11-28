package com.tactbug.ddd.product.inbound.http.brand;

import com.tactbug.ddd.common.base.Result;
import com.tactbug.ddd.product.domain.brand.Brand;
import com.tactbug.ddd.product.domain.brand.command.BrandCommand;
import com.tactbug.ddd.product.inbound.http.brand.vo.BrandVo;
import com.tactbug.ddd.product.service.brand.BrandService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Optional;

@RestController
@RequestMapping("/brand")
public class BrandController {
    @Resource
    private BrandService brandService;

    @PostMapping
    public Result<BrandVo> create(@RequestBody BrandCommand brandCommand){
        Brand brand = brandService.createBrand(brandCommand.createBrand());
        return Result.success(BrandVo.generate(brand));
    }

    @PutMapping
    public Result<BrandVo> update(@RequestBody BrandCommand brandCommand){
        Brand brand = brandService.update(brandCommand);
        return Result.success(BrandVo.generate(brand));
    }

    @DeleteMapping
    public Result<BrandVo> delete(@RequestBody BrandCommand brandCommand){
        Brand brand = brandService.delete(brandCommand)
                .orElse(new Brand());
        return Result.success(BrandVo.generate(brand));
    }

    @GetMapping
    public Result<BrandVo> getOne(Long id){
        Optional<Brand> optional = brandService.getById(id);
        return optional.map(brand -> Result.success(BrandVo.generate(brand))).orElseGet(Result::succeed);
    }
}
