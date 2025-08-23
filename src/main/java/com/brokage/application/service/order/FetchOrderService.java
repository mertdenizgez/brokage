package com.brokage.application.service.order;

import com.brokage.domain.entity.Order;
import com.brokage.domain.enums.OrderStatus;
import com.brokage.infrastructure.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FetchOrderService {
    
    private final OrderRepository orderRepository;
    
    @Transactional(readOnly = true)
    public List<Order> getCustomerOrders(Long customerId) {
        return orderRepository.findByCustomerId(customerId);
    }
    
    @Transactional(readOnly = true)
    public List<Order> getCustomerOrdersByDateRange(Long customerId, LocalDateTime startDate, LocalDateTime endDate) {
        return orderRepository.findByCustomerIdAndDateRange(customerId, startDate, endDate);
    }
    
    @Transactional(readOnly = true)
    public List<Order> getPendingOrders() {
        return orderRepository.findByStatusOrderByCreatedDate(OrderStatus.PENDING);
    }
    
    @Transactional(readOnly = true)
    public Order getOrderById(Long orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));
    }
}
