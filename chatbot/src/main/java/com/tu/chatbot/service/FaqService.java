package com.tu.chatbot.service;

import com.tu.chatbot.model.Faq;
import com.tu.chatbot.repository.FaqRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FaqService {
    private final FaqRepository faqRepository;

    @Transactional(readOnly = true)
    public List<Faq> getAllFaqs() {
        return faqRepository.findAllByOrderByIdAsc();
    }

    @Transactional
    public Faq createFaq(String question, String answer, String createdBy) {
        Faq faq = new Faq();
        faq.setQuestion(question);
        faq.setAnswer(answer);
        faq.setCreatedBy(createdBy);
        return faqRepository.save(faq);
    }

    @Transactional
    public void deleteFaq(Long id) {
        faqRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public Faq getFaqById(Long id) {
        return faqRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("FAQ not found with id: " + id));
    }
}

