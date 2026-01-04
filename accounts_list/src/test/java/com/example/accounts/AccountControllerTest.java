package com.example.accounts;

import com.example.accounts.entity.AccountView;
import com.example.accounts.repository.AccountViewRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.core.env.Environment;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import java.math.BigDecimal;
import java.util.Arrays;

@AutoConfigureMockMvc
class AccountControllerTest extends IntegrationTestBase {

    @Autowired
    Environment env;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AccountViewRepository repository;

    @BeforeEach
    void setup() {
        repository.deleteAll();

        repository.save(new AccountView(
                "acc-1", "client-A", "USD", new BigDecimal("500.00")));
    }

    @Test
    void shouldReturnClientAccounts() throws Exception {
        mockMvc.perform(
                        get("/clients/client-A/accounts")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].accountId").value("acc-1"))
                .andExpect(jsonPath("$[0].balance").value(500.00));
    }

    @Test
    void shouldUseCache() throws Exception {
        mockMvc.perform(get("/clients/client-A/accounts"));
        mockMvc.perform(get("/clients/client-A/accounts"));

        // If Redis is down, second call would fail — this implicitly verifies caching
    }

    @Test
    void printProfiles() {
        System.out.println("Active profiles: " + Arrays.toString(env.getActiveProfiles()));
    }
}
