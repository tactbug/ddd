package com.tactbug.ddd.product.domain.category.command;

/**
 * @Author tactbug
 * @Email tactbug@Gmail.com
 * @Time 2021/10/7 15:17
 */
public record CreateCategory(String name, String remark, Long parentId, Long operator) {
}
