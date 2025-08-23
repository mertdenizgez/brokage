package com.brokage.api.controller;

import com.brokage.application.dto.response.AssetResponse;
import com.brokage.application.mapper.AssetMapper;
import com.brokage.application.service.asset.FetchAssetService;
import com.brokage.domain.entity.Asset;
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
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/assets")
@RequiredArgsConstructor
@Tag(name = "Assets", description = "Asset management APIs")
@SecurityRequirement(name = "bearerAuth")
public class AssetController {

    private final FetchAssetService fetchAssetService;
    private final AssetMapper assetMapper;

    @GetMapping
    @ValidateCustomerAccess
    @Operation(
            summary = "Get customer assets",
            description = "Retrieve all assets belonging to a customer including TRY and stock holdings"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Assets retrieved successfully",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = AssetResponse.class)))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Authentication required",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Access denied - can only access own assets",
                    content = @Content
            )
    })
    public ResponseEntity<List<AssetResponse>> getAssets(
            @Parameter(description = "Customer ID", required = true) @RequestParam Long customerId) {

        List<Asset> assets = fetchAssetService.getCustomerAssets(customerId);
        List<AssetResponse> responses = assetMapper.toResponseList(assets);
        return ResponseEntity.ok(responses);
    }
}
