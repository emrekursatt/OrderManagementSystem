package com.tr.demo.repository;

import com.tr.demo.entity.OrdersProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrdersProductRepository extends JpaRepository<OrdersProductEntity, Long> {
}