package com.brokage.application.service.order;

import com.brokage.application.service.asset.AssetManagementService;
import com.brokage.domain.entity.Asset;
import com.brokage.domain.entity.Order;
import com.brokage.domain.enums.OrderSide;
import com.brokage.infrastructure.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CancelOrderService {
    
    private final OrderRepository orderRepository;
    private final AssetManagementService assetManagementService;
    private final FetchOrderService fetchOrderService;
    
    @Transactional
    public void cancelOrder(Long orderId, Long customerId) {
        Order order = fetchOrderService.getOrderById(orderId);
        
        if (!order.getCustomerId().equals(customerId)) {
            throw new IllegalArgumentException("Order does not belong to customer");
        }
        
        if (!order.canBeCanceled()) {
            throw new IllegalStateException("Order cannot be canceled");
        }
        
        if (order.getOrderSide() == OrderSide.BUY) {
            Asset tryAsset = assetManagementService.getAssetForUpdate(customerId, "TRY");
            tryAsset.releaseAmount(order.getTotalAmount().getAmount());
        } else {
            Asset asset = assetManagementService.getAssetForUpdate(customerId, order.getAssetSymbol().getSymbol());
            asset.releaseAmount(order.getSize().getValue());
        }
        
        order.cancel();
        orderRepository.save(order);
    }
}
