package com.tactbug.ddd.product.query.vo.repository;

import com.tactbug.ddd.product.query.vo.CategoryVo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryVoRepository extends JpaRepository<CategoryVo, Long> {
}
