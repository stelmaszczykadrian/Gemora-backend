package com.gemora.payu;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class PayUTokenService {
    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${payu.client-id}")
    private String clientId;

    @Value("${payu.client-secret}")
    private String clientSecret;

    @Value("${payu.authorization-uri}")
    private String authorizationUrl;

    @SneakyThrows
    public String getToken() {
        String url = authorizationUrl + "?grant_type=client_credentials&client_id=" + clientId + "&client_secret=" + clientSecret;
        final ResponseEntity<String> jsonResponse = restTemplate.postForEntity(url, null, String.class);
        PayUAuthToken payUAuthToken = new ObjectMapper().readValue(jsonResponse.getBody(), PayUAuthToken.class);
        return payUAuthToken.getAccessToken();
    }
}
