package com.tu.chatbot.service;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.memory.repository.jdbc.JdbcChatMemoryRepository;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.pgvector.PgVectorStore;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatClient chatClient;
    private final PgVectorStore vectorStore;
    private final JdbcChatMemoryRepository chatMemoryRepository;
    private final ConfigService configService;
    private final ConversationService conversationService;

    private String promptResourceString;

    @PostConstruct
    void loadTemplateResource() throws IOException {
        val resource = new ClassPathResource("prompt-template/augmented-prompt.st");
        this.promptResourceString = new String(resource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
    }

    @Transactional
    public String chat(String userPrompt, Long conversationId, Long userId) throws IOException {
        if (conversationId == null) {
            throw new RuntimeException("Conversation ID is required");
        }

        conversationService.addMessage(conversationId, userId, "user", userPrompt);

        val context = getRelevantContext(userPrompt);
        val promptTemplate = PromptTemplate.builder()
                .template(promptResourceString)
                .variables(Map.of(
                        "context", context,
                        "userPrompt", userPrompt)
                )
                .build();

        val chatMemory = MessageWindowChatMemory.builder()
                .chatMemoryRepository(chatMemoryRepository)
                .maxMessages(10)
                .build();

        val assistantResponse = chatClient
                .prompt(promptTemplate.create())
                .advisors(MessageChatMemoryAdvisor.builder(chatMemory).build())
                .call()
                .content();

        conversationService.addMessage(conversationId, userId, "assistant", assistantResponse);

        return assistantResponse;
    }

    private String getRelevantContext(String userPrompt) {
        val relevantContextDocuments = vectorStore.similaritySearch(
                SearchRequest.builder()
                        .query(userPrompt)
                        .topK(configService.getTopK())
                        .similarityThreshold(configService.getSimilarityThreshold())
                        .build()
        );

        if (relevantContextDocuments.isEmpty()) {
            return "No relevant information found. Ask the user for more specific details.";
        }

        val context = new StringBuilder();
        for (Document document : relevantContextDocuments) {
            context.append(document.getFormattedContent()).append("\n");
        }

        return context.toString();
    }
}
