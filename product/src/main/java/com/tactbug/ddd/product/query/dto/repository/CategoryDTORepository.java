package com.tactbug.ddd.product.query.dto.repository;

import com.tactbug.ddd.product.query.dto.CategoryDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryDTORepository extends JpaRepository<CategoryDTO, Long> {
}
