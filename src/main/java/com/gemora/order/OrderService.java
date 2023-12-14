package com.gemora.order;

import com.gemora.payu.*;
import lombok.SneakyThrows;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;


@Service
public class OrderService {
    private final OrderRepository orderRepository;
    @Value("${payu.customerIp}")
    private String customerIp;

    @Value("${payu.merchant-pos-id}")
    private String merchantPosId;

    @Value("${payu.currency-code}")
    private String currencyCode;

    private final PayUService payUService;

    public OrderService(OrderRepository orderRepository, PayUService payUService) {
        this.orderRepository = orderRepository;
        this.payUService = payUService;

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

        String payURedirectUrl = payUService.initiatePayUPayment(createOrderPayURequest);

        if (payURedirectUrl.equals("")) {
            return new OrderCreateResponse(null, false);
        }

        return new OrderCreateResponse(payURedirectUrl, true);
    }

    public void saveOrder(Order order) {
        orderRepository.save(order);
    }

    public List<OrderDto> getAllOrders() {
        List<Order> orders = orderRepository.findAll();

        return orders.stream()
                .map(OrderDto::new)
                .collect(Collectors.toList());
    }

    public List<OrderDto> getOrdersByUserId(Integer userId) {
        List<Order> orders = orderRepository.findOrdersByUserId(userId);

        return orders.stream()
                .map(OrderDto::new)
                .collect(Collectors.toList());
    }
}
