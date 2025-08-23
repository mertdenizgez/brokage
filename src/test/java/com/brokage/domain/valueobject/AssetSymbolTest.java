package com.brokage.domain.valueobject;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AssetSymbolTest {

    @Test
    void constructor_ValidSymbol_Success() {
        String symbol = "AAPL";

        AssetSymbol assetSymbol = new AssetSymbol(symbol);

        assertEquals("AAPL", assetSymbol.getSymbol());
    }

    @Test
    void constructor_LowercaseSymbol_ConvertsToUppercase() {
        String symbol = "aapl";

        AssetSymbol assetSymbol = new AssetSymbol(symbol);

        assertEquals("AAPL", assetSymbol.getSymbol());
    }

    @Test
    void constructor_SymbolWithSpaces_TrimsAndConverts() {
        String symbol = "  googl  ";

        AssetSymbol assetSymbol = new AssetSymbol(symbol);

        assertEquals("GOOGL", assetSymbol.getSymbol());
    }

    @Test
    void constructor_NullSymbol_ThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> new AssetSymbol(null));
    }

    @Test
    void constructor_EmptySymbol_ThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> new AssetSymbol(""));
        assertThrows(IllegalArgumentException.class, () -> new AssetSymbol("   "));
    }

    @Test
    void constructor_TooShortSymbol_ThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> new AssetSymbol("A"));
    }

    @Test
    void constructor_TooLongSymbol_ThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> new AssetSymbol("VERYLONGSYMBOL"));
    }

    @Test
    void constructor_InvalidCharacters_ThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> new AssetSymbol("AAP1"));
        assertThrows(IllegalArgumentException.class, () -> new AssetSymbol("AA-PL"));
        assertThrows(IllegalArgumentException.class, () -> new AssetSymbol("AA.PL"));
    }

    @Test
    void of_ValidSymbol_Success() {
        AssetSymbol assetSymbol = AssetSymbol.of("MSFT");

        assertEquals("MSFT", assetSymbol.getSymbol());
    }

    @Test
    void trySymbol_ReturnsTryAsset() {
        AssetSymbol trySymbol = AssetSymbol.trySymbol();

        assertEquals("TRY", trySymbol.getSymbol());
        assertTrue(trySymbol.isCurrency());
    }

    @Test
    void isCurrency_TrySymbol_ReturnsTrue() {
        AssetSymbol trySymbol = new AssetSymbol("TRY");

        assertTrue(trySymbol.isCurrency());
    }

    @Test
    void isCurrency_StockSymbol_ReturnsFalse() {
        AssetSymbol stockSymbol = new AssetSymbol("AAPL");

        assertFalse(stockSymbol.isCurrency());
    }

    @Test
    void isStock_StockSymbol_ReturnsTrue() {
        AssetSymbol stockSymbol = new AssetSymbol("AAPL");

        assertTrue(stockSymbol.isStock());
    }

    @Test
    void isStock_CurrencySymbol_ReturnsFalse() {
        AssetSymbol currencySymbol = new AssetSymbol("TRY");

        assertFalse(currencySymbol.isStock());
    }

    @Test
    void getType_CurrencySymbol_ReturnsCurrency() {
        AssetSymbol currencySymbol = new AssetSymbol("TRY");

        assertEquals(AssetSymbol.AssetType.CURRENCY, currencySymbol.getType());
    }

    @Test
    void getType_StockSymbol_ReturnsStock() {
        AssetSymbol stockSymbol = new AssetSymbol("AAPL");

        assertEquals(AssetSymbol.AssetType.STOCK, stockSymbol.getType());
    }

    @Test
    void getDisplayName_TrySymbol_ReturnsFullName() {
        AssetSymbol trySymbol = new AssetSymbol("TRY");

        assertEquals("Turkish Lira", trySymbol.getDisplayName());
    }

    @Test
    void getDisplayName_StockSymbol_ReturnsSymbol() {
        AssetSymbol stockSymbol = new AssetSymbol("AAPL");

        assertEquals("AAPL", stockSymbol.getDisplayName());
    }

    @Test
    void getDisplayName_UnknownCurrency_ReturnsSymbol() {
        AssetSymbol unknownSymbol = new AssetSymbol("XYZ");

        assertEquals("XYZ", unknownSymbol.getDisplayName());
    }

    @Test
    void equals_SameSymbol_ReturnsTrue() {
        AssetSymbol symbol1 = new AssetSymbol("AAPL");
        AssetSymbol symbol2 = new AssetSymbol("AAPL");

        assertEquals(symbol1, symbol2);
    }

    @Test
    void equals_DifferentSymbol_ReturnsFalse() {
        AssetSymbol symbol1 = new AssetSymbol("AAPL");
        AssetSymbol symbol2 = new AssetSymbol("GOOGL");

        assertNotEquals(symbol1, symbol2);
    }

    @Test
    void equals_SameSymbolDifferentCase_ReturnsTrue() {
        AssetSymbol symbol1 = new AssetSymbol("AAPL");
        AssetSymbol symbol2 = new AssetSymbol("aapl");

        assertEquals(symbol1, symbol2);
    }

    @Test
    void hashCode_SameSymbol_SameHashCode() {
        AssetSymbol symbol1 = new AssetSymbol("AAPL");
        AssetSymbol symbol2 = new AssetSymbol("AAPL");

        assertEquals(symbol1.hashCode(), symbol2.hashCode());
    }

    @Test
    void toString_ContainsSymbol() {
        AssetSymbol assetSymbol = new AssetSymbol("AAPL");
        String toString = assetSymbol.toString();

        assertTrue(toString.contains("AAPL"));
    }

    @Test
    void constructor_MinimumLengthSymbol_Success() {
        AssetSymbol assetSymbol = new AssetSymbol("AB");

        assertEquals("AB", assetSymbol.getSymbol());
    }

    @Test
    void constructor_MaximumLengthSymbol_Success() {
        AssetSymbol assetSymbol = new AssetSymbol("ABCDEFGHIJ");

        assertEquals("ABCDEFGHIJ", assetSymbol.getSymbol());
    }

    @Test
    void constructor_MixedCaseSymbol_ConvertsToUppercase() {
        AssetSymbol assetSymbol = new AssetSymbol("aapl");

        assertEquals("AAPL", assetSymbol.getSymbol());
    }
}
