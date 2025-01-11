package com.tr.demo.repository;

import com.tr.demo.entity.CustomerEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CustomerEntityRepository extends JpaRepository<CustomerEntity, Long> {
  Optional<CustomerEntity> findByUsername(String username);

  List<CustomerEntity> findAllByOrderCount(int orderCount);
}