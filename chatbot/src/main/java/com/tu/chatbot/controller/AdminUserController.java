package com.tu.chatbot.controller;

import com.tu.chatbot.model.Role;
import com.tu.chatbot.model.RoleName;
import com.tu.chatbot.model.UserPrincipal;
import com.tu.chatbot.model.Users;
import com.tu.chatbot.model.dto.UpdateUserRoleRequest;
import com.tu.chatbot.model.dto.UserListResponse;
import com.tu.chatbot.repository.RoleRepository;
import com.tu.chatbot.repository.UsersRepository;
import com.tu.chatbot.service.AuditLogService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin/users")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminUserController {
    private final UsersRepository usersRepository;
    private final RoleRepository roleRepository;
    private final AuditLogService auditLogService;

    @GetMapping
    public ResponseEntity<List<UserListResponse>> getAllUsers() {
        List<Users> users = usersRepository.findAll();
        List<UserListResponse> responses = users.stream()
                .map(user -> new UserListResponse(
                        user.getId(),
                        user.getUsername(),
                        user.getEmail(),
                        user.getRoles().stream()
                                .map(role -> role.getName().name())
                                .collect(Collectors.toSet())
                ))
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    @PutMapping("/{userId}/role")
    public ResponseEntity<UserListResponse> updateUserRole(
            @PathVariable Long userId,
            @RequestBody @Valid UpdateUserRoleRequest request,
            Authentication authentication) {
        Users user = usersRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        String oldRole = user.getRoles().stream()
                .map(r -> r.getName().name())
                .findFirst()
                .orElse("NONE");

        RoleName roleName;
        try {
            roleName = RoleName.valueOf(request.role());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid role: " + request.role());
        }

        Role role = roleRepository.findByName(roleName)
                .orElseThrow(() -> new RuntimeException("Role not found: " + roleName));

        Set<Role> roles = new HashSet<>();
        roles.add(role);
        user.setRoles(roles);

        Users savedUser = usersRepository.save(user);
        
        // Log the change
        String username = ((UserPrincipal) authentication.getPrincipal()).getUsername();
        auditLogService.log(
            "USER_ROLE_CHANGE",
            "USER",
            userId,
            String.format("Changed role of user '%s' from %s to %s", user.getUsername(), oldRole, request.role()),
            username,
            oldRole,
            request.role()
        );
        
        UserListResponse response = new UserListResponse(
                savedUser.getId(),
                savedUser.getUsername(),
                savedUser.getEmail(),
                savedUser.getRoles().stream()
                        .map(r -> r.getName().name())
                        .collect(Collectors.toSet())
        );
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<String> deleteUser(
            @PathVariable Long userId,
            Authentication authentication) {
        Users user = usersRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
        
        String username = user.getUsername();
        String email = user.getEmail();
        
        usersRepository.deleteById(userId);
        
        // Log the deletion
        String performedBy = ((UserPrincipal) authentication.getPrincipal()).getUsername();
        auditLogService.log(
            "USER_DELETE",
            "USER",
            userId,
            String.format("Deleted user: %s (%s)", username, email),
            performedBy,
            String.format("username=%s, email=%s", username, email),
            null
        );
        
        return ResponseEntity.ok("User deleted successfully");
    }

    @GetMapping("/roles")
    public ResponseEntity<List<String>> getAllRoles() {
        List<Role> roles = roleRepository.findAll();
        List<String> roleNames = roles.stream()
                .map(role -> role.getName().name())
                .collect(Collectors.toList());
        return ResponseEntity.ok(roleNames);
    }
}

