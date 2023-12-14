package com.gemora.payu;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class PayUService {
    @Value("${payu.order-url}")
    private String orderUrl;

    @Autowired
    private PayUTokenService tokenService;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public String initiatePayUPayment(CreateOrderPayURequest createOrderPayURequest) throws JsonProcessingException {
        String token = tokenService.getToken();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + token);

        HttpEntity<CreateOrderPayURequest> requestEntity = new HttpEntity<>(createOrderPayURequest, headers);

        ResponseEntity<String> responseEntity = restTemplate.postForEntity(
                orderUrl,
                requestEntity,
                String.class
        );

        if (responseEntity.getStatusCode().is3xxRedirection()) {
            String jsonResponse = responseEntity.getBody();
            PayUOrderCreateResponse response = objectMapper.readValue(jsonResponse, PayUOrderCreateResponse.class);

            return response.getRedirectUri();
        }
        return "";
    }
}
