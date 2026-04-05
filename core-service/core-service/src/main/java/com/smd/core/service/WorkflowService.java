package com.smd.core.service;

import com.smd.core.dto.WorkflowTransitionRequest;
import com.smd.core.dto.WorkflowTransitionResponse;
import com.smd.core.entity.*;
import com.smd.core.entity.Syllabus.SyllabusStatus;
import com.smd.core.entity.SyllabusAuditLog;
import com.smd.core.entity.SyllabusWorkflowHistory.WorkflowAction;
import com.smd.core.exception.InvalidDataException;
import com.smd.core.exception.ResourceNotFoundException;
import com.smd.core.exception.UnauthorizedException;
import com.smd.core.repository.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
public class WorkflowService {
    
    @Autowired
    private SyllabusRepository syllabusRepository;
    
    @Autowired
    private SyllabusWorkflowHistoryRepository workflowHistoryRepository;
    
    @Autowired
    private WorkflowStepRepository workflowStepRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private RoleRepository roleRepository;
    
    @Autowired
    private AuditLogService auditLogService;
    
    @Autowired
    private NotificationService notificationService;
    
    /**
     * LECTURER: Submit syllabus for review (DRAFT -> PENDING_REVIEW)
     */
    @Transactional
    public WorkflowTransitionResponse submitForReview(WorkflowTransitionRequest request, String username) {
        log.info("Lecturer {} submitting syllabus {} for review", username, request.getSyllabusId());
        
        Syllabus syllabus = getSyllabusOrThrow(request.getSyllabusId());
        User user = getUserOrThrow(username);
        
        // Validate: Only LECTURER can submit
        validateUserHasRole(user, "LECTURER");
        
        // Validate: Only owner can submit
        if (!syllabus.getLecturer().getUserId().equals(user.getUserId())) {
            throw new UnauthorizedException("You can only submit your own syllabus");
        }
        
        // Validate: Must be in DRAFT status
        if (syllabus.getCurrentStatus() != SyllabusStatus.DRAFT) {
            throw new InvalidDataException("Syllabus must be in DRAFT status to submit for review. Current status: " + syllabus.getCurrentStatus());
        }
        
        // Update status
        String previousStatus = syllabus.getCurrentStatus().name();
        syllabus.setCurrentStatus(SyllabusStatus.PENDING_REVIEW);
        syllabusRepository.save(syllabus);
        
        // Record history
        recordWorkflowHistory(syllabus, user, WorkflowAction.SUBMIT, "Pending Review", request.getComment());
        
        // Audit log
        auditLogService.logStatusChange(
            syllabus,
            SyllabusAuditLog.AuditAction.SUBMIT_FOR_REVIEW.name(),
            username,
            previousStatus,
            SyllabusStatus.PENDING_REVIEW.name(),
            request.getComment()
        );
        
        // Send notification to HOD
        notificationService.notifySyllabusSubmitted(syllabus, username);
        
        return WorkflowTransitionResponse.fromSyllabus(
            syllabus, 
            previousStatus, 
            "SUBMIT", 
            username,
            request.getComment(),
            "Syllabus submitted for HOD review successfully"
        );
    }
    
    /**
     * HOD: Approve syllabus (PENDING_REVIEW -> PENDING_APPROVAL)
     */
    @Transactional
    public WorkflowTransitionResponse approveByHOD(WorkflowTransitionRequest request, String username) {
        log.info("HOD {} approving syllabus {}", username, request.getSyllabusId());
        
        Syllabus syllabus = getSyllabusOrThrow(request.getSyllabusId());
        User user = getUserOrThrow(username);
        
        // Validate: Only HEAD_OF_DEPARTMENT can approve
        validateUserHasRole(user, "HEAD_OF_DEPARTMENT");
        
        // Validate: Must be in PENDING_REVIEW status
        if (syllabus.getCurrentStatus() != SyllabusStatus.PENDING_REVIEW) {
            throw new InvalidDataException("Syllabus must be in PENDING_REVIEW status. Current status: " + syllabus.getCurrentStatus());
        }
        
        // Update status
        String previousStatus = syllabus.getCurrentStatus().name();
        syllabus.setCurrentStatus(SyllabusStatus.PENDING_APPROVAL);
        syllabusRepository.save(syllabus);
        
        // Record history
        recordWorkflowHistory(syllabus, user, WorkflowAction.APPROVE, "Pending Approval", request.getComment());
        
        // Audit log
        auditLogService.logStatusChange(
            syllabus,
            SyllabusAuditLog.AuditAction.HOD_APPROVE.name(),
            username,
            previousStatus,
            SyllabusStatus.PENDING_APPROVAL.name(),
            request.getComment()
        );
        
        // Send notifications (to lecturer and AA)
        notificationService.notifyHODApproved(syllabus, username);
        
        return WorkflowTransitionResponse.fromSyllabus(
            syllabus, 
            previousStatus, 
            "APPROVE", 
            username,
            request.getComment(),
            "Syllabus approved by HOD, now pending Academic Affairs approval"
        );
    }
    
