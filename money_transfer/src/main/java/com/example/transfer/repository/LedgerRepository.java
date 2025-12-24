package com.example.transfer.repository;

import com.example.transfer.domain.LedgerEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;
import java.util.UUID;

public interface LedgerRepository extends JpaRepository<LedgerEntry, UUID> {

    @Query("""
    select coalesce(sum(l.amount), 0)
    from LedgerEntry l
    where l.accountId = :accountId
  """)
    BigDecimal calculateBalance(UUID accountId);
}

