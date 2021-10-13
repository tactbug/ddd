package com.tactbug.ddd.product.domain.brand.command;

public record CreateBrand(String name, String remark, Long operator) {
}