    /**
     * HOD: Reject syllabus (PENDING_REVIEW -> DRAFT)
     */
    @Transactional
    public WorkflowTransitionResponse rejectByHOD(WorkflowTransitionRequest request, String username) {
        log.info("HOD {} rejecting syllabus {}", username, request.getSyllabusId());
        
        Syllabus syllabus = getSyllabusOrThrow(request.getSyllabusId());
        User user = getUserOrThrow(username);
        
        // Validate: Only HEAD_OF_DEPARTMENT can reject
        validateUserHasRole(user, "HEAD_OF_DEPARTMENT");
        
        // Validate: Must be in PENDING_REVIEW status
        if (syllabus.getCurrentStatus() != SyllabusStatus.PENDING_REVIEW) {
            throw new InvalidDataException("Syllabus must be in PENDING_REVIEW status. Current status: " + syllabus.getCurrentStatus());
        }
        
        // Update status - Return to DRAFT
        String previousStatus = syllabus.getCurrentStatus().name();
        syllabus.setCurrentStatus(SyllabusStatus.DRAFT);
        syllabusRepository.save(syllabus);
        
        // Record history
        recordWorkflowHistory(syllabus, user, WorkflowAction.REJECT, "Draft", request.getComment());
        
        // Audit log
        auditLogService.logStatusChange(
            syllabus,
            SyllabusAuditLog.AuditAction.HOD_REJECT.name(),
            username,
            previousStatus,
            SyllabusStatus.DRAFT.name(),
            request.getComment()
        );
        
        // Send notification to lecturer
        notificationService.notifyHODRejected(syllabus, username, request.getComment());
        
        return WorkflowTransitionResponse.fromSyllabus(
            syllabus, 
            previousStatus, 
            "REJECT", 
            username,
            request.getComment(),
            "Syllabus rejected by HOD, returned to DRAFT for revision"
        );
    }
    
    /**
     * AA: Approve syllabus (PENDING_APPROVAL -> APPROVED)
     */
    @Transactional
    public WorkflowTransitionResponse approveByAA(WorkflowTransitionRequest request, String username) {
        log.info("Academic Affairs {} approving syllabus {}", username, request.getSyllabusId());
        
        Syllabus syllabus = getSyllabusOrThrow(request.getSyllabusId());
        User user = getUserOrThrow(username);
        
        // Validate: Only ACADEMIC_AFFAIRS can approve
        validateUserHasRole(user, "ACADEMIC_AFFAIRS");
        
        // Validate: Must be in PENDING_APPROVAL status
        if (syllabus.getCurrentStatus() != SyllabusStatus.PENDING_APPROVAL) {
            throw new InvalidDataException("Syllabus must be in PENDING_APPROVAL status. Current status: " + syllabus.getCurrentStatus());
        }
        
        // Update status
        String previousStatus = syllabus.getCurrentStatus().name();
        syllabus.setCurrentStatus(SyllabusStatus.APPROVED);
        syllabusRepository.save(syllabus);
        
        // Record history
        recordWorkflowHistory(syllabus, user, WorkflowAction.APPROVE, "Approved", request.getComment());
        
        // Audit log
        auditLogService.logStatusChange(
            syllabus,
            SyllabusAuditLog.AuditAction.AA_APPROVE.name(),
            username,
            previousStatus,
            SyllabusStatus.APPROVED.name(),
            request.getComment()
        );
        
        // Send notifications (to lecturer and Principal)
        notificationService.notifyAAApproved(syllabus, username);
        
        return WorkflowTransitionResponse.fromSyllabus(
            syllabus, 
            previousStatus, 
            "APPROVE", 
            username,
            request.getComment(),
            "Syllabus approved by Academic Affairs, now pending Principal approval"
        );
    }
    
