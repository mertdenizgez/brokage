package com.brokage.domain.valueobject;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class MoneyTest {

    @Test
    void constructor_ValidAmount_Success() {
        BigDecimal amount = new BigDecimal("100.50");
        
        Money money = new Money(amount);
        
        assertEquals(new BigDecimal("100.50"), money.getAmount());
    }

    @Test
    void constructor_NullAmount_ThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> new Money(null));
    }

    @Test
    void constructor_NegativeAmount_ThrowsException() {
        BigDecimal negativeAmount = new BigDecimal("-10.00");
        
        assertThrows(IllegalArgumentException.class, () -> new Money(negativeAmount));
    }

    @Test
    void constructor_RoundsToTwoDecimals() {
        BigDecimal amount = new BigDecimal("100.123456");
        
        Money money = new Money(amount);
        
        assertEquals(new BigDecimal("100.12"), money.getAmount());
    }

    @Test
    void of_BigDecimal_Success() {
        BigDecimal amount = new BigDecimal("50.75");
        
        Money money = Money.of(amount);
        
        assertEquals(amount, money.getAmount());
    }

    @Test
    void of_String_Success() {
        Money money = Money.of("25.99");
        
        assertEquals(new BigDecimal("25.99"), money.getAmount());
    }

    @Test
    void of_Double_Success() {
        Money money = Money.of(15.50);
        
        assertEquals(new BigDecimal("15.50"), money.getAmount());
    }

    @Test
    void zero_ReturnsZeroMoney() {
        Money money = Money.zero();
        
        assertEquals(new BigDecimal("0.00"), money.getAmount());
        assertTrue(money.isZero());
    }

    @Test
    void add_ValidMoney_Success() {
        Money money1 = Money.of("100.00");
        Money money2 = Money.of("50.25");
        
        Money result = money1.add(money2);
        
        assertEquals(new BigDecimal("150.25"), result.getAmount());
        // Original objects unchanged
        assertEquals(new BigDecimal("100.00"), money1.getAmount());
        assertEquals(new BigDecimal("50.25"), money2.getAmount());
    }

    @Test
    void add_NullMoney_ThrowsException() {
        Money money = Money.of("100.00");
        
        assertThrows(IllegalArgumentException.class, () -> money.add(null));
    }

    @Test
    void subtract_ValidMoney_Success() {
        Money money1 = Money.of("100.00");
        Money money2 = Money.of("30.25");
        
        Money result = money1.subtract(money2);
        
        assertEquals(new BigDecimal("69.75"), result.getAmount());
    }

    @Test
    void subtract_InsufficientAmount_ThrowsException() {
        Money money1 = Money.of("50.00");
        Money money2 = Money.of("100.00");
        
        assertThrows(IllegalStateException.class, () -> money1.subtract(money2));
    }

    @Test
    void subtract_NullMoney_ThrowsException() {
        Money money = Money.of("100.00");
        
        assertThrows(IllegalArgumentException.class, () -> money.subtract(null));
    }

    @Test
    void multiply_BigDecimal_Success() {
        Money money = Money.of("10.00");
        BigDecimal factor = new BigDecimal("2.5");
        
        Money result = money.multiply(factor);
        
        assertEquals(new BigDecimal("25.00"), result.getAmount());
    }

    @Test
    void multiply_Quantity_Success() {
        Money price = Money.of("15.50");
        Quantity quantity = Quantity.of("3");
        
        Money total = price.multiply(quantity);
        
        assertEquals(new BigDecimal("46.50"), total.getAmount());
    }

    @Test
    void multiply_NullFactor_ThrowsException() {
        Money money = Money.of("100.00");
        
        assertThrows(IllegalArgumentException.class, () -> money.multiply((BigDecimal) null));
    }

    @Test
    void multiply_NegativeFactor_ThrowsException() {
        Money money = Money.of("100.00");
        BigDecimal negativeFactor = new BigDecimal("-2.0");
        
        assertThrows(IllegalArgumentException.class, () -> money.multiply(negativeFactor));
    }

    @Test
    void multiply_NullQuantity_ThrowsException() {
        Money money = Money.of("100.00");
        
        assertThrows(IllegalArgumentException.class, () -> money.multiply((Quantity) null));
    }

    @Test
    void divide_ValidDivisor_Success() {
        Money money = Money.of("100.00");
        BigDecimal divisor = new BigDecimal("4");
        
        Money result = money.divide(divisor);
        
        assertEquals(new BigDecimal("25.00"), result.getAmount());
    }

    @Test
    void divide_WithRounding_Success() {
        Money money = Money.of("100.00");
        BigDecimal divisor = new BigDecimal("3");
        
        Money result = money.divide(divisor);
        
        assertEquals(new BigDecimal("33.33"), result.getAmount());
    }

    @Test
    void divide_NullDivisor_ThrowsException() {
        Money money = Money.of("100.00");
        
        assertThrows(IllegalArgumentException.class, () -> money.divide(null));
    }

    @Test
    void divide_ZeroDivisor_ThrowsException() {
        Money money = Money.of("100.00");
        
        assertThrows(IllegalArgumentException.class, () -> money.divide(BigDecimal.ZERO));
    }

    @Test
    void divide_NegativeDivisor_ThrowsException() {
        Money money = Money.of("100.00");
        BigDecimal negativeDivisor = new BigDecimal("-2.0");
        
        assertThrows(IllegalArgumentException.class, () -> money.divide(negativeDivisor));
    }

    @Test
    void isZero_ZeroAmount_ReturnsTrue() {
        Money money = Money.zero();
        
        assertTrue(money.isZero());
    }

    @Test
    void isZero_NonZeroAmount_ReturnsFalse() {
        Money money = Money.of("0.01");
        
        assertFalse(money.isZero());
    }

    @Test
    void isPositive_PositiveAmount_ReturnsTrue() {
        Money money = Money.of("0.01");
        
        assertTrue(money.isPositive());
    }

    @Test
    void isPositive_ZeroAmount_ReturnsFalse() {
        Money money = Money.zero();
        
        assertFalse(money.isPositive());
    }

    @Test
    void isGreaterThan_GreaterAmount_ReturnsTrue() {
        Money money1 = Money.of("100.00");
        Money money2 = Money.of("50.00");
        
        assertTrue(money1.isGreaterThan(money2));
        assertFalse(money2.isGreaterThan(money1));
    }

    @Test
    void isGreaterThan_NullMoney_ReturnsTrue() {
        Money money = Money.of("100.00");
        
        assertTrue(money.isGreaterThan(null));
    }

    @Test
    void isLessThan_SmallerAmount_ReturnsTrue() {
        Money money1 = Money.of("50.00");
        Money money2 = Money.of("100.00");
        
        assertTrue(money1.isLessThan(money2));
        assertFalse(money2.isLessThan(money1));
    }

    @Test
    void isLessThan_NullMoney_ReturnsFalse() {
        Money money = Money.of("100.00");
        
        assertFalse(money.isLessThan(null));
    }

    @Test
    void isGreaterThanOrEqual_EqualAmount_ReturnsTrue() {
        Money money1 = Money.of("100.00");
        Money money2 = Money.of("100.00");
        
        assertTrue(money1.isGreaterThanOrEqual(money2));
        assertTrue(money2.isGreaterThanOrEqual(money1));
    }

    @Test
    void isLessThanOrEqual_EqualAmount_ReturnsTrue() {
        Money money1 = Money.of("100.00");
        Money money2 = Money.of("100.00");
        
        assertTrue(money1.isLessThanOrEqual(money2));
        assertTrue(money2.isLessThanOrEqual(money1));
    }

    @Test
    void compareTo_DifferentAmounts_ReturnsCorrectComparison() {
        Money money1 = Money.of("100.00");
        Money money2 = Money.of("50.00");
        Money money3 = Money.of("100.00");
        
        assertEquals(1, money1.compareTo(money2));
        assertEquals(-1, money2.compareTo(money1));
        assertEquals(0, money1.compareTo(money3));
    }

    @Test
    void compareTo_NullMoney_ThrowsException() {
        Money money = Money.of("100.00");
        
        assertThrows(IllegalArgumentException.class, () -> money.compareTo(null));
    }

    @Test
    void equals_SameAmount_ReturnsTrue() {
        Money money1 = Money.of("100.00");
        Money money2 = Money.of("100.00");
        
        assertEquals(money1, money2);
    }

    @Test
    void equals_DifferentAmount_ReturnsFalse() {
        Money money1 = Money.of("100.00");
        Money money2 = Money.of("50.00");
        
        assertNotEquals(money1, money2);
    }

    @Test
    void hashCode_SameAmount_SameHashCode() {
        Money money1 = Money.of("100.00");
        Money money2 = Money.of("100.00");
        
        assertEquals(money1.hashCode(), money2.hashCode());
    }

    @Test
    void toDisplayString_FormatsCorrectly() {
        Money money = Money.of("123.45");
        
        assertEquals("123.45", money.toDisplayString());
    }

    @Test
    void toDisplayString_ZeroAmount_FormatsCorrectly() {
        Money money = Money.zero();
        
        assertEquals("0.00", money.toDisplayString());
    }
}
