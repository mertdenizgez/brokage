package com.brokage.application.dto.response;

import com.brokage.domain.enums.OrderSide;
import com.brokage.domain.enums.OrderStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Schema(description = "Order response")
public class OrderResponse {
    @Schema(description = "Order ID", example = "1")
    private Long id;
    
    @Schema(description = "Customer ID", example = "1")
    private Long customerId;
    
    @Schema(description = "Asset name", example = "AAPL")
    private String assetName;
    
    @Schema(description = "Order side", example = "BUY")
    private OrderSide orderSide;
    
    @Schema(description = "Number of shares", example = "10")
    private BigDecimal size;
    
    @Schema(description = "Price per share", example = "150.00")
    private BigDecimal price;
    
    @Schema(description = "Order status", example = "PENDING")
    private OrderStatus status;
    
    @Schema(description = "Order creation date", example = "2023-12-01T10:30:00")
    private LocalDateTime createdDate;
}