    /**
     * AA: Reject syllabus (PENDING_APPROVAL -> PENDING_REVIEW)
     */
    @Transactional
    public WorkflowTransitionResponse rejectByAA(WorkflowTransitionRequest request, String username) {
        log.info("Academic Affairs {} rejecting syllabus {}", username, request.getSyllabusId());
        
        Syllabus syllabus = getSyllabusOrThrow(request.getSyllabusId());
        User user = getUserOrThrow(username);
        
        // Validate: Only ACADEMIC_AFFAIRS can reject
        validateUserHasRole(user, "ACADEMIC_AFFAIRS");
        
        // Validate: Must be in PENDING_APPROVAL status
        if (syllabus.getCurrentStatus() != SyllabusStatus.PENDING_APPROVAL) {
            throw new InvalidDataException("Syllabus must be in PENDING_APPROVAL status. Current status: " + syllabus.getCurrentStatus());
        }
        
        // Update status - Return to PENDING_REVIEW for HOD to review again
        String previousStatus = syllabus.getCurrentStatus().name();
        syllabus.setCurrentStatus(SyllabusStatus.PENDING_REVIEW);
        syllabusRepository.save(syllabus);
        
        // Record history
        recordWorkflowHistory(syllabus, user, WorkflowAction.REJECT, "Pending Review", request.getComment());
        
        // Audit log
        auditLogService.logStatusChange(
            syllabus,
            SyllabusAuditLog.AuditAction.AA_REJECT.name(),
            username,
            previousStatus,
            SyllabusStatus.PENDING_REVIEW.name(),
            request.getComment()
        );
        
        // Send notification to lecturer
        notificationService.notifyAARejected(syllabus, username, request.getComment());
        
        return WorkflowTransitionResponse.fromSyllabus(
            syllabus, 
            previousStatus, 
            "REJECT", 
            username,
            request.getComment(),
            "Syllabus rejected by Academic Affairs, returned to PENDING_REVIEW"
        );
    }
    
    /**
     * PRINCIPAL: Approve syllabus (APPROVED -> PUBLISHED)
     */
    @Transactional
    public WorkflowTransitionResponse approveByPrincipal(WorkflowTransitionRequest request, String username) {
        log.info("Principal {} approving syllabus {}", username, request.getSyllabusId());
        
        Syllabus syllabus = getSyllabusOrThrow(request.getSyllabusId());
        User user = getUserOrThrow(username);
        
        // Validate: Only PRINCIPAL can approve
        validateUserHasRole(user, "PRINCIPAL");
        
        // Validate: Must be in APPROVED status
        if (syllabus.getCurrentStatus() != SyllabusStatus.APPROVED) {
            throw new InvalidDataException("Syllabus must be in APPROVED status. Current status: " + syllabus.getCurrentStatus());
        }
        
        // Update status
        String previousStatus = syllabus.getCurrentStatus().name();
        syllabus.setCurrentStatus(SyllabusStatus.PUBLISHED);
        syllabus.setPublishedAt(LocalDateTime.now());
        syllabusRepository.save(syllabus);
        
        // Record history
        recordWorkflowHistory(syllabus, user, WorkflowAction.PUBLISH, "Published", request.getComment());
        
        // Audit log
        auditLogService.logStatusChange(
            syllabus,
            SyllabusAuditLog.AuditAction.PRINCIPAL_APPROVE.name(),
            username,
            previousStatus,
            SyllabusStatus.PUBLISHED.name(),
            request.getComment()
        );
        
        // Send notification to lecturer
        notificationService.notifySyllabusPublished(syllabus, username);
        
        return WorkflowTransitionResponse.fromSyllabus(
            syllabus, 
            previousStatus, 
            "PUBLISH", 
            username,
            request.getComment(),
            "Syllabus published successfully by Principal"
        );
    }
    
    /**
     * PRINCIPAL: Reject syllabus (APPROVED -> PENDING_APPROVAL)
     */
    @Transactional
    public WorkflowTransitionResponse rejectByPrincipal(WorkflowTransitionRequest request, String username) {
        log.info("Principal {} rejecting syllabus {}", username, request.getSyllabusId());
        
        Syllabus syllabus = getSyllabusOrThrow(request.getSyllabusId());
        User user = getUserOrThrow(username);
        
        // Validate: Only PRINCIPAL can reject
        validateUserHasRole(user, "PRINCIPAL");
        
        // Validate: Must be in APPROVED status
        if (syllabus.getCurrentStatus() != SyllabusStatus.APPROVED) {
            throw new InvalidDataException("Syllabus must be in APPROVED status. Current status: " + syllabus.getCurrentStatus());
        }
        
        // Update status - Return to PENDING_APPROVAL for AA to review again
        String previousStatus = syllabus.getCurrentStatus().name();
        syllabus.setCurrentStatus(SyllabusStatus.PENDING_APPROVAL);
        syllabusRepository.save(syllabus);
        
        // Record history
        recordWorkflowHistory(syllabus, user, WorkflowAction.REJECT, "Pending Approval", request.getComment());
        
        // Audit log
        auditLogService.logStatusChange(
            syllabus,
            SyllabusAuditLog.AuditAction.PRINCIPAL_REJECT.name(),
            username,
            previousStatus,
            SyllabusStatus.PENDING_APPROVAL.name(),
            request.getComment()
        );
        
        // Send notification to lecturer
        notificationService.notifyPrincipalRejected(syllabus, username, request.getComment());
        
        return WorkflowTransitionResponse.fromSyllabus(
            syllabus, 
            previousStatus, 
            "REJECT", 
            username,
            request.getComment(),
            "Syllabus rejected by Principal, returned to PENDING_APPROVAL"
        );
    }
    
