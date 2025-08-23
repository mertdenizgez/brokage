package com.brokage.application.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "Login request")
public class LoginRequest {
    @NotBlank
    @Schema(description = "Username", example = "customer1", required = true)
    private String username;
    
    @NotBlank
    @Schema(description = "Password", example = "password123", required = true)
    private String password;
}
