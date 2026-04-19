package com.smd.core.util;

import com.smd.core.entity.User;
import com.smd.core.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

/**
 * Utility class for authorization checks
 * Provides helper methods to verify user permissions and roles
 */
@Component
@RequiredArgsConstructor
public class AuthorizationUtil {
    
    private final UserRepository userRepository;
    
    /**
     * Get the current authenticated user's username
     * @return username of the current user, or null if not authenticated
     */
    public String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            Object principal = authentication.getPrincipal();
            if (principal instanceof UserDetails) {
                return ((UserDetails) principal).getUsername();
            } else {
                return principal.toString();
            }
        }
        return null;
    }
    
    /**
     * Get the current authenticated user entity
     * @return User entity, or null if not authenticated
     */
    public User getCurrentUser() {
        String username = getCurrentUsername();
        if (username != null) {
            return userRepository.findByUsername(username).orElse(null);
        }
        return null;
    }
    
    /**
     * Check if current user has a specific role
     * @param role the role to check (e.g., "ADMIN", "ACADEMIC_AFFAIRS")
     * @return true if user has this role, false otherwise
     */
    public boolean hasRole(String role) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            String rolePrefix = "ROLE_";
            String fullRole = rolePrefix + role;
            return authentication.getAuthorities().stream()
                    .anyMatch(auth -> auth.getAuthority().equals(fullRole));
        }
        return false;
    }
    
    /**
     * Check if current user has any of the specified roles
     * @param roles var args of roles to check
     * @return true if user has at least one of these roles
     */
    public boolean hasAnyRole(String... roles) {
        for (String role : roles) {
            if (hasRole(role)) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Check if current user belongs to a specific department
     * @param departmentId the department ID to check
     * @return true if user's department matches the provided ID
     */
    public boolean isInDepartment(Long departmentId) {
        User currentUser = getCurrentUser();
        if (currentUser == null || currentUser.getDepartment() == null) {
            return false;
        }
        return currentUser.getDepartment().getDepartmentId().equals(departmentId);
    }
    
    /**
     * Check if current user is ADMIN or ACADEMIC_AFFAIRS
     * @return true if user has ADMIN or ACADEMIC_AFFAIRS role
     */
    public boolean isAdminOrAcademicAffairs() {
        return hasAnyRole("ADMIN", "ACADEMIC_AFFAIRS");
    }
    
    /**
     * Check if current user is ADMIN
     * @return true if user has ADMIN role
     */
    public boolean isAdmin() {
        return hasRole("ADMIN");
    }
    
    /**
     * Check if current user is ACADEMIC_AFFAIRS
     * @return true if user has ACADEMIC_AFFAIRS role
     */
    public boolean isAcademicAffairs() {
        return hasRole("ACADEMIC_AFFAIRS");
    }
    
    /**
     * Check if current user is HEAD_OF_DEPARTMENT
     * @return true if user has HEAD_OF_DEPARTMENT role
     */
    public boolean isHeadOfDepartment() {
        return hasRole("HEAD_OF_DEPARTMENT");
    }
    
    /**
     * Check if current user is LECTURER
     * @return true if user has LECTURER role
     */
    public boolean isLecturer() {
        return hasRole("LECTURER");
    }
    
    /**
     * Check if current user is STUDENT
     * @return true if user has STUDENT role
     */
    public boolean isStudent() {
        return hasRole("STUDENT");
    }
    
    /**
     * Check if current user can manage courses in a department
     * (ADMIN, ACADEMIC_AFFAIRS, or HEAD_OF_DEPARTMENT of that department)
     * @param departmentId the department ID
     * @return true if user can manage courses in this department
     */
    public boolean canManageCoursesInDepartment(Long departmentId) {
        if (isAdminOrAcademicAffairs()) {
            return true;
        }
        if (isHeadOfDepartment()) {
            return isInDepartment(departmentId);
        }
        return false;
    }
    
    /**
     * Check if current user can update course relationships in a department
     * (ADMIN, ACADEMIC_AFFAIRS, or HEAD_OF_DEPARTMENT of that department)
     * @param departmentId the department ID
     * @return true if user can update course relationships in this department
     */
    public boolean canManageCourseRelationsInDepartment(Long departmentId) {
        return canManageCoursesInDepartment(departmentId);
    }
    
    /**
     * Check if current user can create/update CLO in a department
     * @param departmentId the department ID
     * @return true if user can manage CLOs in this department
     */
    public boolean canManageCLOsInDepartment(Long departmentId) {
        return canManageCoursesInDepartment(departmentId);
    }
}
