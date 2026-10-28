package com.poker.repository;

import com.poker.entity.HandPlayerEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HandPlayerRepository extends JpaRepository<HandPlayerEntity, Long> {
}
