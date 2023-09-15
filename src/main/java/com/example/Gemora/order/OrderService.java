package com.example.Gemora.order;

import com.example.Gemora.payu.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;


@Service
public class OrderService {
    private final RestTemplate restTemplate = new RestTemplate();
    private final TokenService tokenService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${payu.order-url}")
    private String orderUrl;


    public OrderService(TokenService tokenService) {
        this.tokenService = tokenService;

    }

    @SneakyThrows
    public OrderCreateResponse createOrder(OrderCreateRequest orderCreateRequest) {
        orderCreateRequest.setContinueUrl("http://localhost:3000/thank-you");

        String token = tokenService.getToken();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + token);

        HttpEntity<OrderCreateRequest> requestEntity = new HttpEntity<>(orderCreateRequest, headers);

        ResponseEntity<String> responseEntity = restTemplate.postForEntity(
                orderUrl,
                requestEntity,
                String.class
        );

        if (responseEntity.getStatusCode().is3xxRedirection()) {
            String jsonResponse = responseEntity.getBody();
            PayUOrderCreateResponse response = objectMapper.readValue(jsonResponse, PayUOrderCreateResponse.class);

            return new OrderCreateResponse(response.getRedirectUri(), true);
        }

        return new OrderCreateResponse(null, false);

    }


}
