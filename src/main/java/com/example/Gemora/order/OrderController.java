package com.example.Gemora.order;

import com.example.Gemora.order.Order;
import com.example.Gemora.product.Product;
import com.example.Gemora.user.User;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {
    @PostMapping
    public OrderDto createOrder(
            @RequestBody CreateOrderRequest request,
            @AuthenticationPrincipal User user) {
        // TODO: Implement logic to create an order
        // Sprawdzić czy user nie jest nullem, czy czyta tego usera,
        //W Controllerze może być tylko jeden argument oznaczony adnotacją RequestBody
        //Porobić DTO
        // Jak argument przyjmuje listę to można zrobić klasę np  w tym przypadku CreateOrderRequest
        //Pousuwać Response Entity, ponieważ jest to stare podejście
        //
        return null;
    }

    @GetMapping
    public List<OrderDto> getAllOrders() {
        // TODO: Implement logic to get all orders
        return null;
    }

    @GetMapping("/{orderId}")
    public OrderDto getOrderById(@PathVariable Long orderId) {
        // TODO: Implement logic to get order by ID
        return null;
    }
}




