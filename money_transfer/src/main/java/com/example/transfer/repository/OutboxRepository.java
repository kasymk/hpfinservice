package com.example.transfer.repository;

import com.example.transfer.domain.OutboxEvent;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface OutboxRepository
        extends JpaRepository<OutboxEvent, UUID> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
    select e from OutboxEvent e
    where e.status = 'NEW'
    order by e.createdAt
  """)
    List<OutboxEvent> findBatchForProcessing(Pageable pageable);
}

