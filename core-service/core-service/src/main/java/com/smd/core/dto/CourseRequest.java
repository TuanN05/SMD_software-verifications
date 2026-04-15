package com.smd.core.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
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
public class CourseRequest {
    
    @NotBlank(message = "Course code is required")
    @Size(max = 50, message = "Mã môn học không được vượt quá 50 ký tự")
    @Schema(description = "Course code - must be unique", example = "CS101")
    private String courseCode;
    
    @NotBlank(message = "Course name is required")
    @Size(max = 255, message = "Tên môn học không được vượt quá 255 ký tự")
    @Schema(description = "Course name", example = "Introduction to Computer Science")
    private String courseName;
    
    @NotNull(message = "Credits is required")
    @Positive(message = "Credits must be greater than 0")
    @Schema(description = "Credit hours", example = "3")
    private Integer credits;
    
    @Schema(description = "Course type: BAT_BUOC (Mandatory) or TU_CHON (Elective)", 
            example = "BAT_BUOC", 
            defaultValue = "BAT_BUOC")
    private String courseType;
    
    @Valid
    @NotNull(message = "Department is required")
    @Schema(description = "Department information containing departmentId")
    private DepartmentRequest department;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DepartmentRequest {
        
        @NotNull(message = "Department ID is required")
        @Positive(message = "Department ID must be greater than 0")
        @Schema(description = "ID of the department", example = "1")
        private Long departmentId;
    }
}
