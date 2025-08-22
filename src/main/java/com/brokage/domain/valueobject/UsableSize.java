package com.brokage.domain.valueobject;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.math.BigDecimal;

@Getter
@Embeddable
@EqualsAndHashCode
@ToString
public class UsableSize {
    
    @Column(name = "usable_size", precision = 19, scale = 2, nullable = false)
    private BigDecimal value;
    
    // JPA requirement
    protected UsableSize() {}
    
    public UsableSize(BigDecimal value) {
        if (value == null) {
            throw new IllegalArgumentException("Usable size value cannot be null");
        }
        if (value.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Usable size cannot be negative");
        }
        this.value = value;
    }
    
    public static UsableSize of(BigDecimal value) {
        return new UsableSize(value);
    }
    
    public static UsableSize zero() {
        return new UsableSize(BigDecimal.ZERO);
    }
    
    public UsableSize reserve(BigDecimal amount) {
        if (amount == null) {
            throw new IllegalArgumentException("Reserve amount cannot be null");
        }
        if (amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Reserve amount cannot be negative");
        }
        if (value.compareTo(amount) < 0) {
            throw new IllegalStateException("Insufficient usable size");
        }
        return new UsableSize(value.subtract(amount));
    }
    
    public UsableSize release(BigDecimal amount) {
        if (amount == null) {
            throw new IllegalArgumentException("Release amount cannot be null");
        }
        if (amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Release amount cannot be negative");
        }
        return new UsableSize(value.add(amount));
    }
    
    public UsableSize add(BigDecimal amount) {
        if (amount == null) {
            throw new IllegalArgumentException("Add amount cannot be null");
        }
        if (amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Add amount cannot be negative");
        }
        return new UsableSize(value.add(amount));
    }
    
    public boolean canReserve(BigDecimal amount) {
        if (amount == null) {
            return false;
        }
        return value.compareTo(amount) >= 0;
    }
    
    public boolean isZero() {
        return value.compareTo(BigDecimal.ZERO) == 0;
    }
    
    public boolean isGreaterThan(BigDecimal amount) {
        if (amount == null) {
            return true;
        }
        return value.compareTo(amount) > 0;
    }
    
    public boolean isLessThan(BigDecimal amount) {
        if (amount == null) {
            return false;
        }
        return value.compareTo(amount) < 0;
    }
    
    public int compareTo(BigDecimal amount) {
        if (amount == null) {
            throw new IllegalArgumentException("Comparison amount cannot be null");
        }
        return value.compareTo(amount);
    }

}
