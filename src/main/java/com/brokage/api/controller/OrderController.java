package com.brokage.api.controller;

import com.brokage.application.dto.request.CreateOrderRequest;
import com.brokage.application.dto.response.OrderResponse;
import com.brokage.application.mapper.OrderMapper;
import com.brokage.application.service.order.CancelOrderService;
import com.brokage.application.service.order.CreateOrderService;
import com.brokage.application.service.order.FetchOrderService;
import com.brokage.domain.entity.Order;
import com.brokage.infrastructure.security.annotation.ValidateCustomerAccess;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@Tag(name = "Orders", description = "Order management APIs")
@SecurityRequirement(name = "bearerAuth")
public class OrderController {

    private final CreateOrderService createOrderService;
    private final FetchOrderService fetchOrderService;
    private final CancelOrderService cancelOrderService;
    private final OrderMapper orderMapper;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or (#request.customerId == authentication.principal.id)")
    @Operation(
            summary = "Create a new order",
            description = "Create a new stock order for a customer. Requires sufficient balance for the order."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Order created successfully",
                    content = @Content(schema = @Schema(implementation = OrderResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid order data or insufficient balance",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Authentication required",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Access denied - can only create orders for own customer ID",
                    content = @Content
            )
    })
    public ResponseEntity<OrderResponse> createOrder(@Valid @RequestBody CreateOrderRequest request) {
        Order order = createOrderService.createOrder(request);
        OrderResponse response = orderMapper.toResponse(order);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    @ValidateCustomerAccess
    @Operation(
            summary = "Get customer orders",
            description = "Retrieve orders for a customer, optionally filtered by date range"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Orders retrieved successfully",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = OrderResponse.class)))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Authentication required",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Access denied - can only access own orders",
                    content = @Content
            )
    })
    public ResponseEntity<List<OrderResponse>> getOrders(
            @Parameter(description = "Customer ID", required = true) @RequestParam Long customerId,
            @Parameter(description = "Start date for filtering (YYYY-MM-DD)") @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @Parameter(description = "End date for filtering (YYYY-MM-DD)") @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        List<Order> orders;
        if (startDate != null && endDate != null) {
            LocalDateTime start = startDate.atStartOfDay();
            LocalDateTime end = endDate.atTime(LocalTime.MAX);
            orders = fetchOrderService.getCustomerOrdersByDateRange(customerId, start, end);
        } else {
            orders = fetchOrderService.getCustomerOrders(customerId);
        }

        List<OrderResponse> responses = orderMapper.toResponseList(orders);
        return ResponseEntity.ok(responses);
    }

    @DeleteMapping("/{orderId}")
    @ValidateCustomerAccess
    @Operation(
            summary = "Cancel an order",
            description = "Cancel a pending order. Only pending orders can be canceled."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Order canceled successfully"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Order cannot be canceled (not pending) or order not found",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Authentication required",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Access denied - can only cancel own orders",
                    content = @Content
            )
    })
    public ResponseEntity<Void> cancelOrder(
            @Parameter(description = "Order ID to cancel", required = true) @PathVariable Long orderId,
            @Parameter(description = "Customer ID", required = true) @RequestParam Long customerId) {

        cancelOrderService.cancelOrder(orderId, customerId);
        return ResponseEntity.noContent().build();
    }
}
