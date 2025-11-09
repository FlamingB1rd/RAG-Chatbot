//package com.tu.chatbot.controller;
//
//import com.tu.chatbot.model.Users;
//import com.tu.chatbot.model.dto.LoginUserRequest;
//import com.tu.chatbot.model.dto.RegisterUserRequest;
//import com.tu.chatbot.service.UsersService;
//import lombok.val;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//@RestController
//@RequestMapping("/api/user")
//public class UserController {
//
//    private final UsersService usersService;
//
//    @Autowired
//    public UserController(UsersService usersService) {
//        this.usersService = usersService;
//    }
//
//    @PostMapping("/register")
//    public Users register(@RequestBody RegisterUserRequest registerUserRequest) {
//       val users = new Users();
//
//       return usersService.register(registerUserRequest);
//    }
//
//    @PostMapping("/login")
//    public String login(@RequestBody LoginUserRequest loginUserRequest) {
//        System.out.println(loginUserRequest);
//
//        return usersService.login(loginUserRequest);
//    }
//}
