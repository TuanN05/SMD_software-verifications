package com.smd.core.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateVersionRequest {
    
    @NotNull(message = "Source syllabus ID is required")
    private Long sourceSyllabusId;
    
    @NotBlank(message = "Version notes cannot be empty")
    @Size(min = 1, max = 1000, message = "Version notes must be between 1 and 1000 characters")
    private String versionNotes;
    
    private Boolean copyMaterials = true;
    
    private Boolean copySessionPlans = true;
    
    private Boolean copyAssessments = true;
    
    private Boolean copyCLOs = true;
}
