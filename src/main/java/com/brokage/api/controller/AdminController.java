package com.brokage.api.controller;

import com.brokage.application.dto.response.OrderResponse;
import com.brokage.application.mapper.OrderMapper;
import com.brokage.application.service.order.FetchOrderService;
import com.brokage.application.service.order.MatchOrderService;
import com.brokage.domain.entity.Order;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Admin", description = "Administrative APIs (Admin access required)")
@SecurityRequirement(name = "bearerAuth")
public class AdminController {

    private final FetchOrderService fetchOrderService;
    private final MatchOrderService matchOrderService;
    private final OrderMapper orderMapper;

    @GetMapping("/orders/pending")
    @Operation(
            summary = "Get all pending orders",
            description = "Retrieve all pending orders across all customers (Admin only)"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Pending orders retrieved successfully",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = OrderResponse.class)))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Authentication required",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Access denied - Admin role required",
                    content = @Content
            )
    })
    public ResponseEntity<List<OrderResponse>> getPendingOrders() {
        List<Order> orders = fetchOrderService.getPendingOrders();
        List<OrderResponse> responses = orderMapper.toResponseList(orders);
        return ResponseEntity.ok(responses);
    }

    @PostMapping("/orders/{orderId}/match")
    @Operation(
            summary = "Match a pending order",
            description = "Execute a pending order by matching it and updating customer assets (Admin only)"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Order matched successfully"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Order cannot be matched (not pending) or order not found",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Authentication required",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Access denied - Admin role required",
                    content = @Content
            )
    })
    public ResponseEntity<Void> matchOrder(
            @Parameter(description = "Order ID to match", required = true) @PathVariable Long orderId) {
        matchOrderService.matchOrder(orderId);
        return ResponseEntity.ok().build();
    }
}
