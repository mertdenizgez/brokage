package com.brokage.application.service.order;

import com.brokage.domain.entity.Order;
import com.brokage.domain.enums.OrderStatus;
import com.brokage.infrastructure.repository.OrderRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FetchOrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private FetchOrderService fetchOrderService;

    @Test
    void getCustomerOrders_Success() {
        Order order1 = new Order();
        Order order2 = new Order();
        List<Order> expectedOrders = Arrays.asList(order1, order2);
        
        when(orderRepository.findByCustomerId(1L)).thenReturn(expectedOrders);

        List<Order> result = fetchOrderService.getCustomerOrders(1L);

        assertEquals(2, result.size());
        assertEquals(expectedOrders, result);
        verify(orderRepository).findByCustomerId(1L);
    }

    @Test
    void getCustomerOrdersByDateRange_Success() {
        LocalDateTime start = LocalDateTime.now().minusDays(1);
        LocalDateTime end = LocalDateTime.now();
        Order order = new Order();
        List<Order> expectedOrders = Arrays.asList(order);
        
        when(orderRepository.findByCustomerIdAndDateRange(1L, start, end)).thenReturn(expectedOrders);

        List<Order> result = fetchOrderService.getCustomerOrdersByDateRange(1L, start, end);

        assertEquals(1, result.size());
        assertEquals(expectedOrders, result);
        verify(orderRepository).findByCustomerIdAndDateRange(1L, start, end);
    }

    @Test
    void getPendingOrders_Success() {
        Order order = new Order();
        List<Order> expectedOrders = Arrays.asList(order);
        
        when(orderRepository.findByStatusOrderByCreatedDate(OrderStatus.PENDING)).thenReturn(expectedOrders);

        List<Order> result = fetchOrderService.getPendingOrders();

        assertEquals(1, result.size());
        assertEquals(expectedOrders, result);
        verify(orderRepository).findByStatusOrderByCreatedDate(OrderStatus.PENDING);
    }

    @Test
    void getOrderById_Found_Success() {
        Order order = new Order();
        order.setId(1L);
        
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        Order result = fetchOrderService.getOrderById(1L);

        assertEquals(order, result);
        verify(orderRepository).findById(1L);
    }

    @Test
    void getOrderById_NotFound_ThrowsException() {
        when(orderRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> fetchOrderService.getOrderById(1L));
    }
}
