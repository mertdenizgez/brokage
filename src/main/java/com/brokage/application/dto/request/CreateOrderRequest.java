package com.brokage.application.dto.request;

import com.brokage.domain.enums.OrderSide;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Schema(description = "Create order request")
public class CreateOrderRequest {
    @NotNull
    @Schema(description = "Customer ID", example = "1", required = true)
    private Long customerId;

    @NotBlank
    @Schema(description = "Asset name (stock symbol)", example = "AAPL", required = true)
    private String assetName;

    @NotNull
    @Schema(description = "Order side", example = "BUY", required = true)
    private OrderSide orderSide;

    @NotNull
    @DecimalMin(value = "0.01", message = "Size must be greater than 0")
    @Schema(description = "Number of shares", example = "10", required = true)
    private BigDecimal size;

    @NotNull
    @DecimalMin(value = "0.01", message = "Price must be greater than 0")
    @Schema(description = "Price per share", example = "150.00", required = true)
    private BigDecimal price;
}
