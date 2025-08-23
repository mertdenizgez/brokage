package com.brokage.application.service.order;

import com.brokage.application.service.asset.AssetManagementService;
import com.brokage.domain.entity.Asset;
import com.brokage.domain.entity.Order;
import com.brokage.domain.enums.OrderSide;
import com.brokage.domain.enums.OrderStatus;
import com.brokage.domain.valueobject.AssetSymbol;
import com.brokage.domain.valueobject.Money;
import com.brokage.domain.valueobject.Quantity;
import com.brokage.domain.valueobject.UsableSize;
import com.brokage.infrastructure.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CancelOrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private AssetManagementService assetManagementService;

    @Mock
    private FetchOrderService fetchOrderService;

    @InjectMocks
    private CancelOrderService cancelOrderService;

    private Order order;
    private Asset tryAsset;

    @BeforeEach
    void setUp() {
        order = new Order();
        order.setId(1L);
        order.setCustomerId(1L);
        order.setAssetSymbol(AssetSymbol.of("AAPL"));
        order.setOrderSide(OrderSide.BUY);
        order.setSize(Quantity.of(new BigDecimal("10")));
        order.setPrice(Money.of(new BigDecimal("100")));
        order.setStatus(OrderStatus.PENDING);

        tryAsset = new Asset();
        tryAsset.setCustomerId(1L);
        tryAsset.setAssetSymbol(AssetSymbol.of("TRY"));
        tryAsset.setSize(Quantity.of(new BigDecimal("10000")));
        tryAsset.setUsableSize(UsableSize.of(new BigDecimal("10000")));
    }

    @Test
    void cancelOrder_ValidPendingOrder_Success() {
        when(fetchOrderService.getOrderById(1L)).thenReturn(order);
        when(assetManagementService.getAssetForUpdate(1L, "TRY")).thenReturn(tryAsset);

        cancelOrderService.cancelOrder(1L, 1L);

        assertEquals(OrderStatus.CANCELED, order.getStatus());
        verify(orderRepository).save(order);
        assertEquals(new BigDecimal("11000.00"), tryAsset.getUsableSize().getValue());
    }

    @Test
    void cancelOrder_WrongCustomer_ThrowsException() {
        when(fetchOrderService.getOrderById(1L)).thenReturn(order);

        assertThrows(IllegalArgumentException.class, () -> cancelOrderService.cancelOrder(1L, 2L));
    }

    @Test
    void cancelOrder_AlreadyMatched_ThrowsException() {
        order.setStatus(OrderStatus.MATCHED);
        when(fetchOrderService.getOrderById(1L)).thenReturn(order);

        assertThrows(IllegalStateException.class, () -> cancelOrderService.cancelOrder(1L, 1L));
    }
}
