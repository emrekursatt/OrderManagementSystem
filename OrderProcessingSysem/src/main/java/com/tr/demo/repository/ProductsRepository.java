package com.tr.demo.repository;

import com.tr.demo.entity.ProductsEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProductsRepository extends JpaRepository<ProductsEntity, Long> {

    Optional<ProductsEntity> findByName(String productName);
}