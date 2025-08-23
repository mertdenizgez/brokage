package com.brokage.application.service.asset;

import com.brokage.domain.entity.Asset;
import com.brokage.domain.valueobject.UsableSize;
import com.brokage.infrastructure.repository.AssetRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AssetInitializationServiceTest {

    @Mock
    private AssetRepository assetRepository;

    @InjectMocks
    private AssetInitializationService assetInitializationService;

    @Test
    void initializeCustomerWithTRY_Success() {
        BigDecimal initialAmount = new BigDecimal("50000");
        when(assetRepository.save(any(Asset.class))).thenAnswer(invocation -> invocation.getArgument(0));

        assetInitializationService.initializeCustomerWithTRY(1L, initialAmount);

        verify(assetRepository).save(any(Asset.class));
    }

    @Test
    void initializeCustomerWithTRY_ZeroAmount_Success() {
        BigDecimal initialAmount = BigDecimal.ZERO;
        when(assetRepository.save(any(Asset.class))).thenAnswer(invocation -> invocation.getArgument(0));

        assetInitializationService.initializeCustomerWithTRY(2L, initialAmount);

        verify(assetRepository).save(any(Asset.class));
    }
}
