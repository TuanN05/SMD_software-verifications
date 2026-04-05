package com.smd.core.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CourseRelationshipDto {
    private Long relationId;
    
    @NotNull(message = "courseId is required")
    @Positive(message = "courseId must be positive")
    private Long courseId;
    
    private String courseCode;
    private String courseName;
    
    @NotNull(message = "relatedCourseId is required")
    @Positive(message = "relatedCourseId must be positive")
    private Long relatedCourseId;
    
    private String relatedCourseCode;
    private String relatedCourseName;
    
    @NotNull(message = "relationType is required")
    @NotBlank(message = "relationType cannot be empty")
    private String relationType;
}
