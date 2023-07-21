package com.example.Gemora.order;

import com.example.Gemora.product.Product;
import com.example.Gemora.user.User;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class OrderService {
    private final OrderRepository orderRepository;

    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public Order createOrder(List<Product> products, User user) {
        LocalDateTime orderDateTime = LocalDateTime.now();
        double totalAmount = calculateTotalAmount(products);
        Order order = Order.builder()
                .products(products)
                .user(user)
                .orderDateTime(orderDateTime)
                .totalAmount(totalAmount)
                .build();
        return orderRepository.save(order);
    }

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    public Order getOrderById(int orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalStateException("Order not found with id: " + orderId));
    }

    private double calculateTotalAmount(List<Product> products) {
        double totalAmount = 0;
        for (Product product : products) {
            totalAmount += product.getPrice();
        }
        return totalAmount;
    }
}
