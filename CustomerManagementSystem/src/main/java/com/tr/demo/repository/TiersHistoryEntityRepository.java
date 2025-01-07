package com.tr.demo.repository;

import com.tr.demo.entity.TiersHistoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TiersHistoryEntityRepository extends JpaRepository<TiersHistoryEntity, Long> {
}