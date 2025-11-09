//package com.tu.chatbot.service;
//
//import com.tu.chatbot.model.Role;
//import com.tu.chatbot.model.RoleName;
//import com.tu.chatbot.model.Users;
//import com.tu.chatbot.model.dto.LoginUserRequest;
//import com.tu.chatbot.model.dto.RegisterUserRequest;
//import com.tu.chatbot.repository.RoleRepository;
//import com.tu.chatbot.repository.UsersRepository;
//import com.tu.chatbot.security.jwt.JwtService;
//import lombok.AllArgsConstructor;
//import lombok.val;
//import org.springframework.security.access.AuthorizationServiceException;
//import org.springframework.security.authentication.AuthenticationManager;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.stereotype.Service;
//
//import java.util.HashSet;
//
//@Service
//@AllArgsConstructor
//public class UsersService {
//
//    private final RoleRepository roleRepository;
//    private final UsersRepository usersRepository;
//    private final PasswordEncoder passwordEncoder;
//    private final JwtService jwtService;
//    private final AuthenticationManager authenticationManager;
//
//    public Users register(RegisterUserRequest registerUserRequest) {
//        val encodedPassword = passwordEncoder.encode(registerUserRequest.password());
//
//        val user = new Users();
//        user.setUsername(registerUserRequest.username());
//        user.setPassword(encodedPassword);
//
//        val userRoles = new HashSet<Role>();
//        val userRole = roleRepository
//                .findByName(RoleName.ROLE_USER)
//                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
//        userRoles.add(userRole);
//        user.setRoles(userRoles);
//
//        return usersRepository.save(user);
//    }
//
//    public String login(LoginUserRequest loginUserRequest) {
//        val user = Users.from(loginUserRequest);
//
//        val authentication =
//                authenticationManager.authenticate(
//                        new UsernamePasswordAuthenticationToken(
//                                user.getUsername(),
//                                user.getPassword()
//                        )
//                );
//
//        if(authentication.isAuthenticated()) {
//            return jwtService.generateJwt(user.getUsername());
//        } else {
//            throw new AuthorizationServiceException("Access denied for user!");
//        }
//    }
//}
