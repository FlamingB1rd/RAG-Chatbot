package com.tu.chatbot.controller;

import com.tu.chatbot.model.dto.LoginResponse;
import com.tu.chatbot.model.dto.LoginUserRequest;
import com.tu.chatbot.model.dto.RegisterUserRequest;
import com.tu.chatbot.model.dto.UserResponse;
import com.tu.chatbot.service.UsersService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user")
public class UserController {

    private final UsersService usersService;

    @Autowired
    public UserController(UsersService usersService) {
        this.usersService = usersService;
    }

    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(@RequestBody @Valid RegisterUserRequest registerUserRequest) {
        return ResponseEntity.ok(usersService.register(registerUserRequest));
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody @Valid LoginUserRequest loginUserRequest) {
        return ResponseEntity.ok(usersService.login(loginUserRequest));
    }
}
