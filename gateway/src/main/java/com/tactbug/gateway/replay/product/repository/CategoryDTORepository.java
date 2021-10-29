package com.tactbug.gateway.replay.product.repository;

import com.tactbug.gateway.dto.product.CategoryDTO;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryDTORepository extends MongoRepository<CategoryDTO, Long> {
}
