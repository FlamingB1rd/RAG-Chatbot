package com.tu.chatbot.controller;

import com.tu.chatbot.model.Faq;
import com.tu.chatbot.model.UserPrincipal;
import com.tu.chatbot.model.dto.FaqRequest;
import com.tu.chatbot.model.dto.FaqResponse;
import com.tu.chatbot.service.AuditLogService;
import com.tu.chatbot.service.FaqService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/faq")
@RequiredArgsConstructor
public class FaqController {
    private final FaqService faqService;
    private final AuditLogService auditLogService;

    @GetMapping
    public ResponseEntity<List<FaqResponse>> getAllFaqs() {
        List<Faq> faqs = faqService.getAllFaqs();
        List<FaqResponse> responses = faqs.stream()
                .map(faq -> new FaqResponse(faq.getId(), faq.getQuestion(), faq.getAnswer()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<FaqResponse> createFaq(
            @RequestBody @Valid FaqRequest request,
            Authentication authentication) {
        String username = ((UserPrincipal) authentication.getPrincipal()).getUsername();
        Faq faq = faqService.createFaq(request.question(), request.answer(), username);
        
        // Log the creation
        auditLogService.log(
            "FAQ_CREATE",
            "FAQ",
            faq.getId(),
            String.format("Created FAQ: %s", request.question().substring(0, Math.min(50, request.question().length()))),
            username,
            null,
            String.format("Q: %s", request.question())
        );
        
        return ResponseEntity.ok(new FaqResponse(faq.getId(), faq.getQuestion(), faq.getAnswer()));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> deleteFaq(
            @PathVariable Long id,
            Authentication authentication) {
        Faq faq = faqService.getFaqById(id);
        String question = faq.getQuestion();
        faqService.deleteFaq(id);

        String username = ((UserPrincipal) authentication.getPrincipal()).getUsername();
        auditLogService.log(
            "FAQ_DELETE",
            "FAQ",
            id,
            String.format("Deleted FAQ: %s", question.substring(0, Math.min(50, question.length()))),
            username,
            String.format("Q: %s", question),
            null
        );
        
        return ResponseEntity.ok("FAQ deleted successfully");
    }
}

