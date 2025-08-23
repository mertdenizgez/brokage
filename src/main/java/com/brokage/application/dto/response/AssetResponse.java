package com.brokage.application.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Schema(description = "Asset response")
public class AssetResponse {
    @Schema(description = "Asset ID", example = "1")
    private Long id;
    
    @Schema(description = "Customer ID", example = "1")
    private Long customerId;
    
    @Schema(description = "Asset name", example = "TRY")
    private String assetName;
    
    @Schema(description = "Total asset size", example = "10000.00")
    private BigDecimal size;
    
    @Schema(description = "Usable asset size (not reserved for pending orders)", example = "9500.00")
    private BigDecimal usableSize;
}
