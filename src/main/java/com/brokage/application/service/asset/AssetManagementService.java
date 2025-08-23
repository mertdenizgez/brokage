package com.brokage.application.service.asset;

import com.brokage.domain.entity.Asset;
import com.brokage.domain.valueobject.AssetSymbol;
import com.brokage.domain.valueobject.Quantity;
import com.brokage.domain.valueobject.UsableSize;
import com.brokage.infrastructure.repository.AssetRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;



@Service
@RequiredArgsConstructor
public class AssetManagementService {
    
    private final AssetRepository assetRepository;
    
    @Transactional
    public Asset getOrCreateAsset(Long customerId, String assetName) {
        return assetRepository.findByCustomerIdAndAssetName(customerId, assetName)
                .orElseGet(() -> createAsset(customerId, assetName));
    }
    
    @Transactional
    public Asset getAssetForUpdate(Long customerId, String assetName) {
        return assetRepository.findByCustomerIdAndAssetNameForUpdate(customerId, assetName)
                .orElseThrow(() -> new IllegalArgumentException("Asset not found"));
    }
    
    private Asset createAsset(Long customerId, String assetName) {
        Asset asset = new Asset();
        asset.setCustomerId(customerId);
        asset.setAssetSymbol(AssetSymbol.of(assetName));
        asset.setSize(Quantity.zero());
        asset.setUsableSize(UsableSize.zero());
        return assetRepository.save(asset);
    }
}
