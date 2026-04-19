package com.smd.core.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateSyllabusRequest {
    
    @NotNull(message = "Course ID is required")
    private Long courseId;
    
    @NotNull(message = "Lecturer ID is required")
    private Long lecturerId;
    
    @NotNull(message = "Academic year is required")
    @Pattern(regexp = "\\d{4}-\\d{4}", message = "Academic year must be in format YYYY-YYYY (e.g., 2024-2025)")
    private String academicYear;
    
    private Long programId;
    
    @NotBlank(message = "Version notes cannot be empty")
    @Size(min = 1, max = 1000, message = "Version notes must be between 1 and 1000 characters")
    private String versionNotes;
    
    @Size(min = 0, max = 5000, message = "Description must not exceed 5000 characters")
    private String description;
    
    // Optional: ID of previous version to copy content from
    private Long copyFromVersionId;
}
