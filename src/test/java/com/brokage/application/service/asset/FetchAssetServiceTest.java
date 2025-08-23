package com.brokage.application.service.asset;

import com.brokage.domain.entity.Asset;
import com.brokage.domain.valueobject.AssetSymbol;
import com.brokage.domain.valueobject.Quantity;
import com.brokage.domain.valueobject.UsableSize;
import com.brokage.infrastructure.repository.AssetRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FetchAssetServiceTest {

    @Mock
    private AssetRepository assetRepository;

    @InjectMocks
    private FetchAssetService fetchAssetService;

    private Asset tryAsset;
    private Asset stockAsset;

    @BeforeEach
    void setUp() {
        tryAsset = new Asset();
        tryAsset.setId(1L);
        tryAsset.setCustomerId(1L);
        tryAsset.setAssetSymbol(AssetSymbol.of("TRY"));
        tryAsset.setSize(Quantity.of(new BigDecimal("10000")));
        tryAsset.setUsableSize(UsableSize.of(new BigDecimal("10000")));

        stockAsset = new Asset();
        stockAsset.setId(2L);
        stockAsset.setCustomerId(1L);
        stockAsset.setAssetSymbol(AssetSymbol.of("AAPL"));
        stockAsset.setSize(Quantity.of(new BigDecimal("100")));
        stockAsset.setUsableSize(UsableSize.of(new BigDecimal("100")));
    }

    @Test
    void getCustomerAssets_Success() {
        List<Asset> expectedAssets = Arrays.asList(tryAsset, stockAsset);
        when(assetRepository.findByCustomerId(1L)).thenReturn(expectedAssets);

        List<Asset> result = fetchAssetService.getCustomerAssets(1L);

        assertEquals(2, result.size());
        assertEquals(expectedAssets, result);
        verify(assetRepository).findByCustomerId(1L);
    }
}
