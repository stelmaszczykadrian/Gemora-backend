package com.Gemora.unit.order;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.gemora.GemoraApplication;
import com.gemora.order.*;
import com.gemora.payu.CreateOrderPayURequest;
import com.gemora.order.OrderCreateRequest;
import com.gemora.payu.PayUService;
import com.gemora.payu.PayUTokenService;
import com.gemora.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Collections;
import java.util.List;

import static com.Gemora.unit.auth.AuthenticationTestHelper.createUser;
import static com.Gemora.unit.order.OrderTestHelper.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest(classes = GemoraApplication.class)
public class OrderServiceTest {
    private OrderService orderService;

    @Mock
    private PayUTokenService tokenService;

    @Mock
    private PayUService payUService;

    @Mock
    private OrderRepository orderRepositoryMock;

    @BeforeEach
    void init() {
        orderService = new OrderService(orderRepositoryMock, payUService);
    }

    @Test
    public void createOrder_ReturnsSuccessWithExpectedData_OrderCreated() throws JsonProcessingException {
        //given
        String redirectUrl = "http://localhost:3000/thank-you";

        OrderCreateRequest orderCreateRequest = new OrderCreateRequest("Test order", "200");

        when(tokenService.getToken()).thenReturn("mockToken");
        when(payUService.initiatePayUPayment(any(CreateOrderPayURequest.class))).thenReturn(redirectUrl);

        //when
        OrderCreateResponse response = orderService.createOrder(orderCreateRequest);

        //then
        assertTrue(response.isSuccess());
        assertEquals(redirectUrl, response.getData());
    }

    @Test
    public void createOrder_ReturnsFailure_EmptyRedirectUrl() throws JsonProcessingException {
        //given
        String emptyRedirectUrl = "";

        OrderCreateRequest orderCreateRequest = new OrderCreateRequest("Test order", "200");

        when(tokenService.getToken()).thenReturn("mockToken");
        when(payUService.initiatePayUPayment(any(CreateOrderPayURequest.class))).thenReturn(emptyRedirectUrl);

        //when
        OrderCreateResponse response = orderService.createOrder(orderCreateRequest);

        //then
        assertFalse(response.isSuccess());
        assertNull(response.getData());
    }

    @Test
    public void saveOrder_OrderRequestWasPassed_SaveWasCalledOnce() {
        //given
        Order orderToSave = getMockedOrders().get(0);
        when(orderRepositoryMock.save(orderToSave)).thenReturn(orderToSave);

        //when
        orderService.saveOrder(orderToSave);

        //then
        verify(orderRepositoryMock, times(1)).save(orderToSave);
    }

    @Test
    void getAllOrders_ReturnsExpectedListOfOrders_OrderListContainsOrders() {
        //given
        List<Order> mockedOrders = getMockedOrders();

        when(orderRepositoryMock.findAll()).thenReturn(mockedOrders);

        //when
        List<OrderDto> allOrders = orderService.getAllOrders();

        //then
        verify(orderRepositoryMock, times(1)).findAll();
        assertEquals(allOrders.size(), mockedOrders.size());
    }

    @Test
    public void getAllOrders_ReturnsEmptyList_OrdersDoesNotExist() {
        //given
        when(orderRepositoryMock.findAll()).thenReturn(Collections.emptyList());

        //when
        List<OrderDto> allOrders = orderService.getAllOrders();

        //then
        verify(orderRepositoryMock, times(1)).findAll();
        assertTrue(allOrders.isEmpty());
    }

    @Test
    public void getOrdersByUserId_ReturnsListOfOrders_ForSpecificUser() {
        //given
        int userId = 1;
        List<Order> mockedOrders = getMockedOrders();

        when(orderRepositoryMock.findOrdersByUserId(userId)).thenReturn(mockedOrders);

        //when
        List<OrderDto> userOrders = orderService.getOrdersByUserId(userId);

        //then
        verify(orderRepositoryMock, times(1)).findOrdersByUserId(userId);
        assertEquals(userOrders.size(), mockedOrders.size());
    }


    private List<Order> getMockedOrders() {
        List<OrderSimplifiedProduct> orderSimplifiedProducts = createOrderSimplifiedProductList();
        User user = createUser();

        ShippingDetails shippingDetails = createShippingDetails();

        Order order1 = createOrder(1, orderSimplifiedProducts, user, 100.0, shippingDetails);
        Order order2 = createOrder(2, orderSimplifiedProducts, user, 200.0, shippingDetails);

        return List.of(order1, order2);
    }
}
