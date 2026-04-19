package com.smd.core.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.smd.core.entity.Syllabus;
import com.smd.core.entity.SyllabusAuditLog;
import com.smd.core.entity.User;
import com.smd.core.exception.ResourceNotFoundException;
import com.smd.core.repository.SyllabusAuditLogRepository;
import com.smd.core.repository.SyllabusRepository;
import com.smd.core.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Service for managing audit logs
 * Provides methods to log various actions and retrieve audit history
 */
@Service
@Slf4j
public class AuditLogService {
    
    @Autowired
    private SyllabusAuditLogRepository auditLogRepository;
    
    @Autowired
    private SyllabusRepository syllabusRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    /**
     * Log a simple action without status change
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void logAction(Syllabus syllabus, String actionType, String username, String comments) {
        logAction(syllabus, actionType, username, null, null, comments, null);
    }
    
    /**
     * Log an action with status change (e.g., workflow transitions)
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void logStatusChange(Syllabus syllabus, String actionType, String username, 
                                String oldStatus, String newStatus, String comments) {
        logAction(syllabus, actionType, username, oldStatus, newStatus, comments, null);
    }
    
    /**
     * Log an action with additional data
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void logAction(Syllabus syllabus, String actionType, String username, 
                         String oldStatus, String newStatus, String comments, 
                         Map<String, Object> additionalData) {
        try {
            User user = userRepository.findByUsername(username).orElse(null);
            String roleName = "UNKNOWN";
            
            if (user != null && user.getRole() != null) {
                roleName = user.getRole().getRoleName();
            }
            
            SyllabusAuditLog auditLog = SyllabusAuditLog.builder()
                    .syllabus(syllabus)
                    .actionType(actionType)
                    .performedBy(username)
                    .performedByRole(roleName)
                    .oldStatus(oldStatus)
                    .newStatus(newStatus)
                    .comments(comments)
                    .ipAddress(getClientIpAddress())
                    .userAgent(getUserAgent())
                    .timestamp(LocalDateTime.now())
                    .build();
            
            if (additionalData != null && !additionalData.isEmpty()) {
                auditLog.setAdditionalData(objectMapper.writeValueAsString(additionalData));
            }
            
            auditLogRepository.save(auditLog);
            
            log.info("✓ Audit log created: [{}] by {} ({}) for syllabus #{}", 
                     actionType, username, roleName, syllabus.getSyllabusId());
            
        } catch (Exception e) {
            // Don't throw exception - audit log failure shouldn't break the main operation
            log.error("✗ Failed to create audit log for action: {} by {} on syllabus {}", 
                      actionType, username, syllabus.getSyllabusId(), e);
        }
    }
    
    /**
     * Log field changes with before/after values
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void logFieldChanges(Syllabus syllabus, String actionType, String username, 
                                Map<String, String> changedFields, String comments) {
        try {
            User user = userRepository.findByUsername(username).orElse(null);
            String roleName = user != null && user.getRole() != null
                ? user.getRole().getRoleName() 
                : "UNKNOWN";
            
            SyllabusAuditLog auditLog = SyllabusAuditLog.builder()
                    .syllabus(syllabus)
                    .actionType(actionType)
                    .performedBy(username)
                    .performedByRole(roleName)
                    .comments(comments)
                    .changedFields(objectMapper.writeValueAsString(changedFields))
                    .ipAddress(getClientIpAddress())
                    .userAgent(getUserAgent())
                    .timestamp(LocalDateTime.now())
                    .build();
            
            auditLogRepository.save(auditLog);
            
            log.info("✓ Audit log with field changes created: [{}] by {} for syllabus #{}", 
                     actionType, username, syllabus.getSyllabusId());
            
        } catch (Exception e) {
            log.error("✗ Failed to create audit log with field changes", e);
        }
    }
    
    /**
     * Get all audit logs for a specific syllabus
     */
    public List<SyllabusAuditLog> getAuditLogsBySyllabus(Long syllabusId) {
        // Verify syllabus exists
        syllabusRepository.findById(syllabusId)
                .orElseThrow(() -> new ResourceNotFoundException("Syllabus", "syllabusId", syllabusId));
        return auditLogRepository.findBySyllabus_SyllabusIdOrderByTimestampDesc(syllabusId);
    }
    
    /**
     * Get all audit logs by a specific user
     */
    public List<SyllabusAuditLog> getAuditLogsByUser(String username) {
        // Verify user exists - throw 404 if not found
        userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
        return auditLogRepository.findByPerformedByOrderByTimestampDesc(username);
    }
    
