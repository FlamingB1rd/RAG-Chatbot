package com.tu.chatbot.controller;

import com.tu.chatbot.service.DataIngestionService;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotNull;

@RestController
@RequestMapping("/api/data")
public class ContextDataIngestionController {
    DataIngestionService dataIngestionService;

    @Autowired
    public ContextDataIngestionController(DataIngestionService dataIngestionService) {
        this.dataIngestionService = dataIngestionService;
    }

    @PostMapping("/ingest")
    public ResponseEntity<String> ingestDataFromUrl(@RequestBody @NotNull IngestUrl req) {
        val extracted = dataIngestionService.ingestFromUrl(req.url);
        return ResponseEntity.ok(extracted);
    }

    //    @PostMapping(path = "/ingest", consumes = "multipart/form-data")
    //    public ResponseEntity<String> ingestDataFromPdf(@RequestPart("file") MultipartFile file) {
    //        dataIngestionService.ingestFromPdf(file);
    //        return ResponseEntity.ok("Ingested PDF: " + file.getOriginalFilename());
    //    }

    record IngestUrl(String url) {}
}
