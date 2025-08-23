package com.brokage.application.service.order;

import com.brokage.application.dto.request.CreateOrderRequest;
import com.brokage.application.mapper.OrderMapper;
import com.brokage.application.service.asset.AssetManagementService;
import com.brokage.domain.entity.Asset;
import com.brokage.domain.entity.Order;
import com.brokage.domain.enums.OrderSide;
import com.brokage.domain.valueobject.Money;
import com.brokage.domain.valueobject.Quantity;
import com.brokage.infrastructure.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class CreateOrderService {
    
    private final OrderRepository orderRepository;
    private final AssetManagementService assetManagementService;
    private final OrderMapper orderMapper;
    
    @Transactional
    public Order createOrder(CreateOrderRequest request) {
        validateOrderRequest(request);

        if (request.getOrderSide() == OrderSide.BUY) {
            handleBuyOrder(request);
        } else {
            handleSellOrder(request);
        }

        Order order = orderMapper.toEntity(request);
        return orderRepository.save(order);
    }
    
    private void validateOrderRequest(CreateOrderRequest request) {
        if (request.getSize().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Order size must be positive");
        }
        if (request.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Order price must be positive");
        }
    }
    
    private void handleBuyOrder(CreateOrderRequest request) {
        Asset tryAsset = assetManagementService.getAssetForUpdate(request.getCustomerId(), "TRY");
        Money totalAmount = Money.of(request.getPrice()).multiply(Quantity.of(request.getSize()));
        
        if (tryAsset.getUsableSize().compareTo(totalAmount.getAmount()) < 0) {
            throw new IllegalStateException("Insufficient TRY balance");
        }
        
        tryAsset.reserveAmount(totalAmount.getAmount());
    }
    
    private void handleSellOrder(CreateOrderRequest request) {
        Asset asset = assetManagementService.getAssetForUpdate(request.getCustomerId(), request.getAssetName());
        
        if (asset.getUsableSize().compareTo(request.getSize()) < 0) {
            throw new IllegalStateException("Insufficient asset balance");
        }
        
        asset.reserveAmount(request.getSize());
    }
}
