package com.brokage.infrastructure.repository;

import com.brokage.domain.entity.Order;
import com.brokage.domain.enums.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    
    List<Order> findByCustomerId(Long customerId);
    
    @Query("SELECT o FROM Order o WHERE o.customerId = :customerId " +
           "AND o.createdDate >= :startDate AND o.createdDate <= :endDate " +
           "ORDER BY o.createdDate DESC")
    List<Order> findByCustomerIdAndDateRange(@Param("customerId") Long customerId,
                                           @Param("startDate") LocalDateTime startDate,
                                           @Param("endDate") LocalDateTime endDate);

    @Query("SELECT o FROM Order o WHERE o.status = :status ORDER BY o.createdDate ASC")
    List<Order> findByStatusOrderByCreatedDate(@Param("status") OrderStatus status);
}
