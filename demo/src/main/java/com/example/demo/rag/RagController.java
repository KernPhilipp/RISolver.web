package com.example.demo.rag;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/rag")
public class RagController {
    private record RagRequest(String question) {
    }

    private record RagResponse(String answer) {
    }

    private final RagService ragService;

    public RagController(RagService ragService) {
        this.ragService = ragService;
    }

    @PostMapping
    public ResponseEntity<RagResponse> ask(@RequestBody RagRequest req) {
        System.out.println("Backend – empfangene Frage: " + req.question());
        String ans = ragService.getAnswer(req.question());
        return ResponseEntity.ok(new RagResponse(ans));
    }
}
