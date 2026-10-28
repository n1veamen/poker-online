package com.poker.repository;

import com.poker.entity.HandEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HandRepository extends JpaRepository<HandEntity, Long> {
}
