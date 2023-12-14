package com.Gemora.e2e.order;

import com.gemora.order.*;
import com.gemora.order.OrderCreateRequest;
import com.gemora.user.User;
import com.gemora.user.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static com.Gemora.unit.auth.AuthenticationTestHelper.createUser;
import static com.Gemora.unit.order.OrderTestHelper.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class OrderE2ETest {
    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserRepository userRepository;

    @AfterEach
    void setUp() {
        orderRepository.deleteAll();
    }

    @Test
    void createOrder_ReturnsOkStatus_ValidOrderRequest() {
        //given
        OrderCreateRequest request = new OrderCreateRequest("Gemora", "200");

        String baseUrl = "http://localhost:" + port + "/api/orders";

        //when
        ResponseEntity<String> response = restTemplate.postForEntity(baseUrl, request, String.class);

        //then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull().isNotEmpty();
    }

    @Test
    void saveOrder_ReturnsCreatedStatus_ValidOrder() {
        //given
        User user = createUser();
        user = userRepository.save(user);

        List<OrderSimplifiedProduct> orderSimplifiedProductList = createOrderSimplifiedProductList();
        ShippingDetails shippingDetails = createShippingDetails();

        Order order = new Order(1, orderSimplifiedProductList, user, LocalDateTime.now(), 200, shippingDetails);
        String baseUrl = "http://localhost:" + port + "/api/orders/save-order";

        //when
        ResponseEntity<String> response = restTemplate.postForEntity(baseUrl, order, String.class);

        //then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isEqualTo("Order added successfully.");
    }

    @Test
    void getAllOrders_ReturnsOkStatusAndExpectedResponse_OrdersAreValid() {
        //given
        initializeOrdersData();

        String baseUrl = "http://localhost:" + port + "/api/orders";

        //when
        ResponseEntity<List<OrderDto>> response = restTemplate.exchange(
                baseUrl,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                }
        );

        //then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().isEmpty());

        List<OrderDto> orders = response.getBody();

        assertThat(orders).hasSize(3);
        assertThat(orders)
                .extracting(OrderDto::getTotalAmount)
                .containsExactly(200.0, 200.0, 200.0);
    }

    @Test
    void getOrdersByUserId_ReturnsOkStatusAndExpectedOrders_UserExists() {
        //given
        User user = initializeOrdersData();

        int userId = user.getId();

        String baseUrl = "http://localhost:" + port + "/api/orders/" + userId;

        //when
        ResponseEntity<List<OrderDto>> response = restTemplate.exchange(
                baseUrl,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                }
        );

        //then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(3);
    }

    @Test
    void getOrdersByUserId_ReturnsNotFoundStatus_NonExistingUser() {
        //given
        int nonExistingUserId = 999;

        String baseUrl = "http://localhost:" + port + "/api/orders/" + nonExistingUserId;

        //when
        ResponseEntity<List<OrderDto>> response = restTemplate.exchange(
                baseUrl,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                });

        //then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNull();
    }

    private User initializeOrdersData() {
        User user = createUser();
        user = userRepository.save(user);

        List<OrderSimplifiedProduct> orderSimplifiedProductList = createOrderSimplifiedProductList();
        ShippingDetails shippingDetails = createShippingDetails();
        Order order1 = new Order(1, orderSimplifiedProductList, user, LocalDateTime.now(), 200, shippingDetails);
        Order order2 = new Order(2, orderSimplifiedProductList, user, LocalDateTime.now(), 200, shippingDetails);
        Order order3 = new Order(3, orderSimplifiedProductList, user, LocalDateTime.now(), 200, shippingDetails);


        orderRepository.save(order1);
        orderRepository.save(order2);
        orderRepository.save(order3);
        return user;
    }
}
