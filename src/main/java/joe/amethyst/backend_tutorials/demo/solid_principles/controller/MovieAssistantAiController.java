package joe.amethyst.backend_tutorials.demo.solid_principles.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import joe.amethyst.backend_tutorials.demo.solid_principles.domain.MovieAssistantAiResponse;
import joe.amethyst.backend_tutorials.demo.solid_principles.service.DocumentRagService;
import joe.amethyst.backend_tutorials.demo.solid_principles.service.manager.MovieAssistantAiService;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/movie-assistant")
@AllArgsConstructor
public class MovieAssistantAiController {

    private final MovieAssistantAiService movieAssistantAiService;
    private final DocumentRagService documentRagService;

    @PostMapping("/chat")
    public ResponseEntity<MovieAssistantAiResponse> chat(@RequestBody String userRequest) {
        return ResponseEntity.ok(movieAssistantAiService.processRequest(userRequest));
    }

    @GetMapping("/recommendations")
    public ResponseEntity<List<String>> getRecommendations(@RequestParam String param) {
        return ResponseEntity.ok(movieAssistantAiService.askAIforRecommendations(param));
    }

    @PostMapping("/rag/upload")
    public ResponseEntity<String> uploadDocument(@RequestParam("file") MultipartFile file) {
        return ResponseEntity.ok(documentRagService.ingestDocument(file));
    }

    @PostMapping("/rag/ask")
    public ResponseEntity<String> askQuestion(
            @RequestParam("file") MultipartFile file,
            @RequestParam("question") String question) {
        return ResponseEntity.ok(documentRagService.answerQuestion(file, question));
    }
}
