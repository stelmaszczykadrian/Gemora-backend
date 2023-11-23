package com.gemora.order;

import com.gemora.user.UserDto;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class OrderDto {
    private Integer id;
    private List<OrderSimplifiedProduct> products;
    private UserDto user;
    private LocalDateTime orderDateTime;
    private double totalAmount;
    private ShippingDetails shippingDetails;

    public OrderDto(Order order) {
        this.id = order.getId();
        this.products = order.getProducts();
        this.user = new UserDto(order.getUser());
        this.orderDateTime = order.getOrderDateTime();
        this.totalAmount = order.getTotalAmount();
        this.shippingDetails = order.getShippingDetails();
    }
}
