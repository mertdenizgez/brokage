package com.brokage.presentation.controller;

import com.brokage.api.controller.OrderController;
import com.brokage.application.dto.request.CreateOrderRequest;
import com.brokage.application.dto.response.OrderResponse;
import com.brokage.application.mapper.OrderMapper;
import com.brokage.application.service.order.CancelOrderService;
import com.brokage.application.service.order.CreateOrderService;
import com.brokage.application.service.order.FetchOrderService;
import com.brokage.domain.entity.Order;
import com.brokage.domain.enums.OrderSide;
import com.brokage.domain.enums.OrderStatus;
import com.brokage.domain.valueobject.AssetSymbol;
import com.brokage.domain.valueobject.Money;
import com.brokage.domain.valueobject.Quantity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderControllerTest {

    @Mock
    private CreateOrderService createOrderService;

    @Mock
    private FetchOrderService fetchOrderService;

    @Mock
    private CancelOrderService cancelOrderService;

    @Mock
    private OrderMapper orderMapper;

    @InjectMocks
    private OrderController orderController;

    private CreateOrderRequest createOrderRequest;
    private Order mockOrder;
    private OrderResponse mockOrderResponse;
    private List<Order> mockOrders;
    private List<OrderResponse> mockOrderResponses;

    @BeforeEach
    void setUp() {
        createOrderRequest = new CreateOrderRequest();
        createOrderRequest.setCustomerId(1L);
        createOrderRequest.setAssetName("AAPL");
        createOrderRequest.setOrderSide(OrderSide.BUY);
        createOrderRequest.setSize(new BigDecimal("10"));
        createOrderRequest.setPrice(new BigDecimal("150.00"));

        mockOrder = new Order();
        mockOrder.setId(1L);
        mockOrder.setCustomerId(1L);
        mockOrder.setAssetSymbol(AssetSymbol.of("AAPL"));
        mockOrder.setOrderSide(OrderSide.BUY);
        mockOrder.setSize(Quantity.of(new BigDecimal("10")));
        mockOrder.setPrice(Money.of(new BigDecimal("150.00")));
        mockOrder.setStatus(OrderStatus.PENDING);
        mockOrder.setCreatedDate(LocalDateTime.now());

        mockOrderResponse = new OrderResponse();
        mockOrderResponse.setId(1L);
        mockOrderResponse.setCustomerId(1L);
        mockOrderResponse.setAssetName("AAPL");
        mockOrderResponse.setOrderSide(OrderSide.BUY);
        mockOrderResponse.setSize(new BigDecimal("10"));
        mockOrderResponse.setPrice(new BigDecimal("150.00"));
        mockOrderResponse.setStatus(OrderStatus.PENDING);
        mockOrderResponse.setCreatedDate(LocalDateTime.now());

        Order mockOrder2 = new Order();
        mockOrder2.setId(2L);
        mockOrder2.setCustomerId(1L);
        mockOrder2.setAssetSymbol(AssetSymbol.of("GOOGL"));
        mockOrder2.setOrderSide(OrderSide.SELL);
        mockOrder2.setSize(Quantity.of(new BigDecimal("5")));
        mockOrder2.setPrice(Money.of(new BigDecimal("2800.00")));
        mockOrder2.setStatus(OrderStatus.MATCHED);
        mockOrder2.setCreatedDate(LocalDateTime.now().minusDays(1));

        OrderResponse mockOrderResponse2 = new OrderResponse();
        mockOrderResponse2.setId(2L);
        mockOrderResponse2.setCustomerId(1L);
        mockOrderResponse2.setAssetName("GOOGL");
        mockOrderResponse2.setOrderSide(OrderSide.SELL);
        mockOrderResponse2.setSize(new BigDecimal("5"));
        mockOrderResponse2.setPrice(new BigDecimal("2800.00"));
        mockOrderResponse2.setStatus(OrderStatus.MATCHED);
        mockOrderResponse2.setCreatedDate(LocalDateTime.now().minusDays(1));

        mockOrders = Arrays.asList(mockOrder, mockOrder2);
        mockOrderResponses = Arrays.asList(mockOrderResponse, mockOrderResponse2);
    }

    @Test
    void createOrder_WithValidRequest_ShouldReturnCreatedOrder() {
        // Given
        when(createOrderService.createOrder(createOrderRequest)).thenReturn(mockOrder);
        when(orderMapper.toResponse(mockOrder)).thenReturn(mockOrderResponse);

        // When
        ResponseEntity<OrderResponse> response = orderController.createOrder(createOrderRequest);

        // Then
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(mockOrderResponse.getId(), response.getBody().getId());
        assertEquals(mockOrderResponse.getCustomerId(), response.getBody().getCustomerId());
        assertEquals(mockOrderResponse.getAssetName(), response.getBody().getAssetName());
        assertEquals(mockOrderResponse.getOrderSide(), response.getBody().getOrderSide());
        assertEquals(mockOrderResponse.getSize(), response.getBody().getSize());
        assertEquals(mockOrderResponse.getPrice(), response.getBody().getPrice());
        assertEquals(OrderStatus.PENDING, response.getBody().getStatus());

        verify(createOrderService).createOrder(createOrderRequest);
        verify(orderMapper).toResponse(mockOrder);
    }

    @Test
    void createOrder_WhenServiceThrowsException_ShouldPropagateException() {
        // Given
        when(createOrderService.createOrder(createOrderRequest))
                .thenThrow(new IllegalStateException("Insufficient balance"));

        // When & Then
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            orderController.createOrder(createOrderRequest);
        });

        assertEquals("Insufficient balance", exception.getMessage());
        verify(createOrderService).createOrder(createOrderRequest);
        verify(orderMapper, never()).toResponse(any());
    }

    @Test
    void createOrder_WhenMapperThrowsException_ShouldPropagateException() {
        // Given
        when(createOrderService.createOrder(createOrderRequest)).thenReturn(mockOrder);
        when(orderMapper.toResponse(mockOrder)).thenThrow(new RuntimeException("Mapping failed"));

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            orderController.createOrder(createOrderRequest);
        });

        assertEquals("Mapping failed", exception.getMessage());
        verify(createOrderService).createOrder(createOrderRequest);
        verify(orderMapper).toResponse(mockOrder);
    }

    @Test
    void getOrders_WithoutDateRange_ShouldReturnAllCustomerOrders() {
        // Given
        Long customerId = 1L;
        when(fetchOrderService.getCustomerOrders(customerId)).thenReturn(mockOrders);
        when(orderMapper.toResponseList(mockOrders)).thenReturn(mockOrderResponses);

        // When
        ResponseEntity<List<OrderResponse>> response = orderController.getOrders(customerId, null, null);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().size());
        assertEquals(mockOrderResponses, response.getBody());

        verify(fetchOrderService).getCustomerOrders(customerId);
        verify(fetchOrderService, never()).getCustomerOrdersByDateRange(any(), any(), any());
        verify(orderMapper).toResponseList(mockOrders);
    }

    @Test
    void getOrders_WithDateRange_ShouldReturnFilteredOrders() {
        // Given
        Long customerId = 1L;
        LocalDate startDate = LocalDate.now().minusDays(7);
        LocalDate endDate = LocalDate.now();
        LocalDateTime expectedStart = startDate.atStartOfDay();
        LocalDateTime expectedEnd = endDate.atTime(LocalTime.MAX);

        when(fetchOrderService.getCustomerOrdersByDateRange(customerId, expectedStart, expectedEnd))
                .thenReturn(mockOrders);
        when(orderMapper.toResponseList(mockOrders)).thenReturn(mockOrderResponses);

        // When
        ResponseEntity<List<OrderResponse>> response = orderController.getOrders(customerId, startDate, endDate);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().size());
        assertEquals(mockOrderResponses, response.getBody());

        verify(fetchOrderService).getCustomerOrdersByDateRange(customerId, expectedStart, expectedEnd);
        verify(fetchOrderService, never()).getCustomerOrders(any());
        verify(orderMapper).toResponseList(mockOrders);
    }

    @Test
    void getOrders_WithOnlyStartDate_ShouldUseAllOrdersMethod() {
        // Given
        Long customerId = 1L;
        LocalDate startDate = LocalDate.now().minusDays(7);
        when(fetchOrderService.getCustomerOrders(customerId)).thenReturn(mockOrders);
        when(orderMapper.toResponseList(mockOrders)).thenReturn(mockOrderResponses);

        // When
        ResponseEntity<List<OrderResponse>> response = orderController.getOrders(customerId, startDate, null);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().size());

        verify(fetchOrderService).getCustomerOrders(customerId);
        verify(fetchOrderService, never()).getCustomerOrdersByDateRange(any(), any(), any());
        verify(orderMapper).toResponseList(mockOrders);
    }

    @Test
    void getOrders_WithOnlyEndDate_ShouldUseAllOrdersMethod() {
        // Given
        Long customerId = 1L;
        LocalDate endDate = LocalDate.now();
        when(fetchOrderService.getCustomerOrders(customerId)).thenReturn(mockOrders);
        when(orderMapper.toResponseList(mockOrders)).thenReturn(mockOrderResponses);

        // When
        ResponseEntity<List<OrderResponse>> response = orderController.getOrders(customerId, null, endDate);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().size());

        verify(fetchOrderService).getCustomerOrders(customerId);
        verify(fetchOrderService, never()).getCustomerOrdersByDateRange(any(), any(), any());
        verify(orderMapper).toResponseList(mockOrders);
    }

    @Test
    void getOrders_WhenFetchServiceThrowsException_ShouldPropagateException() {
        // Given
        Long customerId = 1L;
        when(fetchOrderService.getCustomerOrders(customerId))
                .thenThrow(new RuntimeException("Database error"));

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            orderController.getOrders(customerId, null, null);
        });

        assertEquals("Database error", exception.getMessage());
        verify(fetchOrderService).getCustomerOrders(customerId);
        verify(orderMapper, never()).toResponseList(any());
    }

    @Test
    void getOrders_WhenMapperThrowsException_ShouldPropagateException() {
        // Given
        Long customerId = 1L;
        when(fetchOrderService.getCustomerOrders(customerId)).thenReturn(mockOrders);
        when(orderMapper.toResponseList(mockOrders)).thenThrow(new RuntimeException("Mapping failed"));

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            orderController.getOrders(customerId, null, null);
        });

        assertEquals("Mapping failed", exception.getMessage());
        verify(fetchOrderService).getCustomerOrders(customerId);
        verify(orderMapper).toResponseList(mockOrders);
    }

    @Test
    void cancelOrder_WithValidParameters_ShouldReturnNoContent() {
        // Given
        Long orderId = 1L;
        Long customerId = 1L;
        doNothing().when(cancelOrderService).cancelOrder(orderId, customerId);

        // When
        ResponseEntity<Void> response = orderController.cancelOrder(orderId, customerId);

        // Then
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());

        verify(cancelOrderService).cancelOrder(orderId, customerId);
    }

    @Test
    void cancelOrder_WhenServiceThrowsException_ShouldPropagateException() {
        // Given
        Long orderId = 1L;
        Long customerId = 1L;
        doThrow(new IllegalStateException("Order cannot be canceled"))
                .when(cancelOrderService).cancelOrder(orderId, customerId);

        // When & Then
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            orderController.cancelOrder(orderId, customerId);
        });

        assertEquals("Order cannot be canceled", exception.getMessage());
        verify(cancelOrderService).cancelOrder(orderId, customerId);
    }

    @Test
    void cancelOrder_WhenOrderNotFound_ShouldPropagateException() {
        // Given
        Long orderId = 999L;
        Long customerId = 1L;
        doThrow(new RuntimeException("Order not found"))
                .when(cancelOrderService).cancelOrder(orderId, customerId);

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            orderController.cancelOrder(orderId, customerId);
        });

        assertEquals("Order not found", exception.getMessage());
        verify(cancelOrderService).cancelOrder(orderId, customerId);
    }

    @Test
    void getOrders_WithEmptyResult_ShouldReturnEmptyList() {
        // Given
        Long customerId = 1L;
        when(fetchOrderService.getCustomerOrders(customerId)).thenReturn(List.of());
        when(orderMapper.toResponseList(List.of())).thenReturn(List.of());

        // When
        ResponseEntity<List<OrderResponse>> response = orderController.getOrders(customerId, null, null);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isEmpty());

        verify(fetchOrderService).getCustomerOrders(customerId);
        verify(orderMapper).toResponseList(List.of());
    }

    @Test
    void getOrders_DateRangeConversion_ShouldUseCorrectTimeValues() {
        // Given
        Long customerId = 1L;
        LocalDate startDate = LocalDate.of(2023, 12, 1);
        LocalDate endDate = LocalDate.of(2023, 12, 31);

        LocalDateTime expectedStart = LocalDateTime.of(2023, 12, 1, 0, 0, 0);
        LocalDateTime expectedEnd = LocalDateTime.of(2023, 12, 31, 23, 59, 59, 999999999);

        when(fetchOrderService.getCustomerOrdersByDateRange(eq(customerId), eq(expectedStart), any(LocalDateTime.class)))
                .thenReturn(mockOrders);
        when(orderMapper.toResponseList(mockOrders)).thenReturn(mockOrderResponses);

        // When
        ResponseEntity<List<OrderResponse>> response = orderController.getOrders(customerId, startDate, endDate);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(fetchOrderService).getCustomerOrdersByDateRange(eq(customerId), eq(expectedStart), any(LocalDateTime.class));
    }
}
