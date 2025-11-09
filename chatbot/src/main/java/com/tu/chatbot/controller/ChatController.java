package com.tu.chatbot.controller;

import com.tu.chatbot.service.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotNull;

@RestController()
@RequestMapping("/api/chat")
public class ChatController {

    public final ChatService chatService;

    @Autowired
    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @PostMapping("")
    public ResponseEntity<String> chat(@RequestBody @NotNull String question) {
        try {
            return new ResponseEntity<>(chatService.chat(question), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    //TODO: Nice to have, only implement if I have time:
//    @GetMapping("/stream")
//    public Flux<String> chatStream(@RequestParam @NotNull String question) {
//        return chatClient.prompt()
//                .user(question.trim())
//                .stream()
//                .content();
//    }
}
