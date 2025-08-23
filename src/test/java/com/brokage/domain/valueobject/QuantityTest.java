package com.brokage.domain.valueobject;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class QuantityTest {

    @Test
    void constructor_ValidValue_Success() {
        BigDecimal value = new BigDecimal("10.50");
        
        Quantity quantity = new Quantity(value);
        
        assertEquals(new BigDecimal("10.50"), quantity.getValue());
    }

    @Test
    void constructor_NullValue_ThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> new Quantity(null));
    }

    @Test
    void constructor_NegativeValue_ThrowsException() {
        BigDecimal negativeValue = new BigDecimal("-5.00");
        
        assertThrows(IllegalArgumentException.class, () -> new Quantity(negativeValue));
    }

    @Test
    void constructor_RoundsToTwoDecimals() {
        BigDecimal value = new BigDecimal("10.123456");
        
        Quantity quantity = new Quantity(value);
        
        assertEquals(new BigDecimal("10.12"), quantity.getValue());
    }

    @Test
    void of_BigDecimal_Success() {
        BigDecimal value = new BigDecimal("5.75");
        
        Quantity quantity = Quantity.of(value);
        
        assertEquals(value, quantity.getValue());
    }

    @Test
    void of_String_Success() {
        Quantity quantity = Quantity.of("7.25");
        
        assertEquals(new BigDecimal("7.25"), quantity.getValue());
    }

    @Test
    void of_Double_Success() {
        Quantity quantity = Quantity.of(3.50);
        
        assertEquals(new BigDecimal("3.50"), quantity.getValue());
    }

    @Test
    void of_Int_Success() {
        Quantity quantity = Quantity.of(5);
        
        assertEquals(new BigDecimal("5.00"), quantity.getValue());
    }

    @Test
    void zero_ReturnsZeroQuantity() {
        Quantity quantity = Quantity.zero();
        
        assertEquals(new BigDecimal("0.00"), quantity.getValue());
        assertTrue(quantity.isZero());
    }

    @Test
    void one_ReturnsOneQuantity() {
        Quantity quantity = Quantity.one();
        
        assertEquals(new BigDecimal("1.00"), quantity.getValue());
    }

    @Test
    void add_ValidQuantity_Success() {
        Quantity quantity1 = Quantity.of("10.00");
        Quantity quantity2 = Quantity.of("5.25");
        
        Quantity result = quantity1.add(quantity2);
        
        assertEquals(new BigDecimal("15.25"), result.getValue());
        // Original objects unchanged
        assertEquals(new BigDecimal("10.00"), quantity1.getValue());
        assertEquals(new BigDecimal("5.25"), quantity2.getValue());
    }

    @Test
    void add_NullQuantity_ThrowsException() {
        Quantity quantity = Quantity.of("10.00");
        
        assertThrows(IllegalArgumentException.class, () -> quantity.add(null));
    }

    @Test
    void subtract_ValidQuantity_Success() {
        Quantity quantity1 = Quantity.of("10.00");
        Quantity quantity2 = Quantity.of("3.25");
        
        Quantity result = quantity1.subtract(quantity2);
        
        assertEquals(new BigDecimal("6.75"), result.getValue());
    }

    @Test
    void subtract_InsufficientQuantity_ThrowsException() {
        Quantity quantity1 = Quantity.of("5.00");
        Quantity quantity2 = Quantity.of("10.00");
        
        assertThrows(IllegalStateException.class, () -> quantity1.subtract(quantity2));
    }

    @Test
    void subtract_NullQuantity_ThrowsException() {
        Quantity quantity = Quantity.of("10.00");
        
        assertThrows(IllegalArgumentException.class, () -> quantity.subtract(null));
    }

    @Test
    void multiply_BigDecimal_Success() {
        Quantity quantity = Quantity.of("5.00");
        BigDecimal factor = new BigDecimal("2.5");
        
        Quantity result = quantity.multiply(factor);
        
        assertEquals(new BigDecimal("12.50"), result.getValue());
    }

    @Test
    void multiply_NullFactor_ThrowsException() {
        Quantity quantity = Quantity.of("10.00");
        
        assertThrows(IllegalArgumentException.class, () -> quantity.multiply(null));
    }

    @Test
    void multiply_NegativeFactor_ThrowsException() {
        Quantity quantity = Quantity.of("10.00");
        BigDecimal negativeFactor = new BigDecimal("-2.0");
        
        assertThrows(IllegalArgumentException.class, () -> quantity.multiply(negativeFactor));
    }

    @Test
    void divide_ValidDivisor_Success() {
        Quantity quantity = Quantity.of("10.00");
        BigDecimal divisor = new BigDecimal("4");
        
        Quantity result = quantity.divide(divisor);
        
        assertEquals(new BigDecimal("2.50"), result.getValue());
    }

    @Test
    void divide_WithRounding_Success() {
        Quantity quantity = Quantity.of("10.00");
        BigDecimal divisor = new BigDecimal("3");
        
        Quantity result = quantity.divide(divisor);
        
        assertEquals(new BigDecimal("3.33"), result.getValue());
    }

    @Test
    void divide_NullDivisor_ThrowsException() {
        Quantity quantity = Quantity.of("10.00");
        
        assertThrows(IllegalArgumentException.class, () -> quantity.divide(null));
    }

    @Test
    void divide_ZeroDivisor_ThrowsException() {
        Quantity quantity = Quantity.of("10.00");
        
        assertThrows(IllegalArgumentException.class, () -> quantity.divide(BigDecimal.ZERO));
    }

    @Test
    void divide_NegativeDivisor_ThrowsException() {
        Quantity quantity = Quantity.of("10.00");
        BigDecimal negativeDivisor = new BigDecimal("-2.0");
        
        assertThrows(IllegalArgumentException.class, () -> quantity.divide(negativeDivisor));
    }

    @Test
    void isZero_ZeroValue_ReturnsTrue() {
        Quantity quantity = Quantity.zero();
        
        assertTrue(quantity.isZero());
    }

    @Test
    void isZero_NonZeroValue_ReturnsFalse() {
        Quantity quantity = Quantity.of("0.01");
        
        assertFalse(quantity.isZero());
    }

    @Test
    void isPositive_PositiveValue_ReturnsTrue() {
        Quantity quantity = Quantity.of("0.01");
        
        assertTrue(quantity.isPositive());
    }

    @Test
    void isPositive_ZeroValue_ReturnsFalse() {
        Quantity quantity = Quantity.zero();
        
        assertFalse(quantity.isPositive());
    }

    @Test
    void isGreaterThan_GreaterValue_ReturnsTrue() {
        Quantity quantity1 = Quantity.of("10.00");
        Quantity quantity2 = Quantity.of("5.00");
        
        assertTrue(quantity1.isGreaterThan(quantity2));
        assertFalse(quantity2.isGreaterThan(quantity1));
    }

    @Test
    void isGreaterThan_NullQuantity_ReturnsTrue() {
        Quantity quantity = Quantity.of("10.00");
        
        assertTrue(quantity.isGreaterThan(null));
    }

    @Test
    void isLessThan_SmallerValue_ReturnsTrue() {
        Quantity quantity1 = Quantity.of("5.00");
        Quantity quantity2 = Quantity.of("10.00");
        
        assertTrue(quantity1.isLessThan(quantity2));
        assertFalse(quantity2.isLessThan(quantity1));
    }

    @Test
    void isLessThan_NullQuantity_ReturnsFalse() {
        Quantity quantity = Quantity.of("10.00");
        
        assertFalse(quantity.isLessThan(null));
    }

    @Test
    void isGreaterThanOrEqual_EqualValue_ReturnsTrue() {
        Quantity quantity1 = Quantity.of("10.00");
        Quantity quantity2 = Quantity.of("10.00");
        
        assertTrue(quantity1.isGreaterThanOrEqual(quantity2));
        assertTrue(quantity2.isGreaterThanOrEqual(quantity1));
    }

    @Test
    void isLessThanOrEqual_EqualValue_ReturnsTrue() {
        Quantity quantity1 = Quantity.of("10.00");
        Quantity quantity2 = Quantity.of("10.00");
        
        assertTrue(quantity1.isLessThanOrEqual(quantity2));
        assertTrue(quantity2.isLessThanOrEqual(quantity1));
    }

    @Test
    void isSufficient_SufficientQuantity_ReturnsTrue() {
        Quantity available = Quantity.of("10.00");
        Quantity required = Quantity.of("5.00");
        
        assertTrue(available.isSufficient(required));
    }

    @Test
    void isSufficient_InsufficientQuantity_ReturnsFalse() {
        Quantity available = Quantity.of("5.00");
        Quantity required = Quantity.of("10.00");
        
        assertFalse(available.isSufficient(required));
    }

    @Test
    void isSufficient_EqualQuantity_ReturnsTrue() {
        Quantity available = Quantity.of("10.00");
        Quantity required = Quantity.of("10.00");
        
        assertTrue(available.isSufficient(required));
    }

    @Test
    void isSufficient_NullQuantity_ReturnsTrue() {
        Quantity available = Quantity.of("10.00");
        
        assertTrue(available.isSufficient(null));
    }

    @Test
    void compareTo_DifferentValues_ReturnsCorrectComparison() {
        Quantity quantity1 = Quantity.of("10.00");
        Quantity quantity2 = Quantity.of("5.00");
        Quantity quantity3 = Quantity.of("10.00");
        
        assertEquals(1, quantity1.compareTo(quantity2));
        assertEquals(-1, quantity2.compareTo(quantity1));
        assertEquals(0, quantity1.compareTo(quantity3));
    }

    @Test
    void compareTo_NullQuantity_ThrowsException() {
        Quantity quantity = Quantity.of("10.00");
        
        assertThrows(IllegalArgumentException.class, () -> quantity.compareTo(null));
    }

    @Test
    void equals_SameValue_ReturnsTrue() {
        Quantity quantity1 = Quantity.of("10.00");
        Quantity quantity2 = Quantity.of("10.00");
        
        assertEquals(quantity1, quantity2);
    }

    @Test
    void equals_DifferentValue_ReturnsFalse() {
        Quantity quantity1 = Quantity.of("10.00");
        Quantity quantity2 = Quantity.of("5.00");
        
        assertNotEquals(quantity1, quantity2);
    }

    @Test
    void hashCode_SameValue_SameHashCode() {
        Quantity quantity1 = Quantity.of("10.00");
        Quantity quantity2 = Quantity.of("10.00");
        
        assertEquals(quantity1.hashCode(), quantity2.hashCode());
    }

    @Test
    void toDisplayString_FormatsCorrectly() {
        Quantity quantity = Quantity.of("12.34");
        
        assertEquals("12.34", quantity.toDisplayString());
    }

    @Test
    void toDisplayString_ZeroValue_FormatsCorrectly() {
        Quantity quantity = Quantity.zero();
        
        assertEquals("0.00", quantity.toDisplayString());
    }
}
