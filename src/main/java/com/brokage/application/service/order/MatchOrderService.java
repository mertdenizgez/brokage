package com.brokage.application.service.order;

import com.brokage.application.service.asset.AssetManagementService;
import com.brokage.domain.entity.Asset;
import com.brokage.domain.entity.Order;
import com.brokage.domain.enums.OrderSide;
import com.brokage.domain.enums.OrderStatus;
import com.brokage.domain.valueobject.Quantity;
import com.brokage.infrastructure.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MatchOrderService {
    
    private final OrderRepository orderRepository;
    private final AssetManagementService assetManagementService;
    private final FetchOrderService fetchOrderService;
    
    @Transactional
    public void matchOrder(Long orderId) {
        Order order = fetchOrderService.getOrderById(orderId);
        
        if (order.getStatus() != OrderStatus.PENDING) {
            throw new IllegalStateException("Only pending orders can be matched");
        }
        
        if (order.getOrderSide() == OrderSide.BUY) {
            handleBuyOrderMatching(order);
        } else {
            handleSellOrderMatching(order);
        }
        
        order.match();
        orderRepository.save(order);
    }
    
    private void handleBuyOrderMatching(Order order) {
        Asset targetAsset = assetManagementService.getOrCreateAsset(order.getCustomerId(), order.getAssetSymbol().getSymbol());
        
        // For buy orders, we just add the bought asset (TRY was already reserved)
        targetAsset.addSize(order.getSize());
    }
    
    private void handleSellOrderMatching(Order order) {
        Asset asset = assetManagementService.getAssetForUpdate(order.getCustomerId(), order.getAssetSymbol().getSymbol());
        Asset tryAsset = assetManagementService.getOrCreateAsset(order.getCustomerId(), "TRY");
        
        // For sell orders, subtract the sold asset and add the received TRY amount
        asset.subtractSize(order.getSize());
        tryAsset.addSize(Quantity.of(order.getTotalAmount().getAmount()));
    }
}
