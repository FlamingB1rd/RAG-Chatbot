package com.tu.chatbot.service;

import lombok.RequiredArgsConstructor;
import lombok.val;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.pgvector.PgVectorStore;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DataIngestionService {

    private final PgVectorStore vectorStore;
    private final JdbcTemplate jdbcTemplate;

    public void ingestFromUrl(String url) {
        org.jsoup.nodes.Document htmlDoc;
        try {
            htmlDoc = Jsoup.connect(url)
                    .userAgent("Mozilla/5.0 (SpringAI-Crawler)")
                    .timeout(15_000)
                    .get();
        } catch (IOException e) {
            throw new RuntimeException("Cannot fetch url: " + url, e);
        }

        val newContent = extractFromDocument(htmlDoc);

        if (existsByUrl(url)) {
            val existingContent = getContentByUrl(url);

            val normalizedExisting = existingContent.trim().replaceAll("\\s+", " ");
            val normalizedNew = newContent.trim().replaceAll("\\s+", " ");

            if (!normalizedExisting.equals(normalizedNew)) {
                val filter = String.format("url == '%s'", url.replace("'", "''"));
                vectorStore.delete(filter);
                
                val document = new Document(
                        UUID.randomUUID().toString(),
                        newContent,
                        Map.of("url", url)
                );
                vectorStore.add(List.of(document));
            }
        } else {
            val document = new Document(
                    UUID.randomUUID().toString(),
                    newContent,
                    Map.of("url", url)
            );
            vectorStore.add(List.of(document));
        }
    }

    private String extractFromDocument(org.jsoup.nodes.Document doc) {
        val content = String.format("""
                Title: %s
                Main Content: %s
                Side content: %s
                """, findTitle(doc), findMainContent(doc), findSideContent(doc));

        return content;
    }

    private String findTitle(org.jsoup.nodes.Document doc) {
        val element = doc.selectFirst("h1, h2, title");
        return element != null ? element.text() : "";
    }

    private String findMainContent(org.jsoup.nodes.Document doc) {
        doc.select("script, style, noscript, header, footer").remove();

        Element element = doc.selectFirst("#right_column .ckedited, #content_wrapper");
        if (element == null) {
            element = doc.body();
        }

        element.select("#left_column, nav, ul, ol").remove();

        return element.text().trim();
    }

    private String findSideContent(org.jsoup.nodes.Document doc) {
        val element = doc.selectFirst("#left_column, aside, nav");
        return element != null ? element.text() : "";
    }

    public boolean existsByUrl(String url) {
        String sql = """
            SELECT COUNT(*) 
            FROM vector_store 
            WHERE metadata->>'url' = ?
            """;

        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, url);
        return count != null && count > 0;
    }

    public List<String> getAllUrls() {
        String sql = """
            SELECT DISTINCT metadata->>'url' as url
            FROM vector_store
            WHERE metadata->>'url' IS NOT NULL
            ORDER BY url
            """;
        return jdbcTemplate.queryForList(sql, String.class);
    }

    public String getContentByUrl(String url) {
        String sql = """
            SELECT content
            FROM vector_store
            WHERE metadata->>'url' = ?
            LIMIT 1
            """;
        List<String> results = jdbcTemplate.queryForList(sql, String.class, url);
        return results.isEmpty() ? "" : results.get(0);
    }

    public void deleteByUrl(String url) {
        String filter = String.format("url == '%s'", url.replace("'", "''"));
        vectorStore.delete(filter);
    }
}
