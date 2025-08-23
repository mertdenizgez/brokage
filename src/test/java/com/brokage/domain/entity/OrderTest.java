package com.brokage.domain.entity;

import com.brokage.domain.enums.OrderSide;
import com.brokage.domain.enums.OrderStatus;
import com.brokage.domain.valueobject.AssetSymbol;
import com.brokage.domain.valueobject.Money;
import com.brokage.domain.valueobject.Quantity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class OrderTest {

    private Order order;

    @BeforeEach
    void setUp() {
        order = new Order();
        order.setCustomerId(1L);
        order.setAssetSymbol(AssetSymbol.of("AAPL"));
        order.setOrderSide(OrderSide.BUY);
        order.setSize(Quantity.of(new BigDecimal("10")));
        order.setPrice(Money.of(new BigDecimal("150")));
        order.setStatus(OrderStatus.PENDING);
    }

    @Test
    void getTotalAmount_Success() {
        Money totalAmount = order.getTotalAmount();
        assertEquals(new BigDecimal("1500.00"), totalAmount.getAmount());
    }

    @Test
    void canBeCanceled_PendingOrder_ReturnsTrue() {
        assertTrue(order.canBeCanceled());
    }

    @Test
    void canBeCanceled_MatchedOrder_ReturnsFalse() {
        order.setStatus(OrderStatus.MATCHED);
        assertFalse(order.canBeCanceled());
    }

    @Test
    void canBeCanceled_CanceledOrder_ReturnsFalse() {
        order.setStatus(OrderStatus.CANCELED);
        assertFalse(order.canBeCanceled());
    }

    @Test
    void cancel_PendingOrder_Success() {
        order.cancel();
        assertEquals(OrderStatus.CANCELED, order.getStatus());
    }

    @Test
    void cancel_MatchedOrder_ThrowsException() {
        order.setStatus(OrderStatus.MATCHED);
        assertThrows(IllegalStateException.class, () -> order.cancel());
    }

    @Test
    void cancel_AlreadyCanceledOrder_ThrowsException() {
        order.setStatus(OrderStatus.CANCELED);
        assertThrows(IllegalStateException.class, () -> order.cancel());
    }

    @Test
    void match_PendingOrder_Success() {
        order.match();
        assertEquals(OrderStatus.MATCHED, order.getStatus());
    }

    @Test
    void match_CanceledOrder_ThrowsException() {
        order.setStatus(OrderStatus.CANCELED);
        assertThrows(IllegalStateException.class, () -> order.match());
    }

    @Test
    void match_AlreadyMatchedOrder_ThrowsException() {
        order.setStatus(OrderStatus.MATCHED);
        assertThrows(IllegalStateException.class, () -> order.match());
    }
}
