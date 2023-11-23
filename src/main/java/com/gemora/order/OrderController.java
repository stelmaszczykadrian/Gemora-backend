package com.gemora.order;

import com.gemora.payu.OrderCreateRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;


    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public ResponseEntity<String> createOrder(
            @RequestBody OrderCreateRequest request) {

        OrderCreateResponse orderCreate = orderService.createOrder(request);


        return orderCreate.isSuccess()?new ResponseEntity<>(orderCreate.getData(),HttpStatus.OK) : new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR) ;
    }

    @PostMapping("/save-order")
    public void saveOrder(@RequestBody Order order) {
        orderService.saveOrder(order);
    }


}