    /**
     * Get workflow history for a syllabus (submit, approve, reject actions)
     */
    public List<SyllabusAuditLog> getWorkflowHistory(Long syllabusId) {
        // Verify syllabus exists
        syllabusRepository.findById(syllabusId)
                .orElseThrow(() -> new ResourceNotFoundException("Syllabus", "syllabusId", syllabusId));
        return auditLogRepository.findWorkflowHistory(syllabusId);
    }
    
    /**
     * Get audit logs within a date range
     */
    public List<SyllabusAuditLog> getAuditLogsByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return auditLogRepository.findByDateRange(startDate, endDate);
    }
    
    /**
     * Get audit logs by action type
     */
    public List<SyllabusAuditLog> getAuditLogsByActionType(String actionType) {
        return auditLogRepository.findByActionTypeOrderByTimestampDesc(actionType);
    }
    
    /**
     * Get recent audit logs (last N days)
     */
    public List<SyllabusAuditLog> getRecentAuditLogs(int days) {
        LocalDateTime cutoffDate = LocalDateTime.now().minusDays(days);
        return auditLogRepository.findRecentLogs(cutoffDate);
    }
    
    /**
     * Get audit logs for a specific syllabus and user
     */
    public List<SyllabusAuditLog> getAuditLogsBySyllabusAndUser(Long syllabusId, String username) {
        return auditLogRepository.findBySyllabusAndUser(syllabusId, username);
    }
    
    /**
     * Get client IP address from HTTP request
     */
    private String getClientIpAddress() {
        try {
            ServletRequestAttributes attributes = 
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            
            if (attributes != null) {
                HttpServletRequest request = attributes.getRequest();
                
                // Check for forwarded IP first (in case of proxy/load balancer)
                String xForwardedFor = request.getHeader("X-Forwarded-For");
                if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
                    // X-Forwarded-For can contain multiple IPs, get the first one
                    return xForwardedFor.split(",")[0].trim();
                }
                
                String xRealIp = request.getHeader("X-Real-IP");
                if (xRealIp != null && !xRealIp.isEmpty()) {
                    return xRealIp;
                }
                
                return request.getRemoteAddr();
            }
        } catch (Exception e) {
            log.warn("Failed to get client IP address", e);
        }
        return "UNKNOWN";
    }
    
    /**
     * Get user agent from HTTP request
     */
    private String getUserAgent() {
        try {
            ServletRequestAttributes attributes = 
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            
            if (attributes != null) {
                HttpServletRequest request = attributes.getRequest();
                String userAgent = request.getHeader("User-Agent");
                
                if (userAgent != null && userAgent.length() > 500) {
                    return userAgent.substring(0, 500);
                }
                
                return userAgent;
            }
        } catch (Exception e) {
            log.warn("Failed to get user agent", e);
        }
        return "UNKNOWN";
    }
    
    // ==================== ADMIN ENDPOINTS ====================
    
    /**
     * Get all audit logs with pagination (Admin only)
     */
    public Page<SyllabusAuditLog> getAllAuditLogs(Pageable pageable) {
        return auditLogRepository.findAll(pageable);
    }
    
    /**
     * Get audit logs by academic year
     */
    public List<SyllabusAuditLog> getAuditLogsByAcademicYear(String academicYear) {
        return auditLogRepository.findByAcademicYear(academicYear);
    }
    
    /**
     * Get audit log statistics
     */
    public Map<String, Object> getAuditLogStatistics() {
        Map<String, Object> stats = new HashMap<>();
        
        // Total count
        long totalLogs = auditLogRepository.count();
        stats.put("totalLogs", totalLogs);
        
        // Count by action type
        List<Object[]> actionTypeCounts = auditLogRepository.countByActionType();
        Map<String, Long> actionTypeMap = new HashMap<>();
        for (Object[] row : actionTypeCounts) {
            actionTypeMap.put((String) row[0], (Long) row[1]);
        }
        stats.put("countByActionType", actionTypeMap);
        
        // Recent activity (last 24 hours)
        LocalDateTime last24Hours = LocalDateTime.now().minusHours(24);
        List<SyllabusAuditLog> recentLogs = auditLogRepository.findRecentLogs(last24Hours);
        stats.put("logsLast24Hours", recentLogs.size());
        
        // Last 7 days
        LocalDateTime last7Days = LocalDateTime.now().minusDays(7);
        List<SyllabusAuditLog> weekLogs = auditLogRepository.findRecentLogs(last7Days);
        stats.put("logsLast7Days", weekLogs.size());
        
        // Last 30 days
        LocalDateTime last30Days = LocalDateTime.now().minusDays(30);
        List<SyllabusAuditLog> monthLogs = auditLogRepository.findRecentLogs(last30Days);
        stats.put("logsLast30Days", monthLogs.size());
        
        return stats;
    }
}
