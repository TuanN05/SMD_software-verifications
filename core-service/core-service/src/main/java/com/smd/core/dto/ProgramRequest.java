package com.smd.core.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProgramRequest {
    
    @NotBlank(message = "Program name is required")
    @Schema(description = "Name of the program", example = "Kỹ thuật Phần mềm")
    private String programName;
    
    @NotNull(message = "Department is required")
    @Schema(description = "Department object containing departmentId")
    private DepartmentInput department;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DepartmentInput {
        @NotNull(message = "Department ID is required")
        @Schema(description = "ID of the department", example = "1")
        private Long departmentId;
    }
}