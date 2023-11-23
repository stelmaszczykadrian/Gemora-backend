package com.gemora.order;

import com.gemora.payu.CreateOrderPayURequest;
import com.gemora.payu.OrderCreateRequest;
import com.gemora.payu.PayUOrderCreateResponse;
import com.gemora.payu.TokenService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;


@Service
public class OrderService {
    private final OrderRepository orderRepository;
    private final RestTemplate restTemplate = new RestTemplate();
    private final TokenService tokenService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${payu.order-url}")
    private String orderUrl;

    @Value("${payu.customerIp}")
    private String customerIp;

    @Value("${payu.merchant-pos-id}")
    private String merchantPosId;

    @Value("${payu.currency-code}")
    private String currencyCode;


    public OrderService(OrderRepository orderRepository, TokenService tokenService) {
        this.orderRepository = orderRepository;
        this.tokenService = tokenService;

    }

    @SneakyThrows
    public OrderCreateResponse createOrder(OrderCreateRequest orderCreateRequest) {
        String continueUrl = "http://localhost:3000/thank-you";

        CreateOrderPayURequest createOrderPayURequest = new CreateOrderPayURequest(
                continueUrl,
                customerIp,
                merchantPosId,
                orderCreateRequest.getDescription(),
                currencyCode,
                orderCreateRequest.getTotalAmount());

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

            return new OrderCreateResponse(response.getRedirectUri(), true);
        }

        return new OrderCreateResponse(null, false);

    }


}
