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
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AssetManagementServiceTest {

    @Mock
    private AssetRepository assetRepository;

    @InjectMocks
    private AssetManagementService assetManagementService;

    private Asset tryAsset;

    @BeforeEach
    void setUp() {
        tryAsset = new Asset();
        tryAsset.setId(1L);
        tryAsset.setCustomerId(1L);
        tryAsset.setAssetSymbol(AssetSymbol.of("TRY"));
        tryAsset.setSize(Quantity.of(new BigDecimal("10000")));
        tryAsset.setUsableSize(UsableSize.of(new BigDecimal("10000")));
    }

    @Test
    void getOrCreateAsset_ExistingAsset_ReturnsExisting() {
        when(assetRepository.findByCustomerIdAndAssetName(1L, "TRY")).thenReturn(Optional.of(tryAsset));

        Asset result = assetManagementService.getOrCreateAsset(1L, "TRY");

        assertEquals(tryAsset, result);
        verify(assetRepository).findByCustomerIdAndAssetName(1L, "TRY");
        verify(assetRepository, never()).save(any(Asset.class));
    }

    @Test
    void getOrCreateAsset_NewAsset_CreatesNew() {
        when(assetRepository.findByCustomerIdAndAssetName(1L, "GOOGL")).thenReturn(Optional.empty());
        when(assetRepository.save(any(Asset.class))).thenAnswer(invocation -> {
            Asset asset = invocation.getArgument(0);
            asset.setId(3L);
            return asset;
        });

        Asset result = assetManagementService.getOrCreateAsset(1L, "GOOGL");

        assertNotNull(result);
        assertEquals(1L, result.getCustomerId());
        assertEquals("GOOGL", result.getAssetSymbol().getSymbol());
        assertEquals(new BigDecimal("0.00"), result.getSize().getValue());
        assertEquals(BigDecimal.ZERO, result.getUsableSize().getValue());
        verify(assetRepository).save(any(Asset.class));
    }

    @Test
    void getAssetForUpdate_ExistingAsset_Success() {
        when(assetRepository.findByCustomerIdAndAssetNameForUpdate(1L, "TRY")).thenReturn(Optional.of(tryAsset));

        Asset result = assetManagementService.getAssetForUpdate(1L, "TRY");

        assertEquals(tryAsset, result);
        verify(assetRepository).findByCustomerIdAndAssetNameForUpdate(1L, "TRY");
    }

    @Test
    void getAssetForUpdate_NonExistingAsset_ThrowsException() {
        when(assetRepository.findByCustomerIdAndAssetNameForUpdate(1L, "NONEXISTENT"))
                .thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, 
                () -> assetManagementService.getAssetForUpdate(1L, "NONEXISTENT"));
    }
}
