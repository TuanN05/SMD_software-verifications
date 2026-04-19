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
public class MaterialRequest {
    
    @NotNull(message = "Syllabus ID is required")
    private Long syllabusId;
    
    @NotBlank(message = "Title is required")
    @Size(max = 255, message = "Title length exceeds 255 characters")
    private String title;
    
    private String author;
    
    @NotBlank(message = "Material type is required")
    private String materialType;
}
