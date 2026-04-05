package com.smd.core.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoleRequest {
    @NotNull(message = "roleName is required")
    @NotBlank(message = "roleName cannot be empty")
    @Size(min = 1, max = 255, message = "roleName must be between 1 and 255 characters")
    private String roleName;
}
