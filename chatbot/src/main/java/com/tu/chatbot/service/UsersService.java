package com.tu.chatbot.service;

import com.tu.chatbot.model.Role;
import com.tu.chatbot.model.RoleName;
import com.tu.chatbot.model.Users;
import com.tu.chatbot.model.UserPrincipal;
import com.tu.chatbot.model.dto.LoginResponse;
import com.tu.chatbot.model.dto.LoginUserRequest;
import com.tu.chatbot.model.dto.RegisterUserRequest;
import com.tu.chatbot.model.dto.UserResponse;
import com.tu.chatbot.repository.RoleRepository;
import com.tu.chatbot.repository.UsersRepository;
import com.tu.chatbot.security.jwt.JwtService;
import lombok.AllArgsConstructor;
import lombok.val;
import org.springframework.security.access.AuthorizationServiceException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class UsersService {

    private final RoleRepository roleRepository;
    private final UsersRepository usersRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @Transactional
    public UserResponse register(RegisterUserRequest registerUserRequest) {
        usersRepository.findByUsername(registerUserRequest.username())
                .ifPresent(user -> {
                    throw new IllegalArgumentException("Username is already taken.");
                });

        val encodedPassword = passwordEncoder.encode(registerUserRequest.password());

        usersRepository.findByEmail(registerUserRequest.email())
                .ifPresent(user -> {
                    throw new IllegalArgumentException("Email is already taken.");
                });

        val user = new Users();
        user.setUsername(registerUserRequest.username());
        user.setPassword(encodedPassword);
        user.setEmail(registerUserRequest.email());

        val userRoles = new HashSet<Role>();
        val userRole = roleRepository
                .findByName(RoleName.ROLE_USER)
                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
        userRoles.add(userRole);
        user.setRoles(userRoles);

        return toResponse(usersRepository.save(user));
    }

    public LoginResponse login(LoginUserRequest loginUserRequest) {
        val authentication =
                authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(
                                loginUserRequest.username(),
                                loginUserRequest.password()
                        )
                );

        if(!authentication.isAuthenticated()) {
            throw new AuthorizationServiceException("Invalid username or password.");
        }

        val principal = (UserPrincipal) authentication.getPrincipal();
        val token = jwtService.generateJwt(principal);

        return new LoginResponse(
                principal.getUsername(),
                extractRoles(principal),
                token,
                "Bearer",
                jwtService.getExpirationInSeconds()
        );
    }

    private UserResponse toResponse(Users user) {
        return new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getRoles()
                        .stream()
                        .map(role -> role.getName().name())
                        .collect(Collectors.toSet())
        );
    }

    private Set<String> extractRoles(UserPrincipal principal) {
        return principal.getAuthorities()
                .stream()
                .map(grantedAuthority -> grantedAuthority.getAuthority())
                .collect(Collectors.toSet());
    }
}
