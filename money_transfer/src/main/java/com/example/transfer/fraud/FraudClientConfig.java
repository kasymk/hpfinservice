package com.example.transfer.fraud;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

import java.net.http.HttpClient;
import java.time.Duration;

@Configuration
public class FraudClientConfig {

    @Bean
    RestClient fraudRestClient(
            RestClient.Builder builder,
            @Value("${fraud.base-url}") String baseUrl,
            @Value("${fraud.timeout-ms}") long timeoutMs
    ) {

        HttpClient httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofMillis(timeoutMs))
                .build();

        ClientHttpRequestFactory requestFactory =
                new JdkClientHttpRequestFactory(httpClient);

        return builder
                .baseUrl(baseUrl)
                .requestFactory(requestFactory)
                .build();
    }
}

