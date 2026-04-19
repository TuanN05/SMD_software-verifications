package com.smd.core.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DepartmentRequest {
    
    @NotBlank(message = "Department name is required and cannot be empty")
    @Schema(description = "Department name", example = "Computer Science")
    private String deptName;
}
