package com.brokage.domain.valueobject;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Getter
@Embeddable
@EqualsAndHashCode
@ToString
public class Money {
    
    @Column(precision = 19, scale = 2, nullable = false)
    private BigDecimal amount;
    
    // JPA requirement
    protected Money() {}
    
    public Money(BigDecimal amount) {
        if (amount == null) {
            throw new IllegalArgumentException("Money amount cannot be null");
        }
        if (amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Money amount cannot be negative");
        }
        this.amount = amount.setScale(2, RoundingMode.HALF_UP);
    }
    
    public static Money of(BigDecimal amount) {
        return new Money(amount);
    }
    
    public static Money of(String amount) {
        return new Money(new BigDecimal(amount));
    }
    
    public static Money of(double amount) {
        return new Money(BigDecimal.valueOf(amount));
    }
    
    public static Money zero() {
        return new Money(BigDecimal.ZERO);
    }
    
    public Money add(Money other) {
        if (other == null) {
            throw new IllegalArgumentException("Cannot add null money");
        }
        return new Money(this.amount.add(other.amount));
    }
    
    public Money subtract(Money other) {
        if (other == null) {
            throw new IllegalArgumentException("Cannot subtract null money");
        }
        if (this.amount.compareTo(other.amount) < 0) {
            throw new IllegalStateException("Insufficient money: cannot subtract " + other + " from " + this);
        }
        return new Money(this.amount.subtract(other.amount));
    }
    
    public Money multiply(BigDecimal factor) {
        if (factor == null) {
            throw new IllegalArgumentException("Multiplication factor cannot be null");
        }
        if (factor.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Multiplication factor cannot be negative");
        }
        return new Money(this.amount.multiply(factor));
    }
    
    public Money multiply(Quantity quantity) {
        if (quantity == null) {
            throw new IllegalArgumentException("Quantity cannot be null");
        }
        return multiply(quantity.getValue());
    }
    
    public Money divide(BigDecimal divisor) {
        if (divisor == null) {
            throw new IllegalArgumentException("Divisor cannot be null");
        }
        if (divisor.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Divisor must be positive");
        }
        return new Money(this.amount.divide(divisor, 2, RoundingMode.HALF_UP));
    }
    
    public boolean isZero() {
        return amount.compareTo(BigDecimal.ZERO) == 0;
    }
    
    public boolean isPositive() {
        return amount.compareTo(BigDecimal.ZERO) > 0;
    }
    
    public boolean isGreaterThan(Money other) {
        if (other == null) {
            return true;
        }
        return amount.compareTo(other.amount) > 0;
    }
    
    public boolean isLessThan(Money other) {
        if (other == null) {
            return false;
        }
        return amount.compareTo(other.amount) < 0;
    }
    
    public boolean isGreaterThanOrEqual(Money other) {
        if (other == null) {
            return true;
        }
        return amount.compareTo(other.amount) >= 0;
    }
    
    public boolean isLessThanOrEqual(Money other) {
        if (other == null) {
            return false;
        }
        return amount.compareTo(other.amount) <= 0;
    }
    
    public int compareTo(Money other) {
        if (other == null) {
            throw new IllegalArgumentException("Cannot compare with null money");
        }
        return amount.compareTo(other.amount);
    }

    public String toDisplayString() {
        return String.format("%.2f", amount);
    }
}
