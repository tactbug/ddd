package com.tactbug.ddd.product.domain.category.command;

import java.util.Collection;

public record RemoveChild(Long id, Collection<Long> childrenIds, Long operator) {
}
