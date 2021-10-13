package com.tactbug.ddd.product.domain.brand.command;

public record UpdateBrandName(Long id, String name, Long operator) {
}
