package com.smd.core.controller;

import com.smd.core.dto.CourseRelationshipDto;
import com.smd.core.dto.CourseTreeNodeDto;
import com.smd.core.service.CourseRelationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/course-relations")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Course Relationship", description = "APIs for managing course relationships (Prerequisites, Corequisites, Equivalents)")
public class CourseRelationController {
    
    private final CourseRelationService relationService;
    
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'ACADEMIC_AFFAIRS', 'HEAD_OF_DEPARTMENT')")
    @Operation(summary = "Create a new course relationship",
               description = "Create a relationship between two courses such as prerequisite, corequisite or equivalent")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Successfully created"),
        @ApiResponse(responseCode = "400", description = "Invalid data or circular dependency detected"),
        @ApiResponse(responseCode = "404", description = "Course not found")
    })
    public ResponseEntity<CourseRelationshipDto> createRelationship(
            @jakarta.validation.Valid @RequestBody CourseRelationshipDto dto) {
        log.info("REST request to create course relationship");
        CourseRelationshipDto result = relationService.createRelationship(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }
    
    @GetMapping("/{courseId}")
    @Operation(summary = "Get all relationships of a course",
               description = "Returns the list of related courses")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Successfully retrieved"),
        @ApiResponse(responseCode = "404", description = "Course not found")
    })
    public ResponseEntity<List<CourseRelationshipDto>> getCourseRelationships(
            @Parameter(description = "Course ID")
            @PathVariable Long courseId) {
        log.info("REST request to get relationships for course: {}", courseId);
        List<CourseRelationshipDto> relationships = relationService.getCourseRelationships(courseId);
        return ResponseEntity.ok(relationships);
    }
    
    @GetMapping("/{courseId}/dependency-tree")
    @Operation(summary = "Get course dependency tree",
               description = "Returns the complete tree structure including prerequisites, corequisites and equivalents")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Successfully retrieved"),
        @ApiResponse(responseCode = "404", description = "Course not found")
    })
    public ResponseEntity<CourseTreeNodeDto> getCourseTree(
            @Parameter(description = "Course ID")
            @PathVariable Long courseId) {
        log.info("REST request to get course tree for: {}", courseId);
        CourseTreeNodeDto tree = relationService.getCourseTree(courseId);
        return ResponseEntity.ok(tree);
    }
    
    @GetMapping("/available-prerequisites/{courseId}")
    @Operation(summary = "Get available courses that can be added as prerequisites",
               description = "Returns valid courses that won't create circular dependencies")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Successfully retrieved"),
        @ApiResponse(responseCode = "404", description = "Course not found")
    })
    public ResponseEntity<List<CourseRelationshipDto>> getAvailablePrerequisites(
            @Parameter(description = "Course ID")
            @PathVariable Long courseId) {
        log.info("REST request to get available prerequisites for: {}", courseId);
        List<CourseRelationshipDto> available = relationService.getAvailablePrerequisites(courseId);
        return ResponseEntity.ok(available);
    }
    
    @GetMapping("/check-circular")
    @Operation(summary = "Check if adding a relationship would create a circular dependency",
               description = "Returns true if circular dependency detected, false if valid")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Successfully checked")
    })
    public ResponseEntity<Map<String, Boolean>> checkCircularDependency(
            @Parameter(description = "Main course ID")
            @RequestParam Long courseId,
            @Parameter(description = "Related course ID")
            @RequestParam Long relatedCourseId) {
        log.info("REST request to check circular dependency: {} -> {}", courseId, relatedCourseId);
        boolean hasCircular = relationService.hasCircularDependency(courseId, relatedCourseId);
        return ResponseEntity.ok(Map.of("hasCircularDependency", hasCircular));
    }
    
    @GetMapping("/statistics/department/{departmentId}")
    @Operation(summary = "Get relationship statistics for a department",
               description = "Returns the count and classification of relationships")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Successfully retrieved")
    })
    public ResponseEntity<Map<String, Object>> getStatistics(
            @Parameter(description = "Department ID")
            @PathVariable Long departmentId) {
        log.info("REST request to get relationship statistics for department: {}", departmentId);
        Map<String, Object> stats = relationService.getRelationshipStatistics(departmentId);
        return ResponseEntity.ok(stats);
    }
    
    @DeleteMapping("/{relationshipId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'ACADEMIC_AFFAIRS', 'HEAD_OF_DEPARTMENT')")
    @Operation(summary = "Delete a course relationship",
               description = "Remove the relationship between two courses")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Successfully deleted"),
        @ApiResponse(responseCode = "404", description = "Relationship not found")
    })
    public ResponseEntity<Void> deleteRelationship(
            @Parameter(description = "Relationship ID")
            @PathVariable Long relationshipId) {
        log.info("REST request to delete course relationship: {}", relationshipId);
        relationService.deleteRelationship(relationshipId);
        return ResponseEntity.noContent().build();
    }
    
    // ===== EXCEPTION HANDLERS =====
    
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Map<String, Object>> handleTypeMismatchException(MethodArgumentTypeMismatchException e) {
        String paramName = e.getName();
        String invalidValue = e.getValue() != null ? e.getValue().toString() : "null";
        String requiredType = e.getRequiredType() != null ? e.getRequiredType().getSimpleName() : "Unknown";
        
        log.warn("Invalid parameter type: {} = {} (expected {})", paramName, invalidValue, requiredType);
        
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("timestamp", java.time.LocalDateTime.now());
        errorResponse.put("status", 400);
        errorResponse.put("error", "Bad Request");
        errorResponse.put("message", paramName + " must be a valid " + requiredType);
        errorResponse.put("invalidValue", invalidValue);
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }
}
