package com.smd.core.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CLORequest {
    
    @NotNull(message = "Syllabus ID is required")
    private Long syllabusId;
    
    @NotBlank(message = "CLO code is required")
    @Size(max = 255, message = "CLO code không được vượt quá 255 ký tự")
    private String cloCode;
    
    @NotBlank(message = "CLO description is required")
    @Size(max = 1000, message = "CLO description không được vượt quá 1000 ký tự")
    private String cloDescription;
}
