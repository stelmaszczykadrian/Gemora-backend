package com.Gemora.integration.order;

import com.Gemora.unit.TestUtils;
import com.gemora.GemoraApplication;
import com.gemora.order.*;
import com.gemora.order.OrderCreateRequest;
import com.gemora.user.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.*;

import static com.Gemora.unit.TestUtils.asJsonString;
import static com.Gemora.unit.auth.AuthenticationTestHelper.createUser;
import static com.Gemora.unit.order.OrderTestHelper.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(classes = {GemoraApplication.class})
@AutoConfigureMockMvc
public class OrderControllerIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OrderService orderService;

    @Autowired
    private TestUtils testUtils;

    @Test
    void createOrder_ReturnsOkStatus_ValidOrderRequest() throws Exception {
        //given
        OrderCreateRequest request = new OrderCreateRequest("Gemora", "200");

        when(orderService.createOrder(any(OrderCreateRequest.class)))
                .thenReturn(new OrderCreateResponse("http://example.com", true));

        //when
        ResultActions result = mockMvc.perform(post("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(request)));

        //then
        result.andExpect(status().isOk())
                .andExpect(content().string("http://example.com"));
    }

    @Test
    void saveOrder_ReturnsCreatedStatus_OrderIsValid() throws Exception {
        //given
        List<OrderSimplifiedProduct> orderSimplifiedProducts = createOrderSimplifiedProductList();
        User user = createUser();
        ShippingDetails shippingDetails = createShippingDetails();

        Order order = createOrder(1, orderSimplifiedProducts, user, 100.0, shippingDetails);

        doNothing().when(orderService).saveOrder(any(Order.class));

        //when
        ResultActions result = mockMvc.perform(post("/api/orders/save-order")
                .content(testUtils.asJsonString2(order))
                .contentType(MediaType.APPLICATION_JSON));

        //then
        result.andExpect(status().isCreated())
                .andExpect(content().string("Order added successfully."));
    }

    @Test
    void getAllOrders_ReturnsOkStatusAndExpectedData_ValidOrdersList() throws Exception {
        //given
        List<OrderDto> expectedOrders = createExpectedOrders();

        when(orderService.getAllOrders()).thenReturn(expectedOrders);

        //when
        ResultActions result = mockMvc.perform(get("/api/orders"));

        //then
        result.andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].totalAmount").value(100.0))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].totalAmount").value(200.0));
    }

    @Test
    void getAllOrders_ReturnsNotFoundStatus_OrdersListIsEmpty() throws Exception {
        //given
        when(orderService.getAllOrders()).thenReturn(Collections.emptyList());

        //when
        ResultActions result = mockMvc.perform(get("/api/orders"));

        //then
        result.andExpect(status().isNotFound())
                .andExpect(content().string(""));
    }

    @Test
    void getOrdersByUserId_ReturnsOkStatusAndExpectedData_ValidOrdersList() throws Exception {
        //given
        int userId = 1;
        List<OrderDto> expectedOrders = createExpectedOrders();

        when(orderService.getOrdersByUserId(eq(userId))).thenReturn(expectedOrders);

        //when
        ResultActions result = mockMvc.perform(get("/api/orders/{id}", userId));

        //then
        result.andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].user.id").value(userId))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].user.id").value(userId));
    }

    @Test
    void getOrdersByUserId_ReturnsNotFoundStatus_UserDoesNotExist() throws Exception {
        //given
        int userId = 999;

        when(orderService.getOrdersByUserId(eq(userId))).thenReturn(Collections.emptyList());

        //when
        ResultActions result = mockMvc.perform(get("/api/orders/{id}", userId));

        //then
        result.andExpect(status().isNotFound())
                .andExpect(content().string(""));
    }
}
