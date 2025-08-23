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
class MatchOrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private AssetManagementService assetManagementService;

    @Mock
    private FetchOrderService fetchOrderService;

    @InjectMocks
    private MatchOrderService matchOrderService;

    private Order buyOrder;
    private Order sellOrder;
    private Asset tryAsset;
    private Asset stockAsset;

    @BeforeEach
    void setUp() {
        buyOrder = new Order();
        buyOrder.setId(1L);
        buyOrder.setCustomerId(1L);
        buyOrder.setAssetSymbol(AssetSymbol.of("AAPL"));
        buyOrder.setOrderSide(OrderSide.BUY);
        buyOrder.setSize(Quantity.of(new BigDecimal("10")));
        buyOrder.setPrice(Money.of(new BigDecimal("100")));
        buyOrder.setStatus(OrderStatus.PENDING);

        sellOrder = new Order();
        sellOrder.setId(2L);
        sellOrder.setCustomerId(1L);
        sellOrder.setAssetSymbol(AssetSymbol.of("AAPL"));
        sellOrder.setOrderSide(OrderSide.SELL);
        sellOrder.setSize(Quantity.of(new BigDecimal("5")));
        sellOrder.setPrice(Money.of(new BigDecimal("110")));
        sellOrder.setStatus(OrderStatus.PENDING);

        tryAsset = new Asset();
        tryAsset.setCustomerId(1L);
        tryAsset.setAssetSymbol(AssetSymbol.of("TRY"));
        tryAsset.setSize(Quantity.of(new BigDecimal("10000")));
        tryAsset.setUsableSize(UsableSize.of(new BigDecimal("10000")));

        stockAsset = new Asset();
        stockAsset.setCustomerId(1L);
        stockAsset.setAssetSymbol(AssetSymbol.of("AAPL"));
        stockAsset.setSize(Quantity.of(new BigDecimal("20")));
        stockAsset.setUsableSize(UsableSize.of(new BigDecimal("20")));
    }

    @Test
    void matchOrder_BuyOrder_Success() {
        when(fetchOrderService.getOrderById(1L)).thenReturn(buyOrder);
        when(assetManagementService.getOrCreateAsset(1L, "AAPL")).thenReturn(stockAsset);

        matchOrderService.matchOrder(1L);

        assertEquals(OrderStatus.MATCHED, buyOrder.getStatus());
        verify(orderRepository).save(buyOrder);
        assertEquals(new BigDecimal("30.00"), stockAsset.getSize().getValue());
    }

    @Test
    void matchOrder_SellOrder_Success() {
        when(fetchOrderService.getOrderById(2L)).thenReturn(sellOrder);
        when(assetManagementService.getAssetForUpdate(1L, "AAPL")).thenReturn(stockAsset);
        when(assetManagementService.getOrCreateAsset(1L, "TRY")).thenReturn(tryAsset);

        matchOrderService.matchOrder(2L);

        assertEquals(OrderStatus.MATCHED, sellOrder.getStatus());
        verify(orderRepository).save(sellOrder);
        assertEquals(new BigDecimal("15.00"), stockAsset.getSize().getValue());
        assertEquals(new BigDecimal("10550.00"), tryAsset.getSize().getValue());
    }

    @Test
    void matchOrder_AlreadyMatched_ThrowsException() {
        buyOrder.setStatus(OrderStatus.MATCHED);
        when(fetchOrderService.getOrderById(1L)).thenReturn(buyOrder);

        assertThrows(IllegalStateException.class, () -> matchOrderService.matchOrder(1L));
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    void matchOrder_CanceledOrder_ThrowsException() {
        buyOrder.setStatus(OrderStatus.CANCELED);
        when(fetchOrderService.getOrderById(1L)).thenReturn(buyOrder);

        assertThrows(IllegalStateException.class, () -> matchOrderService.matchOrder(1L));
        verify(orderRepository, never()).save(any(Order.class));
    }
}
