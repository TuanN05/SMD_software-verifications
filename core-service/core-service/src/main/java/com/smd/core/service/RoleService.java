package com.smd.core.service;

import com.smd.core.dto.AssignRoleRequest;
import com.smd.core.dto.UserRoleResponse;
import com.smd.core.entity.Role;
import com.smd.core.entity.User;
import com.smd.core.entity.UserRole;
import com.smd.core.exception.DuplicateResourceException;
import com.smd.core.exception.ResourceNotFoundException;
import com.smd.core.repository.RoleRepository;
import com.smd.core.repository.UserRepository;
import com.smd.core.repository.UserRoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RoleService {

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserRoleRepository userRoleRepository;

    /**
     * Get all available roles in the system
     */
    public List<Role> getAllRoles() {
        return roleRepository.findAll();
    }

    /**
     * Create a new role (for seeding or admin purposes)
     */
    @Transactional
    public Role createRole(String roleName) {
        if (roleRepository.findByRoleName(roleName).isPresent()) {
            throw new DuplicateResourceException("Role", "roleName", roleName);
        }

        Role role = Role.builder()
                .roleName(roleName.toUpperCase())
                .build();

        return roleRepository.save(role);
    }

    /**
     * Assign a role to a user
     */
    @Transactional
    public UserRoleResponse assignRoleToUser(AssignRoleRequest request) {
        System.out.println("\n==> [ASSIGN ROLE] Assigning role '" + request.getRoleName() + "' to userId: " + request.getUserId());

        // Get user
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "userId", request.getUserId()));

        // Get role
        Role role = roleRepository.findByRoleName(request.getRoleName().toUpperCase())
                .orElseThrow(() -> new ResourceNotFoundException("Role", "roleName", request.getRoleName()));

        // Check if user already has this role
        boolean hasRole = user.getUserRoles() != null && user.getUserRoles().stream()
                .anyMatch(ur -> ur.getRole().getRoleId().equals(role.getRoleId()));

        if (hasRole) {
            throw new DuplicateResourceException("UserRole", "user_role", 
                    "User already has role: " + request.getRoleName());
        }

        // Create user-role mapping
        UserRole userRole = UserRole.builder()
                .user(user)
                .role(role)
                .build();

        userRoleRepository.save(userRole);

        // Get all roles of the user
        List<String> userRoles = userRoleRepository.findByUser_UserId(user.getUserId()).stream()
                .map(ur -> ur.getRole().getRoleName())
                .collect(Collectors.toList());

        System.out.println("==> [ASSIGN ROLE SUCCESS] User now has roles: " + userRoles);

        return UserRoleResponse.builder()
                .userId(user.getUserId())
                .username(user.getUsername())
                .fullName(user.getFullName())
                .roles(userRoles)
                .message("Role assigned successfully")
                .build();
    }

    /**
     * Remove a role from a user
     */
    @Transactional
    public UserRoleResponse removeRoleFromUser(Long userId, String roleName) {
        System.out.println("\n==> [REMOVE ROLE] Removing role '" + roleName + "' from userId: " + userId);

        // Get user
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "userId", userId));

        // Get role
        Role role = roleRepository.findByRoleName(roleName.toUpperCase())
                .orElseThrow(() -> new ResourceNotFoundException("Role", "roleName", roleName));

        // Find and delete user-role mapping
        UserRole userRole = userRoleRepository.findByUser_UserIdAndRole_RoleId(userId, role.getRoleId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "User does not have role: " + roleName));

        userRoleRepository.delete(userRole);

        // Get remaining roles
        List<String> remainingRoles = userRoleRepository.findByUser_UserId(userId).stream()
                .map(ur -> ur.getRole().getRoleName())
                .collect(Collectors.toList());

        System.out.println("==> [REMOVE ROLE SUCCESS] User now has roles: " + remainingRoles);

        return UserRoleResponse.builder()
                .userId(user.getUserId())
                .username(user.getUsername())
                .fullName(user.getFullName())
                .roles(remainingRoles)
                .message("Role removed successfully")
                .build();
    }

    /**
     * Get all roles of a user
     */
    public UserRoleResponse getUserRoles(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "userId", userId));

        List<String> userRoles = userRoleRepository.findByUser_UserId(userId).stream()
                .map(ur -> ur.getRole().getRoleName())
                .collect(Collectors.toList());

        return UserRoleResponse.builder()
                .userId(user.getUserId())
                .username(user.getUsername())
                .fullName(user.getFullName())
                .roles(userRoles)
                .build();
    }

    /**
     * Initialize default roles in the system
     */
    @Transactional
    public void initializeDefaultRoles() {
        String[] defaultRoles = {"ADMIN", "LECTURER", "HEAD_OF_DEPARTMENT", "ACADEMIC_AFFAIRS", "PRINCIPAL", "STUDENT"};

        for (String roleName : defaultRoles) {
            if (roleRepository.findByRoleName(roleName).isEmpty()) {
                createRole(roleName);
                System.out.println("==> Created default role: " + roleName);
            }
        }
    }
}
