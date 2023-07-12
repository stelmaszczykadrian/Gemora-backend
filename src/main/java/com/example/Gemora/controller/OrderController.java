package com.example.Gemora.controller;

import com.example.Gemora.model.Order;
import com.example.Gemora.model.Product;
import com.example.Gemora.user.User;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {
    @PostMapping
    public ResponseEntity<Order> createOrder(@RequestBody List<Product> products, @RequestBody User user) {
        // TODO: Implement logic to create an order
        return null;
    }

    @GetMapping
    public ResponseEntity<List<Order>> getAllOrders() {
        // TODO: Implement logic to get all orders
        return null;
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<Order> getOrderById(@PathVariable Long orderId) {
        // TODO: Implement logic to get order by ID
        return null;
    }
}




