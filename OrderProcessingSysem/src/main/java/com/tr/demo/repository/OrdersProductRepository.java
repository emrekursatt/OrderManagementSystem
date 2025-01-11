package com.tr.demo.repository;

import com.tr.demo.entity.OrdersProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface OrdersProductRepository extends JpaRepository<OrdersProductEntity, Long> {

    @Query("SELECT o FROM OrdersProductEntity o WHERE o.ordersEntity.customerEmail = :email")
    List<OrdersProductEntity> getOrderProductsByCustomerEmail(String email);
}