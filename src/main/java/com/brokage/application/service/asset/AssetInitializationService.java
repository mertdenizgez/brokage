package com.brokage.application.service.asset;

import com.brokage.domain.entity.Asset;
import com.brokage.domain.valueobject.AssetSymbol;
import com.brokage.domain.valueobject.Quantity;
import com.brokage.domain.valueobject.UsableSize;
import com.brokage.infrastructure.repository.AssetRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class AssetInitializationService {
    
    private final AssetRepository assetRepository;
    
    @Transactional
    public void initializeCustomerWithTRY(Long customerId, BigDecimal initialAmount) {
        Asset tryAsset = new Asset();
        tryAsset.setCustomerId(customerId);
        tryAsset.setAssetSymbol(AssetSymbol.trySymbol());
        tryAsset.setSize(Quantity.of(initialAmount));
        tryAsset.setUsableSize(UsableSize.of(initialAmount));
        assetRepository.save(tryAsset);
    }
}
