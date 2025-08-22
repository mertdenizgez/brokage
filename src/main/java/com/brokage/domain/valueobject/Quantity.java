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
public class Quantity {
    
    @Column(precision = 19, scale = 2, nullable = false)
    private BigDecimal value;
    
    // JPA requirement
    protected Quantity() {}
    
    public Quantity(BigDecimal value) {
        if (value == null) {
            throw new IllegalArgumentException("Quantity value cannot be null");
        }
        if (value.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Quantity cannot be negative");
        }
        this.value = value.setScale(2, RoundingMode.HALF_UP);
    }
    
    public static Quantity of(BigDecimal value) {
        return new Quantity(value);
    }
    
    public static Quantity of(String value) {
        return new Quantity(new BigDecimal(value));
    }
    
    public static Quantity of(double value) {
        return new Quantity(BigDecimal.valueOf(value));
    }
    
    public static Quantity of(int value) {
        return new Quantity(BigDecimal.valueOf(value));
    }
    
    public static Quantity zero() {
        return new Quantity(BigDecimal.ZERO);
    }
    
    public static Quantity one() {
        return new Quantity(BigDecimal.ONE);
    }
    
    public Quantity add(Quantity other) {
        if (other == null) {
            throw new IllegalArgumentException("Cannot add null quantity");
        }
        return new Quantity(this.value.add(other.value));
    }
    
    public Quantity subtract(Quantity other) {
        if (other == null) {
            throw new IllegalArgumentException("Cannot subtract null quantity");
        }
        if (this.value.compareTo(other.value) < 0) {
            throw new IllegalStateException("Insufficient quantity: cannot subtract " + other + " from " + this);
        }
        return new Quantity(this.value.subtract(other.value));
    }
    
    public Quantity multiply(BigDecimal factor) {
        if (factor == null) {
            throw new IllegalArgumentException("Multiplication factor cannot be null");
        }
        if (factor.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Multiplication factor cannot be negative");
        }
        return new Quantity(this.value.multiply(factor));
    }
    
    public Quantity divide(BigDecimal divisor) {
        if (divisor == null) {
            throw new IllegalArgumentException("Divisor cannot be null");
        }
        if (divisor.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Divisor must be positive");
        }
        return new Quantity(this.value.divide(divisor, 2, RoundingMode.HALF_UP));
    }
    
    public boolean isZero() {
        return value.compareTo(BigDecimal.ZERO) == 0;
    }
    
    public boolean isPositive() {
        return value.compareTo(BigDecimal.ZERO) > 0;
    }
    
    public boolean isGreaterThan(Quantity other) {
        if (other == null) {
            return true;
        }
        return value.compareTo(other.value) > 0;
    }
    
    public boolean isLessThan(Quantity other) {
        if (other == null) {
            return false;
        }
        return value.compareTo(other.value) < 0;
    }
    
    public boolean isGreaterThanOrEqual(Quantity other) {
        if (other == null) {
            return true;
        }
        return value.compareTo(other.value) >= 0;
    }
    
    public boolean isLessThanOrEqual(Quantity other) {
        if (other == null) {
            return false;
        }
        return value.compareTo(other.value) <= 0;
    }
    
    public boolean isSufficient(Quantity required) {
        if (required == null) {
            return true;
        }
        return value.compareTo(required.value) >= 0;
    }
    
    public int compareTo(Quantity other) {
        if (other == null) {
            throw new IllegalArgumentException("Cannot compare with null quantity");
        }
        return value.compareTo(other.value);
    }

    public String toDisplayString() {
        return String.format("%.2f", value);
    }
}
