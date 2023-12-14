package com.gemora.order;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.gemora.validation.ValidationHelper.handleBindingResultErrors;

@RestController
@RequestMapping("/api/orders")
public class OrderController {
    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public ResponseEntity<String> createOrder(@RequestBody OrderCreateRequest request) {
        OrderCreateResponse orderCreate = orderService.createOrder(request);

        if (orderCreate.isSuccess()) {
            return new ResponseEntity<>(orderCreate.getData(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/save-order")
    public ResponseEntity<String> saveOrder(@Valid @RequestBody Order order, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return handleBindingResultErrors(bindingResult);
        }

        try {
            orderService.saveOrder(order);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body("Order added successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<List<OrderDto>> getAllOrders() {
        List<OrderDto> allOrders = orderService.getAllOrders();

        return allOrders.isEmpty() ? ResponseEntity.notFound().build() : ResponseEntity.ok(allOrders);
    }

    @GetMapping("/{id}")
    public ResponseEntity<List<OrderDto>> getOrdersByUserId(
            @PathVariable Integer id) {
        List<OrderDto> userOrders = orderService.getOrdersByUserId(id);

        return !userOrders.isEmpty() ? ResponseEntity.ok(userOrders) : ResponseEntity.notFound().build();
    }
}




