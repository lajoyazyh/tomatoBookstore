package com.example.tomatomall.repository;

import com.example.tomatomall.po.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface SpecificationRepository extends JpaRepository<Specification, Integer> {
    List<Specification> findByProductId(Integer productId);

    @Transactional
    void deleteByProductId(Integer productId);
}
