package com.brokage.application.service.order;

import com.brokage.application.dto.request.CreateOrderRequest;
import com.brokage.application.mapper.OrderMapper;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateOrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private AssetManagementService assetManagementService;

    @Mock
    private OrderMapper orderMapper;

    @InjectMocks
    private CreateOrderService createOrderService;

    private CreateOrderRequest buyOrderRequest;
    private CreateOrderRequest sellOrderRequest;
    private Order mockOrder;
    private Asset tryAsset;
    private Asset stockAsset;

    @BeforeEach
    void setUp() {
        buyOrderRequest = new CreateOrderRequest();
        buyOrderRequest.setCustomerId(1L);
        buyOrderRequest.setAssetName("AAPL");
        buyOrderRequest.setOrderSide(OrderSide.BUY);
        buyOrderRequest.setSize(new BigDecimal("10"));
        buyOrderRequest.setPrice(new BigDecimal("150.00"));

        sellOrderRequest = new CreateOrderRequest();
        sellOrderRequest.setCustomerId(1L);
        sellOrderRequest.setAssetName("AAPL");
        sellOrderRequest.setOrderSide(OrderSide.SELL);
        sellOrderRequest.setSize(new BigDecimal("5"));
        sellOrderRequest.setPrice(new BigDecimal("155.00"));

        mockOrder = new Order();
        mockOrder.setId(1L);
        mockOrder.setCustomerId(1L);
        mockOrder.setAssetSymbol(AssetSymbol.of("AAPL"));
        mockOrder.setOrderSide(OrderSide.BUY);
        mockOrder.setSize(Quantity.of(new BigDecimal("10")));
        mockOrder.setPrice(Money.of(new BigDecimal("150.00")));
        mockOrder.setStatus(OrderStatus.PENDING);

        tryAsset = new Asset();
        tryAsset.setId(1L);
        tryAsset.setCustomerId(1L);
        tryAsset.setAssetSymbol(AssetSymbol.of("TRY"));
        tryAsset.setSize(Quantity.of(new BigDecimal("10000.00")));
        tryAsset.setUsableSize(UsableSize.of(new BigDecimal("10000.00")));

        stockAsset = new Asset();
        stockAsset.setId(2L);
        stockAsset.setCustomerId(1L);
        stockAsset.setAssetSymbol(AssetSymbol.of("AAPL"));
        stockAsset.setSize(Quantity.of(new BigDecimal("20")));
        stockAsset.setUsableSize(UsableSize.of(new BigDecimal("20")));
    }

    @Test
    void createBuyOrder_WithSufficientBalance_ShouldSucceed() {
        // Given
        // Create a fresh asset for this test
        Asset testTryAsset = new Asset();
        testTryAsset.setId(1L);
        testTryAsset.setCustomerId(1L);
        testTryAsset.setAssetSymbol(AssetSymbol.of("TRY"));
        testTryAsset.setSize(Quantity.of(new BigDecimal("10000.00")));
        testTryAsset.setUsableSize(UsableSize.of(new BigDecimal("10000.00")));

        when(assetManagementService.getAssetForUpdate(1L, "TRY")).thenReturn(testTryAsset);
        when(orderMapper.toEntity(buyOrderRequest)).thenReturn(mockOrder);
        when(orderRepository.save(any(Order.class))).thenReturn(mockOrder);

        // When
        Order result = createOrderService.createOrder(buyOrderRequest);

        // Then
        assertNotNull(result);
        assertEquals(mockOrder.getId(), result.getId());
        assertEquals(mockOrder.getCustomerId(), result.getCustomerId());
        assertEquals(mockOrder.getAssetSymbol().getSymbol(), result.getAssetSymbol().getSymbol());
        assertEquals(OrderSide.BUY, result.getOrderSide());

        verify(assetManagementService).getAssetForUpdate(1L, "TRY");
        verify(orderMapper).toEntity(buyOrderRequest);
        verify(orderRepository).save(any(Order.class));
        
        // Verify the asset balance was reduced correctly
        assertEquals(0, new BigDecimal("8500.00").compareTo(testTryAsset.getUsableSize().getValue())); // 10000 - 1500
    }

    @Test
    void createBuyOrder_WithInsufficientBalance_ShouldThrowException() {
        // Given
        tryAsset.setUsableSize(UsableSize.of(new BigDecimal("1000.00"))); // Insufficient for 1500.00
        when(assetManagementService.getAssetForUpdate(1L, "TRY")).thenReturn(tryAsset);

        // When & Then
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            createOrderService.createOrder(buyOrderRequest);
        });

        assertEquals("Insufficient TRY balance", exception.getMessage());
        verify(assetManagementService).getAssetForUpdate(1L, "TRY");
        verify(orderMapper, never()).toEntity(any());
        verify(orderRepository, never()).save(any());
    }

    @Test
    void createSellOrder_WithSufficientAssets_ShouldSucceed() {
        // Given
        // Create a fresh asset for this test
        Asset testStockAsset = new Asset();
        testStockAsset.setId(2L);
        testStockAsset.setCustomerId(1L);
        testStockAsset.setAssetSymbol(AssetSymbol.of("AAPL"));
        testStockAsset.setSize(Quantity.of(new BigDecimal("20")));
        testStockAsset.setUsableSize(UsableSize.of(new BigDecimal("20")));

        when(assetManagementService.getAssetForUpdate(1L, "AAPL")).thenReturn(testStockAsset);
        when(orderMapper.toEntity(sellOrderRequest)).thenReturn(mockOrder);
        when(orderRepository.save(any(Order.class))).thenReturn(mockOrder);

        // When
        Order result = createOrderService.createOrder(sellOrderRequest);

        // Then
        assertNotNull(result);
        assertEquals(mockOrder.getId(), result.getId());
        assertEquals(mockOrder.getCustomerId(), result.getCustomerId());
        assertEquals(mockOrder.getAssetSymbol().getSymbol(), result.getAssetSymbol().getSymbol());

        verify(assetManagementService).getAssetForUpdate(1L, "AAPL");
        verify(orderMapper).toEntity(sellOrderRequest);
        verify(orderRepository).save(any(Order.class));
        
        // Verify the asset balance was reduced correctly
        assertEquals(0, new BigDecimal("15").compareTo(testStockAsset.getUsableSize().getValue())); // 20 - 5
    }

    @Test
    void createSellOrder_WithInsufficientAssets_ShouldThrowException() {
        // Given
        stockAsset.setUsableSize(UsableSize.of(new BigDecimal("3"))); // Insufficient for sell order of 5
        when(assetManagementService.getAssetForUpdate(1L, "AAPL")).thenReturn(stockAsset);

        // When & Then
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            createOrderService.createOrder(sellOrderRequest);
        });

        assertEquals("Insufficient asset balance", exception.getMessage());
        verify(assetManagementService).getAssetForUpdate(1L, "AAPL");
        verify(orderMapper, never()).toEntity(any());
        verify(orderRepository, never()).save(any());
    }

    @Test
    void createOrder_WithZeroSize_ShouldThrowException() {
        // Given
        buyOrderRequest.setSize(BigDecimal.ZERO);

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            createOrderService.createOrder(buyOrderRequest);
        });

        assertEquals("Order size must be positive", exception.getMessage());
        verify(assetManagementService, never()).getAssetForUpdate(any(), any());
        verify(orderMapper, never()).toEntity(any());
        verify(orderRepository, never()).save(any());
    }

    @Test
    void createOrder_WithNegativeSize_ShouldThrowException() {
        // Given
        buyOrderRequest.setSize(new BigDecimal("-5"));

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            createOrderService.createOrder(buyOrderRequest);
        });

        assertEquals("Order size must be positive", exception.getMessage());
        verify(assetManagementService, never()).getAssetForUpdate(any(), any());
        verify(orderMapper, never()).toEntity(any());
        verify(orderRepository, never()).save(any());
    }

    @Test
    void createOrder_WithZeroPrice_ShouldThrowException() {
        // Given
        buyOrderRequest.setPrice(BigDecimal.ZERO);

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            createOrderService.createOrder(buyOrderRequest);
        });

        assertEquals("Order price must be positive", exception.getMessage());
        verify(assetManagementService, never()).getAssetForUpdate(any(), any());
        verify(orderMapper, never()).toEntity(any());
        verify(orderRepository, never()).save(any());
    }

    @Test
    void createOrder_WithNegativePrice_ShouldThrowException() {
        // Given
        buyOrderRequest.setPrice(new BigDecimal("-100"));

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            createOrderService.createOrder(buyOrderRequest);
        });

        assertEquals("Order price must be positive", exception.getMessage());
        verify(assetManagementService, never()).getAssetForUpdate(any(), any());
        verify(orderMapper, never()).toEntity(any());
        verify(orderRepository, never()).save(any());
    }

    @Test
    void createBuyOrder_CalculatesTotalAmountCorrectly() {
        // Given
        buyOrderRequest.setSize(new BigDecimal("15.5"));
        buyOrderRequest.setPrice(new BigDecimal("123.45"));
        BigDecimal expectedTotal = new BigDecimal("15.5").multiply(new BigDecimal("123.45")); // 1913.475

        // Create a fresh asset with sufficient balance for this test
        Asset testTryAsset = new Asset();
        testTryAsset.setId(1L);
        testTryAsset.setCustomerId(1L);
        testTryAsset.setAssetSymbol(AssetSymbol.of("TRY"));
        testTryAsset.setSize(Quantity.of(new BigDecimal("10000.00")));
        testTryAsset.setUsableSize(UsableSize.of(new BigDecimal("10000.00")));

        when(assetManagementService.getAssetForUpdate(1L, "TRY")).thenReturn(testTryAsset);
        when(orderMapper.toEntity(buyOrderRequest)).thenReturn(mockOrder);
        when(orderRepository.save(any(Order.class))).thenReturn(mockOrder);

        // When
        createOrderService.createOrder(buyOrderRequest);

        // Then
        // Verify the correct amount was reserved by checking the remaining balance
        // Note: Money value object rounds to 2 decimal places, so 1913.475 becomes 1913.48
        BigDecimal expectedTotalRounded = new BigDecimal("1913.48");
        BigDecimal expectedRemainingBalance = new BigDecimal("10000.00").subtract(expectedTotalRounded);
        assertEquals(0, expectedRemainingBalance.compareTo(testTryAsset.getUsableSize().getValue()));
    }

    @Test
    void createOrder_WhenAssetManagementServiceThrowsException_ShouldPropagateException() {
        // Given
        when(assetManagementService.getAssetForUpdate(1L, "TRY"))
                .thenThrow(new RuntimeException("Asset not found"));

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            createOrderService.createOrder(buyOrderRequest);
        });

        assertEquals("Asset not found", exception.getMessage());
        verify(orderMapper, never()).toEntity(any());
        verify(orderRepository, never()).save(any());
    }

    @Test
    void createOrder_WhenOrderMapperThrowsException_ShouldPropagateException() {
        // Given
        when(assetManagementService.getAssetForUpdate(1L, "TRY")).thenReturn(tryAsset);
        when(orderMapper.toEntity(buyOrderRequest)).thenThrow(new RuntimeException("Mapping failed"));

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            createOrderService.createOrder(buyOrderRequest);
        });

        assertEquals("Mapping failed", exception.getMessage());
        verify(orderRepository, never()).save(any());
    }

    @Test
    void createOrder_WhenRepositoryThrowsException_ShouldPropagateException() {
        // Given
        when(assetManagementService.getAssetForUpdate(1L, "TRY")).thenReturn(tryAsset);
        when(orderMapper.toEntity(buyOrderRequest)).thenReturn(mockOrder);
        when(orderRepository.save(any(Order.class))).thenThrow(new RuntimeException("Database error"));

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            createOrderService.createOrder(buyOrderRequest);
        });

        assertEquals("Database error", exception.getMessage());
    }
}
