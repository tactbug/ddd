package com.tactbug.ddd.product.domain.category.command;

/**
 * @Author tactbug
 * @Email tactbug@Gmail.com
 * @Time 2021/10/7 16:57
 */
public record CategoryUpdateName(Long domainId, String name, Long operator) {
}
