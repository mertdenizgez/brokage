package com.brokage.domain.valueobject;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class UsableSizeTest {

    @Test
    void constructor_ValidValue_Success() {
        BigDecimal value = new BigDecimal("100");
        
        UsableSize usableSize = new UsableSize(value);
        
        assertEquals(value, usableSize.getValue());
    }

    @Test
    void constructor_NullValue_ThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> new UsableSize(null));
    }

    @Test
    void constructor_NegativeValue_ThrowsException() {
        BigDecimal negativeValue = new BigDecimal("-10");
        
        assertThrows(IllegalArgumentException.class, () -> new UsableSize(negativeValue));
    }

    @Test
    void of_ValidValue_Success() {
        BigDecimal value = new BigDecimal("100");
        
        UsableSize usableSize = UsableSize.of(value);
        
        assertEquals(value, usableSize.getValue());
    }

    @Test
    void zero_ReturnsZeroUsableSize() {
        UsableSize usableSize = UsableSize.zero();
        
        assertEquals(BigDecimal.ZERO, usableSize.getValue());
        assertTrue(usableSize.isZero());
    }

    @Test
    void reserve_SufficientAmount_Success() {
        UsableSize usableSize = UsableSize.of(new BigDecimal("100"));
        BigDecimal reserveAmount = new BigDecimal("30");
        
        UsableSize result = usableSize.reserve(reserveAmount);
        
        assertEquals(new BigDecimal("70"), result.getValue());
        assertEquals(new BigDecimal("100"), usableSize.getValue()); // Original unchanged
    }

    @Test
    void reserve_InsufficientAmount_ThrowsException() {
        UsableSize usableSize = UsableSize.of(new BigDecimal("50"));
        BigDecimal reserveAmount = new BigDecimal("100");
        
        assertThrows(IllegalStateException.class, () -> usableSize.reserve(reserveAmount));
    }

    @Test
    void reserve_NullAmount_ThrowsException() {
        UsableSize usableSize = UsableSize.of(new BigDecimal("100"));
        
        assertThrows(IllegalArgumentException.class, () -> usableSize.reserve(null));
    }

    @Test
    void reserve_NegativeAmount_ThrowsException() {
        UsableSize usableSize = UsableSize.of(new BigDecimal("100"));
        BigDecimal negativeAmount = new BigDecimal("-10");
        
        assertThrows(IllegalArgumentException.class, () -> usableSize.reserve(negativeAmount));
    }

    @Test
    void release_ValidAmount_Success() {
        UsableSize usableSize = UsableSize.of(new BigDecimal("70"));
        BigDecimal releaseAmount = new BigDecimal("20");
        
        UsableSize result = usableSize.release(releaseAmount);
        
        assertEquals(new BigDecimal("90"), result.getValue());
        assertEquals(new BigDecimal("70"), usableSize.getValue()); // Original unchanged
    }

    @Test
    void release_NullAmount_ThrowsException() {
        UsableSize usableSize = UsableSize.of(new BigDecimal("100"));
        
        assertThrows(IllegalArgumentException.class, () -> usableSize.release(null));
    }

    @Test
    void release_NegativeAmount_ThrowsException() {
        UsableSize usableSize = UsableSize.of(new BigDecimal("100"));
        BigDecimal negativeAmount = new BigDecimal("-10");
        
        assertThrows(IllegalArgumentException.class, () -> usableSize.release(negativeAmount));
    }

    @Test
    void add_ValidAmount_Success() {
        UsableSize usableSize = UsableSize.of(new BigDecimal("100"));
        BigDecimal addAmount = new BigDecimal("50");
        
        UsableSize result = usableSize.add(addAmount);
        
        assertEquals(new BigDecimal("150"), result.getValue());
        assertEquals(new BigDecimal("100"), usableSize.getValue()); // Original unchanged
    }

    @Test
    void add_NullAmount_ThrowsException() {
        UsableSize usableSize = UsableSize.of(new BigDecimal("100"));
        
        assertThrows(IllegalArgumentException.class, () -> usableSize.add(null));
    }

    @Test
    void add_NegativeAmount_ThrowsException() {
        UsableSize usableSize = UsableSize.of(new BigDecimal("100"));
        BigDecimal negativeAmount = new BigDecimal("-10");
        
        assertThrows(IllegalArgumentException.class, () -> usableSize.add(negativeAmount));
    }

    @Test
    void canReserve_SufficientAmount_ReturnsTrue() {
        UsableSize usableSize = UsableSize.of(new BigDecimal("100"));
        BigDecimal amount = new BigDecimal("50");
        
        assertTrue(usableSize.canReserve(amount));
    }

    @Test
    void canReserve_InsufficientAmount_ReturnsFalse() {
        UsableSize usableSize = UsableSize.of(new BigDecimal("50"));
        BigDecimal amount = new BigDecimal("100");
        
        assertFalse(usableSize.canReserve(amount));
    }

    @Test
    void canReserve_NullAmount_ReturnsFalse() {
        UsableSize usableSize = UsableSize.of(new BigDecimal("100"));
        
        assertFalse(usableSize.canReserve(null));
    }

    @Test
    void isZero_ZeroValue_ReturnsTrue() {
        UsableSize usableSize = UsableSize.zero();
        
        assertTrue(usableSize.isZero());
    }

    @Test
    void isZero_NonZeroValue_ReturnsFalse() {
        UsableSize usableSize = UsableSize.of(new BigDecimal("100"));
        
        assertFalse(usableSize.isZero());
    }

    @Test
    void isGreaterThan_GreaterValue_ReturnsTrue() {
        UsableSize usableSize = UsableSize.of(new BigDecimal("100"));
        BigDecimal amount = new BigDecimal("50");
        
        assertTrue(usableSize.isGreaterThan(amount));
    }

    @Test
    void isGreaterThan_SmallerValue_ReturnsFalse() {
        UsableSize usableSize = UsableSize.of(new BigDecimal("50"));
        BigDecimal amount = new BigDecimal("100");
        
        assertFalse(usableSize.isGreaterThan(amount));
    }

    @Test
    void isGreaterThan_NullValue_ReturnsTrue() {
        UsableSize usableSize = UsableSize.of(new BigDecimal("100"));
        
        assertTrue(usableSize.isGreaterThan(null));
    }

    @Test
    void isLessThan_SmallerValue_ReturnsTrue() {
        UsableSize usableSize = UsableSize.of(new BigDecimal("50"));
        BigDecimal amount = new BigDecimal("100");
        
        assertTrue(usableSize.isLessThan(amount));
    }

    @Test
    void isLessThan_GreaterValue_ReturnsFalse() {
        UsableSize usableSize = UsableSize.of(new BigDecimal("100"));
        BigDecimal amount = new BigDecimal("50");
        
        assertFalse(usableSize.isLessThan(amount));
    }

    @Test
    void isLessThan_NullValue_ReturnsFalse() {
        UsableSize usableSize = UsableSize.of(new BigDecimal("100"));
        
        assertFalse(usableSize.isLessThan(null));
    }

    @Test
    void compareTo_ValidValue_ReturnsCorrectComparison() {
        UsableSize usableSize = UsableSize.of(new BigDecimal("100"));
        
        assertEquals(1, usableSize.compareTo(new BigDecimal("50")));
        assertEquals(0, usableSize.compareTo(new BigDecimal("100")));
        assertEquals(-1, usableSize.compareTo(new BigDecimal("150")));
    }

    @Test
    void compareTo_NullValue_ThrowsException() {
        UsableSize usableSize = UsableSize.of(new BigDecimal("100"));
        
        assertThrows(IllegalArgumentException.class, () -> usableSize.compareTo(null));
    }

    @Test
    void equals_SameValue_ReturnsTrue() {
        UsableSize usableSize1 = UsableSize.of(new BigDecimal("100"));
        UsableSize usableSize2 = UsableSize.of(new BigDecimal("100"));
        
        assertEquals(usableSize1, usableSize2);
    }

    @Test
    void equals_DifferentValue_ReturnsFalse() {
        UsableSize usableSize1 = UsableSize.of(new BigDecimal("100"));
        UsableSize usableSize2 = UsableSize.of(new BigDecimal("50"));
        
        assertNotEquals(usableSize1, usableSize2);
    }

    @Test
    void hashCode_SameValue_SameHashCode() {
        UsableSize usableSize1 = UsableSize.of(new BigDecimal("100"));
        UsableSize usableSize2 = UsableSize.of(new BigDecimal("100"));
        
        assertEquals(usableSize1.hashCode(), usableSize2.hashCode());
    }
}
