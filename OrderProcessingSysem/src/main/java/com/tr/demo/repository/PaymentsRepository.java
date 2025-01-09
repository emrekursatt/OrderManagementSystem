package com.tr.demo.repository;

import com.tr.demo.entity.PaymentsEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaymentsRepository extends JpaRepository<PaymentsEntity, Long> {

    Optional<PaymentsEntity> findByPaymentMethod(String paymentMethod);
}