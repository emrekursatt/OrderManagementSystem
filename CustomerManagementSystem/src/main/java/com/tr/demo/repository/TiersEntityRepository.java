package com.tr.demo.repository;

import com.tr.demo.entity.TiersEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TiersEntityRepository extends JpaRepository<TiersEntity, Long> {
}