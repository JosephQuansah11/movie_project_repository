package joe.amethyst.backend_tutorials.demo.solid_principles.service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class DocumentRagService {
    private final Map<String, String> documentCache = new ConcurrentHashMap<>();
    private final Map<String, List<String>> chunkCache = new ConcurrentHashMap<>();

    public String ingestDocument(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Document file is required");
        }

        String content = readDocumentContent(file);
        String key = file.getOriginalFilename() == null ? "document" : file.getOriginalFilename();
        documentCache.put(key, content);
        chunkCache.put(key, chunkTextIntoSentences(content));
        return "Document loaded successfully: " + key;
    }

    public String answerQuestion(MultipartFile file, String question) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Document file is required");
        }

        String documentKey = file.getOriginalFilename() == null ? "document" : file.getOriginalFilename();
        String documentText = documentCache.get(documentKey);
        if (documentText == null) {
            documentText = readDocumentContent(file);
            documentCache.put(documentKey, documentText);
            chunkCache.put(documentKey, chunkTextIntoSentences(documentText));
        }

        List<String> chunks = chunkCache.get(documentKey);
        if (chunks == null || chunks.isEmpty()) {
            chunks = chunkTextIntoSentences(documentText);
            chunkCache.put(documentKey, chunks);
        }

        String context = extractRelevantContext(chunks, question);
        String prompt = "Use only the document context below to answer the user's question. "
                + "If the answer is not explicitly in the document, say that clearly.\n\n"
                + "Document context:\n"
                + context + "\n\nQuestion:\n"
                + question;

        String modelAnswer = callOllama(prompt);
        if (modelAnswer != null && !modelAnswer.startsWith("RAG answer failed")
                && !modelAnswer.startsWith("Unable to answer from uploaded document using Ollama")) {
            return modelAnswer;
        }

        return buildFallbackAnswer(context, question);
    }

    private String extractRelevantContext(List<String> chunks, String question) {
        if (chunks == null || chunks.isEmpty()) {
            return "";
        }

        String lowerQuestion = question == null ? "" : question.toLowerCase();
        List<String> relevant = new ArrayList<>();
        for (String chunk : chunks) {
            String lowerChunk = chunk.toLowerCase();
            if (lowerQuestion.isBlank() || lowerChunk.contains(lowerQuestion) || containsKeywords(lowerChunk, lowerQuestion)) {
                relevant.add(chunk);
            }
        }

        if (relevant.isEmpty()) {
            return String.join(" ", chunks.subList(0, Math.min(chunks.size(), 3)));
        }

        return String.join(" ", relevant.subList(0, Math.min(relevant.size(), 5)));
    }

    private boolean containsKeywords(String lowerChunk, String lowerQuestion) {
        String normalized = lowerQuestion.replaceAll("[^a-z0-9\\s]", " ");
        for (String term : normalized.split("\\s+")) {
            if (term.length() > 3 && lowerChunk.contains(term)) {
                return true;
            }
        }
        return false;
    }

    private String buildFallbackAnswer(String context, String question) {
        if (context == null || context.isBlank()) {
            return "The uploaded document does not contain enough information to answer: " + question;
        }

        String normalizedQuestion = question == null ? "" : question.trim();
        String answer = context;
        if (answer.length() > 600) {
            answer = answer.substring(0, 600) + "...";
        }
        return "Based on the uploaded document: " + answer;
    }

    private String readDocumentContent(MultipartFile file) {
        try {
            return new String(file.getBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new IllegalArgumentException("Unable to read uploaded document", e);
        }
    }

    private List<String> chunkTextIntoSentences(String text) {
        if (text == null || text.isBlank()) {
            return List.of();
        }

        List<String> chunks = new ArrayList<>();
        String[] sentences = text.split("(?<=[.!?])\\s+");
        for (String sentence : sentences) {
            String trimmed = sentence.trim();
            if (!trimmed.isEmpty()) {
                chunks.add(trimmed);
            }
        }
        return chunks;
    }

    private String callOllama(String prompt) {
        try {
            HttpClient client = HttpClient.newHttpClient();
            String body = "{\"model\":\"codeqwen:latest\",\"messages\":[{\"role\":\"user\",\"content\":\"" + prompt.replace("\"", "\\\"") + "\"}],\"stream\":false}";

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:11434/api/chat"))
                    .header("Content-Type", "application/json")
                    .header("Accept", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(body))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() >= 400) {
                return "Unable to answer from uploaded document using Ollama: HTTP " + response.statusCode();
            }

            String responseBody = response.body();
            if (responseBody == null || responseBody.isBlank()) {
                return "No answer received from Ollama.";
            }

            int messageIndex = responseBody.indexOf("\"message\"");
            if (messageIndex < 0) {
                return responseBody;
            }

            int contentIndex = responseBody.indexOf("\"content\"", messageIndex);
            if (contentIndex < 0) {
                return responseBody;
            }

            int valueStart = responseBody.indexOf(':', contentIndex);
            int firstQuote = responseBody.indexOf('"', valueStart + 1);
            int lastQuote = responseBody.lastIndexOf('"');
            if (firstQuote >= 0 && lastQuote > firstQuote) {
                return responseBody.substring(firstQuote + 1, lastQuote)
                        .replace("\\n", System.lineSeparator())
                        .replace("\\\"", "\"")
                        .replace("\\\\", "\\");
            }

            return responseBody;
        } catch (Exception e) {
            return "RAG answer failed: " + e.getMessage();
        }
    }
}