package com.tactbug.ddd.product.domain.category.command;

/**
 * @Author tactbug
 * @Email tactbug@Gmail.com
 * @Time 2021/10/7 16:58
 */
public record CategoryChangeParent(Long id, Long parentId, Long operator) {
}
