package com.Gemora.unit.order;

import com.gemora.order.Order;
import com.gemora.order.OrderDto;
import com.gemora.order.OrderSimplifiedProduct;
import com.gemora.order.ShippingDetails;
import com.gemora.user.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.Gemora.unit.auth.AuthenticationTestHelper.createUser;

public class OrderTestHelper {
    public static Order createOrder(int id, List<OrderSimplifiedProduct> orderSimplifiedProducts, User user, double totalAmount, ShippingDetails shippingDetails) {
        return Order.builder()
                .id(id)
                .products(orderSimplifiedProducts)
                .user(user)
                .orderDateTime(LocalDateTime.now())
                .totalAmount(totalAmount)
                .shippingDetails(shippingDetails)
                .build();
    }

    public static List<OrderSimplifiedProduct> createOrderSimplifiedProductList() {
        List<OrderSimplifiedProduct> products = new ArrayList<>();

        OrderSimplifiedProduct product1 = new OrderSimplifiedProduct(1, "Product Name 1", 5, 10.0);
        OrderSimplifiedProduct product2 = new OrderSimplifiedProduct(2, "Product Name 2", 3, 15.0);

        products.add(product1);
        products.add(product2);

        return products;
    }

    public static ShippingDetails createShippingDetails() {
        return new ShippingDetails("John", "Doe", "Example 13", "New York", "00001", "johndoe@gmail.com", "Send message before arrive");
    }

    public static List<OrderDto> createExpectedOrders() {
        List<OrderSimplifiedProduct> orderSimplifiedProducts = createOrderSimplifiedProductList();
        User user = createUser();
        ShippingDetails shippingDetails = createShippingDetails();

        Order order1 = createOrder(1, orderSimplifiedProducts, user, 100.0, shippingDetails);
        Order order2 = createOrder(2, orderSimplifiedProducts, user, 200.0, shippingDetails);

        OrderDto orderDto1 = new OrderDto(order1);
        OrderDto orderDto2 = new OrderDto(order2);

        return Arrays.asList(orderDto1, orderDto2);
    }
}
