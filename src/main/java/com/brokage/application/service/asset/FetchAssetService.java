package com.brokage.application.service.asset;

import com.brokage.domain.entity.Asset;
import com.brokage.infrastructure.repository.AssetRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FetchAssetService {
    
    private final AssetRepository assetRepository;
    
    @Transactional(readOnly = true)
    public List<Asset> getCustomerAssets(Long customerId) {
        return assetRepository.findByCustomerId(customerId);
    }
}
