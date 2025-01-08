package com.tr.demo.repository;

import com.tr.demo.entity.TiersEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TiersEntityRepository extends JpaRepository<TiersEntity, Long> {
    Optional<TiersEntity> findByName(String name);
}