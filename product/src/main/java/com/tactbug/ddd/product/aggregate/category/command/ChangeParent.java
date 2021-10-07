package com.tactbug.ddd.product.aggregate.category.command;

/**
 * @Author tactbug
 * @Email tactbug@Gmail.com
 * @Time 2021/10/7 16:58
 */
public record ChangeParent(Long parentId, Long operator) {
}
