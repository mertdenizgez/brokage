package com.brokage.domain.entity;

import com.brokage.domain.valueobject.AssetSymbol;
import com.brokage.domain.valueobject.Quantity;
import com.brokage.domain.valueobject.UsableSize;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class AssetTest {

    private Asset asset;

    @BeforeEach
    void setUp() {
        asset = new Asset();
        asset.setCustomerId(1L);
        asset.setAssetSymbol(AssetSymbol.of("AAPL"));
        asset.setSize(Quantity.of(new BigDecimal("100")));
        asset.setUsableSize(UsableSize.of(new BigDecimal("100")));
    }

    @Test
    void reserveAmount_SufficientUsableSize_Success() {
        BigDecimal reserveAmount = new BigDecimal("30");
        
        asset.reserveAmount(reserveAmount);
        
        assertEquals(new BigDecimal("70"), asset.getUsableSize().getValue());
        assertEquals(new BigDecimal("100.00"), asset.getSize().getValue());
    }

    @Test
    void reserveAmount_InsufficientUsableSize_ThrowsException() {
        BigDecimal reserveAmount = new BigDecimal("150");
        
        assertThrows(IllegalStateException.class, () -> asset.reserveAmount(reserveAmount));
        assertEquals(new BigDecimal("100"), asset.getUsableSize().getValue());
    }

    @Test
    void releaseAmount_Success() {
        asset.setUsableSize(UsableSize.of(new BigDecimal("70")));
        BigDecimal releaseAmount = new BigDecimal("20");
        
        asset.releaseAmount(releaseAmount);
        
        assertEquals(new BigDecimal("90"), asset.getUsableSize().getValue());
    }

    @Test
    void addSize_Success() {
        BigDecimal addAmount = new BigDecimal("50");
        
        asset.addSize(Quantity.of(addAmount));
        
        assertEquals(new BigDecimal("150.00"), asset.getSize().getValue());
        assertEquals(new BigDecimal("150.00"), asset.getUsableSize().getValue());
    }

    @Test
    void subtractSize_SufficientSize_Success() {
        BigDecimal subtractAmount = new BigDecimal("30");
        
        asset.subtractSize(Quantity.of(subtractAmount));
        
        assertEquals(new BigDecimal("70.00"), asset.getSize().getValue());
        assertEquals(new BigDecimal("100"), asset.getUsableSize().getValue());
    }

    @Test
    void subtractSize_InsufficientSize_ThrowsException() {
        BigDecimal subtractAmount = new BigDecimal("150");
        
        assertThrows(IllegalStateException.class, () -> asset.subtractSize(Quantity.of(subtractAmount)));
        assertEquals(new BigDecimal("100.00"), asset.getSize().getValue());
    }
}
