package com.example.accounts.repository;

import com.example.accounts.entity.AccountView;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AccountViewRepository
        extends JpaRepository<AccountView, String> {

    List<AccountView> findByClientId(String clientId);
}

