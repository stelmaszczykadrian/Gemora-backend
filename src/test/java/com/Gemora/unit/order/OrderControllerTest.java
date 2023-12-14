package com.Gemora.unit.order;

import com.gemora.GemoraApplication;
import com.gemora.order.*;
import com.gemora.order.OrderCreateRequest;
import com.gemora.user.User;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;

import java.util.*;

import static com.Gemora.unit.TestUtils.getBindingResult;
import static com.Gemora.unit.auth.AuthenticationTestHelper.createUser;
import static com.Gemora.unit.order.OrderTestHelper.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.*;

@SpringBootTest(classes = GemoraApplication.class)
public class OrderControllerTest {
    private OrderController orderController;
    @Mock
    private OrderService orderService;

    @BeforeEach
    void init() {
        orderController = new OrderController(orderService);
    }

    @Test
    public void createOrder_ReturnsOkStatus_ValidOrder() {
        //given
        OrderCreateRequest validRequest = new OrderCreateRequest("Valid description", "100.0");
        OrderCreateResponse successResponse = new OrderCreateResponse(null, true);

        when(orderService.createOrder(eq(validRequest))).thenReturn(successResponse);

        //when
        ResponseEntity<String> response = orderController.createOrder(validRequest);

        //then
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void createOrder_ReturnsInternalServerErrorStatus_InvalidOrder() {
        //given
        OrderCreateRequest invalidRequest = new OrderCreateRequest(null, "Invalid totalAmount");
        OrderCreateResponse errorResponse = new OrderCreateResponse(null, false);

        when(orderService.createOrder(eq(invalidRequest))).thenReturn(errorResponse);

        //when
        ResponseEntity<String> response = orderController.createOrder(invalidRequest);

        //then
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void saveOrder_ReturnsCreatedStatusAndSaveOrderWasCalledOnce_ValidOrder() {
        //given
        List<OrderSimplifiedProduct> orderSimplifiedProducts = createOrderSimplifiedProductList();
        User user = createUser();
        ShippingDetails shippingDetails = createShippingDetails();

        Order order = createOrder(1, orderSimplifiedProducts, user, 100.0, shippingDetails);

        BindingResult bindingResult = getBindingResult(false);

        //when
        ResponseEntity<String> response = orderController.saveOrder(order, bindingResult);

        //then
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("Order added successfully.", response.getBody());
        verify(orderService, times(1)).saveOrder(order);
    }

    @Test
    void saveOrder_ReturnsBadRequestStatus_WhenBindingErrors() {
        //given
        Order order = new Order();

        BindingResult bindingResult = getBindingResult(true);

        //when
        ResponseEntity<String> response = orderController.saveOrder(order, bindingResult);

        //then
        verifyNoInteractions(orderService);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void saveOrder_ReturnsBadRequestStatus_ThrownRuntimeException() {
        //given
        Order order = new Order();

        BindingResult bindingResult = getBindingResult(false);

        doThrow(RuntimeException.class).when(orderService).saveOrder(any());

        //when
        ResponseEntity<String> response = orderController.saveOrder(order, bindingResult);

        //then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void getAllOrders_ReturnsExpectedOrdersList_OrdersAreAvailable() {
        //given
        List<OrderDto> expectedOrders = createExpectedOrders();

        when(orderService.getAllOrders()).thenReturn(expectedOrders);

        //when
        ResponseEntity<List<OrderDto>> response = orderController.getAllOrders();

        //then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isInstanceOf(List.class);

        List<OrderDto> allOrders = response.getBody();
        assertThat(allOrders).hasSize(2);
        assertThat(allOrders).containsExactlyElementsOf(expectedOrders);
    }

    @Test
    void getAllOrders_ReturnsNotFoundStatus_OrdersDoesNotExist() {
        //given
        when(orderService.getAllOrders()).thenReturn(Collections.emptyList());

        //when
        ResponseEntity<List<OrderDto>> response = orderController.getAllOrders();

        //then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNull();
    }

    @Test
    void getOrdersByUserId_ReturnsExpectedOrdersList_UserHasOrders() {
        //given
        int userId = 1;
        List<OrderDto> expectedOrders = createExpectedOrders();

        when(orderService.getOrdersByUserId(userId)).thenReturn(expectedOrders);

        //when
        ResponseEntity<List<OrderDto>> response = orderController.getOrdersByUserId(userId);

        //then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(expectedOrders);
    }

    @Test
    void getProductById_ReturnsNotFound_UserDoesNotExist() {
        //given
        int userId = 999;

        when(orderService.getOrdersByUserId(userId)).thenReturn(Collections.emptyList());

        //when
        ResponseEntity<List<OrderDto>> response = orderController.getOrdersByUserId(userId);

        //then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNull();
    }
}
