package com.brokage.domain.valueobject;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.util.Set;
import java.util.regex.Pattern;

@Getter
@Embeddable
@EqualsAndHashCode
@ToString
public class AssetSymbol {
    
    private static final Pattern VALID_SYMBOL_PATTERN = Pattern.compile("^[A-Z]{2,10}$");
    private static final Set<String> CURRENCY_SYMBOLS = Set.of("TRY");
    
    @Column(name = "asset_name", nullable = false)
    private String symbol;
    
    // JPA requirement
    protected AssetSymbol() {}
    
    public AssetSymbol(String symbol) {
        if (symbol == null || symbol.trim().isEmpty()) {
            throw new IllegalArgumentException("Asset symbol cannot be null or empty");
        }
        
        String normalizedSymbol = symbol.trim().toUpperCase();
        
        if (!VALID_SYMBOL_PATTERN.matcher(normalizedSymbol).matches()) {
            throw new IllegalArgumentException("Asset symbol must be 2-10 uppercase letters: " + symbol);
        }
        
        this.symbol = normalizedSymbol;
    }
    
    public static AssetSymbol of(String symbol) {
        return new AssetSymbol(symbol);
    }
    
    public static AssetSymbol trySymbol() {
        return new AssetSymbol("TRY");
    }
    
    public boolean isCurrency() {
        return CURRENCY_SYMBOLS.contains(symbol);
    }
    
    public boolean isStock() {
        return !isCurrency();
    }
    
    public AssetType getType() {
        return isCurrency() ? AssetType.CURRENCY : AssetType.STOCK;
    }

    public String getDisplayName() {
        if (isCurrency()) {
            return switch (symbol) {
                case "TRY" -> "Turkish Lira";
                case "USD" -> "US Dollar";
                case "EUR" -> "Euro";
                case "GBP" -> "British Pound";
                case "JPY" -> "Japanese Yen";
                default -> symbol;
            };
        }
        return symbol; // For stocks, return the symbol as-is
    }
    
    public enum AssetType {
        CURRENCY,
        STOCK
    }
}