    /**
     * Get workflow history for a syllabus
     */
    @Transactional(readOnly = true)
    public List<SyllabusWorkflowHistory> getWorkflowHistory(Long syllabusId) {
        Syllabus syllabus = getSyllabusOrThrow(syllabusId);
        return workflowHistoryRepository.findBySyllabus_SyllabusIdOrderByActionTimeDesc(syllabusId);
    }
    
    /**
     * Get syllabuses by status for a user
     */
    @Transactional(readOnly = true)
    public List<Syllabus> getSyllabusesByStatus(SyllabusStatus status, String username) {
        User user = getUserOrThrow(username);
        Set<String> roleNames = user.getUserRoles().stream()
                .map(ur -> ur.getRole().getRoleName())
                .collect(Collectors.toSet());
        
        List<Syllabus> syllabuses = syllabusRepository.findByCurrentStatus(status);
        
        // Filter based on role
        if (roleNames.contains("ADMIN") || roleNames.contains("ACADEMIC_AFFAIRS") || roleNames.contains("PRINCIPAL")) {
            // ADMIN, AA, and PRINCIPAL can see all
            return syllabuses;
        } else if (roleNames.contains("HEAD_OF_DEPARTMENT")) {
            // HOD can see syllabuses from their department
            if (user.getDepartment() != null) {
                return syllabuses.stream()
                        .filter(s -> s.getCourse() != null && 
                                    s.getCourse().getDepartment() != null &&
                                    s.getCourse().getDepartment().getDepartmentId()
                                            .equals(user.getDepartment().getDepartmentId()))
                        .collect(Collectors.toList());
            }
            return List.of();
        } else if (roleNames.contains("LECTURER")) {
            // LECTURER can only see their own
            return syllabuses.stream()
                    .filter(s -> s.getLecturer().getUserId().equals(user.getUserId()))
                    .collect(Collectors.toList());
        }
        
        return List.of();
    }
    
    // ==================== HELPER METHODS ====================
    
    private Syllabus getSyllabusOrThrow(Long syllabusId) {
        return syllabusRepository.findById(syllabusId)
                .orElseThrow(() -> new ResourceNotFoundException("Syllabus", "syllabusId", syllabusId));
    }
    
    private User getUserOrThrow(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
    }
    
    private void validateUserHasRole(User user, String requiredRole) {
        boolean hasRole = user.getUserRoles().stream()
                .anyMatch(ur -> ur.getRole().getRoleName().equals(requiredRole));
        
        if (!hasRole) {
            throw new UnauthorizedException("You must have " + requiredRole + " role to perform this action");
        }
    }
    
    private void recordWorkflowHistory(Syllabus syllabus, User user, WorkflowAction action, 
                                      String stepName, String comment) {
        // Get or create workflow step
        WorkflowStep step = workflowStepRepository.findByStepName(stepName)
                .orElseGet(() -> {
                    WorkflowStep newStep = WorkflowStep.builder()
                            .stepName(stepName)
                            .stepOrder(getStepOrder(stepName))
                            .build();
                    return workflowStepRepository.save(newStep);
                });
        
        // Create history record
        SyllabusWorkflowHistory history = SyllabusWorkflowHistory.builder()
                .syllabus(syllabus)
                .workflowStep(step)
                .actionBy(user)
                .action(action)
                .comment(comment)
                .build();
        
        workflowHistoryRepository.save(history);
        log.info("Recorded workflow history: {} by {} on syllabus {}", action, user.getUsername(), syllabus.getSyllabusId());
    }
    
    private Integer getStepOrder(String stepName) {
        return switch (stepName) {
            case "Draft" -> 1;
            case "Pending Review" -> 2;
            case "Pending Approval" -> 3;
            case "Approved" -> 4;
            case "Published" -> 5;
            default -> 0;
        };
    }
}
