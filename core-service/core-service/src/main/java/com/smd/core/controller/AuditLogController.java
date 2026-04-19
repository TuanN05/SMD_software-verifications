package com.smd.core.controller;

import com.smd.core.dto.AuditLogResponse;
import com.smd.core.dto.ResponseWrapper;
import com.smd.core.entity.SyllabusAuditLog;
import com.smd.core.service.AuditLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.constraints.Positive;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Controller for audit log management - Admin only
 * Provides comprehensive audit trail monitoring for administrators
 */
@RestController
@RequestMapping("/api/audit-logs")
@Tag(name = "Audit Log Management", description = "APIs for monitoring and retrieving audit logs (Admin only)")
@SecurityRequirement(name = "bearerAuth")
@Validated
@Slf4j
public class AuditLogController {
    
    @Autowired
    private AuditLogService auditLogService;
    
    /**
     * Get all audit logs with pagination (Admin only)
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
        summary = "Get all audit logs with pagination",
        description = "Retrieve all audit logs in the system with pagination support. Admin only."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Audit logs retrieved successfully",
            content = @Content(schema = @Schema(implementation = ResponseWrapper.class))
        ),
        @ApiResponse(responseCode = "403", description = "Access denied - Admin role required"),
        @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<ResponseWrapper<Map<String, Object>>> getAllAuditLogs(
            @Parameter(description = "Page number (0-indexed)") 
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") 
            @RequestParam(defaultValue = "50") int size,
            @Parameter(description = "Sort by field (default: timestamp)") 
            @RequestParam(defaultValue = "timestamp") String sortBy,
            @Parameter(description = "Sort direction (asc/desc)") 
            @RequestParam(defaultValue = "desc") String sortDir
    ) {
        try {
            Sort.Direction direction = sortDir.equalsIgnoreCase("asc") 
                ? Sort.Direction.ASC 
                : Sort.Direction.DESC;
            
            Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
            Page<SyllabusAuditLog> auditLogPage = auditLogService.getAllAuditLogs(pageable);
            
            List<AuditLogResponse> auditLogs = auditLogPage.getContent().stream()
                    .map(AuditLogResponse::fromEntity)
                    .collect(Collectors.toList());
            
            Map<String, Object> response = new HashMap<>();
            response.put("auditLogs", auditLogs);
            response.put("currentPage", auditLogPage.getNumber());
            response.put("totalItems", auditLogPage.getTotalElements());
            response.put("totalPages", auditLogPage.getTotalPages());
            response.put("pageSize", auditLogPage.getSize());
            
            log.info("✓ Retrieved {} audit logs (page {}/{})", 
                     auditLogs.size(), page + 1, auditLogPage.getTotalPages());
            
            return ResponseEntity.ok(new ResponseWrapper<>(
                true,
                "Audit logs retrieved successfully",
                response
            ));
            
        } catch (Exception e) {
            log.error("✗ Error retrieving audit logs", e);
            return ResponseEntity.internalServerError().body(new ResponseWrapper<>(
                false,
                "Failed to retrieve audit logs: " + e.getMessage(),
                null
            ));
        }
    }
    
    /**
     * Get audit logs by date range (Admin only)
     */
    @GetMapping("/date-range")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
        summary = "Get audit logs by date range",
        description = "Retrieve audit logs within a specific date range. Admin only."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Audit logs retrieved successfully"
        ),
        @ApiResponse(responseCode = "403", description = "Access denied"),
        @ApiResponse(responseCode = "400", description = "Invalid date range")
    })
    public ResponseEntity<ResponseWrapper<List<AuditLogResponse>>> getAuditLogsByDateRange(
            @Parameter(description = "Start date (ISO format: 2024-01-01T00:00:00)")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @Parameter(description = "End date (ISO format: 2024-12-31T23:59:59)")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate
    ) {
        try {
            if (startDate.isAfter(endDate)) {
                return ResponseEntity.badRequest().body(new ResponseWrapper<>(
                    false,
                    "Start date must be before end date",
                    null
                ));
            }
            
            List<SyllabusAuditLog> auditLogs = auditLogService.getAuditLogsByDateRange(startDate, endDate);
            List<AuditLogResponse> response = auditLogs.stream()
                    .map(AuditLogResponse::fromEntity)
                    .collect(Collectors.toList());
            
            log.info("✓ Retrieved {} audit logs between {} and {}", 
                     response.size(), startDate, endDate);
            
            return ResponseEntity.ok(new ResponseWrapper<>(
                true,
                String.format("Found %d audit logs in the specified date range", response.size()),
                response
            ));
            
        } catch (Exception e) {
            log.error("✗ Error retrieving audit logs by date range", e);
            return ResponseEntity.internalServerError().body(new ResponseWrapper<>(
                false,
                "Failed to retrieve audit logs: " + e.getMessage(),
                null
            ));
        }
    }
    
    /**
     * Get audit logs by action type (Admin only)
     */
    @GetMapping("/action-type/{actionType}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
        summary = "Get audit logs by action type",
        description = "Retrieve all audit logs for a specific action type (e.g., CREATE_SYLLABUS, UPLOAD_PDF, HOD_APPROVE). Admin only."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Audit logs retrieved successfully"),
        @ApiResponse(responseCode = "403", description = "Access denied")
    })
    public ResponseEntity<ResponseWrapper<List<AuditLogResponse>>> getAuditLogsByActionType(
            @Parameter(description = "Action type (e.g., CREATE_SYLLABUS, HOD_APPROVE)") 
            @PathVariable String actionType
    ) {
        try {
            List<SyllabusAuditLog> auditLogs = auditLogService.getAuditLogsByActionType(actionType);
            List<AuditLogResponse> response = auditLogs.stream()
                    .map(AuditLogResponse::fromEntity)
                    .collect(Collectors.toList());
            
            log.info("✓ Retrieved {} audit logs for action type: {}", response.size(), actionType);
            
            return ResponseEntity.ok(new ResponseWrapper<>(
                true,
                String.format("Found %d audit logs for action type: %s", response.size(), actionType),
                response
            ));
            
        } catch (Exception e) {
            log.error("✗ Error retrieving audit logs by action type: {}", actionType, e);
            return ResponseEntity.internalServerError().body(new ResponseWrapper<>(
                false,
                "Failed to retrieve audit logs: " + e.getMessage(),
                null
            ));
        }
    }
    
    /**
     * Get audit logs by user (Admin only)
     */
    @GetMapping("/user/{username}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
        summary = "Get audit logs by user",
        description = "Retrieve all audit logs for a specific user. Admin only."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Audit logs retrieved successfully"),
        @ApiResponse(responseCode = "403", description = "Access denied")
    })
    public ResponseEntity<ResponseWrapper<List<AuditLogResponse>>> getAuditLogsByUser(
            @Parameter(description = "Username") 
            @PathVariable String username
    ) {
        try {
            List<SyllabusAuditLog> auditLogs = auditLogService.getAuditLogsByUser(username);
            List<AuditLogResponse> response = auditLogs.stream()
                    .map(AuditLogResponse::fromEntity)
                    .collect(Collectors.toList());
            
            log.info("✓ Retrieved {} audit logs for user: {}", response.size(), username);
            
            return ResponseEntity.ok(new ResponseWrapper<>(
                true,
                String.format("Found %d audit logs for user: %s", response.size(), username),
                response
            ));
            
        } catch (Exception e) {
            log.error("✗ Error retrieving audit logs for user: {}", username, e);
            return ResponseEntity.internalServerError().body(new ResponseWrapper<>(
                false,
                "Failed to retrieve audit logs: " + e.getMessage(),
                null
            ));
        }
    }
    
    /**
     * Get audit logs for a specific syllabus (Admin only)
     */
    @GetMapping("/syllabus/{syllabusId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
        summary = "Get audit logs for a specific syllabus",
        description = "Retrieve all audit logs related to a specific syllabus. Admin only."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Audit logs retrieved successfully"),
        @ApiResponse(responseCode = "400", description = "Bad Request - syllabusId must be positive"),
        @ApiResponse(responseCode = "403", description = "Access denied"),
        @ApiResponse(responseCode = "404", description = "Syllabus not found")
    })
    public ResponseEntity<ResponseWrapper<List<AuditLogResponse>>> getAuditLogsBySyllabus(
            @Parameter(description = "Syllabus ID - must be greater than 0") 
            @Positive(message = "Syllabus ID must be greater than 0")
            @PathVariable Long syllabusId
    ) {
        try {
            List<SyllabusAuditLog> auditLogs = auditLogService.getAuditLogsBySyllabus(syllabusId);
            List<AuditLogResponse> response = auditLogs.stream()
                    .map(AuditLogResponse::fromEntity)
                    .collect(Collectors.toList());
            
            log.info("✓ Retrieved {} audit logs for syllabus ID: {}", response.size(), syllabusId);
            
            return ResponseEntity.ok(new ResponseWrapper<>(
                true,
                String.format("Found %d audit logs for syllabus ID: %d", response.size(), syllabusId),
                response
            ));
            
        } catch (Exception e) {
            log.error("✗ Error retrieving audit logs for syllabus: {}", syllabusId, e);
            return ResponseEntity.internalServerError().body(new ResponseWrapper<>(
                false,
                "Failed to retrieve audit logs: " + e.getMessage(),
                null
            ));
        }
    }
    
    /**
     * Get recent audit logs (last N days) (Admin only)
     */
    @GetMapping("/recent")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
        summary = "Get recent audit logs",
        description = "Retrieve audit logs from the last N days. Default is 7 days. Admin only."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Recent audit logs retrieved successfully"),
        @ApiResponse(responseCode = "403", description = "Access denied")
    })
    public ResponseEntity<ResponseWrapper<List<AuditLogResponse>>> getRecentAuditLogs(
            @Parameter(description = "Number of days to look back (default: 7)") 
            @RequestParam(defaultValue = "7") int days
    ) {
        try {
            List<SyllabusAuditLog> auditLogs = auditLogService.getRecentAuditLogs(days);
            List<AuditLogResponse> response = auditLogs.stream()
                    .map(AuditLogResponse::fromEntity)
                    .collect(Collectors.toList());
            
            log.info("✓ Retrieved {} audit logs from the last {} days", response.size(), days);
            
            return ResponseEntity.ok(new ResponseWrapper<>(
                true,
                String.format("Found %d audit logs from the last %d days", response.size(), days),
                response
            ));
            
        } catch (Exception e) {
            log.error("✗ Error retrieving recent audit logs", e);
            return ResponseEntity.internalServerError().body(new ResponseWrapper<>(
                false,
                "Failed to retrieve audit logs: " + e.getMessage(),
                null
            ));
        }
    }
    
    /**
     * Get audit logs statistics (Admin only)
     */
    @GetMapping("/statistics")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
        summary = "Get audit logs statistics",
        description = "Retrieve statistical information about audit logs including counts by action type. Admin only."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Statistics retrieved successfully"),
        @ApiResponse(responseCode = "403", description = "Access denied")
    })
    public ResponseEntity<ResponseWrapper<Map<String, Object>>> getAuditLogStatistics() {
        try {
            Map<String, Object> statistics = auditLogService.getAuditLogStatistics();
            
            log.info("✓ Retrieved audit log statistics");
            
            return ResponseEntity.ok(new ResponseWrapper<>(
                true,
                "Audit log statistics retrieved successfully",
                statistics
            ));
            
        } catch (Exception e) {
            log.error("✗ Error retrieving audit log statistics", e);
            return ResponseEntity.internalServerError().body(new ResponseWrapper<>(
                false,
                "Failed to retrieve statistics: " + e.getMessage(),
                null
            ));
        }
    }
    
    /**
     * Get audit logs by academic year (Admin only)
     */
    @GetMapping("/academic-year/{academicYear}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
        summary = "Get audit logs by academic year",
        description = "Retrieve all audit logs for a specific academic year. Admin only."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Audit logs retrieved successfully"),
        @ApiResponse(responseCode = "403", description = "Access denied")
    })
    public ResponseEntity<ResponseWrapper<List<AuditLogResponse>>> getAuditLogsByAcademicYear(
            @Parameter(description = "Academic year (e.g., 2024-2025)") 
            @PathVariable String academicYear
    ) {
        try {
            List<SyllabusAuditLog> auditLogs = auditLogService.getAuditLogsByAcademicYear(academicYear);
            List<AuditLogResponse> response = auditLogs.stream()
                    .map(AuditLogResponse::fromEntity)
                    .collect(Collectors.toList());
            
            log.info("✓ Retrieved {} audit logs for academic year: {}", response.size(), academicYear);
            
            return ResponseEntity.ok(new ResponseWrapper<>(
                true,
                String.format("Found %d audit logs for academic year: %s", response.size(), academicYear),
                response
            ));
            
        } catch (Exception e) {
            log.error("✗ Error retrieving audit logs for academic year: {}", academicYear, e);
            return ResponseEntity.internalServerError().body(new ResponseWrapper<>(
                false,
                "Failed to retrieve audit logs: " + e.getMessage(),
                null
            ));
        }
    }
    
}
