package com.smd.core.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SessionPlanRequest {
    
    @NotNull(message = "Syllabus ID is required")
    private Long syllabusId;
    
    @NotNull(message = "Week number is required")
    @Positive(message = "Week number must be greater than 0")
    private Integer weekNo;
    
    @NotBlank(message = "Topic is required")
    @Size(max = 255, message = "Topic length exceeds 255 characters")
    private String topic;
    
    private String teachingMethod;
}
