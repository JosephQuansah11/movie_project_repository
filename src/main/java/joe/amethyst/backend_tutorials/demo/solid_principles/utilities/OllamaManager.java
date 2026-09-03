package joe.amethyst.backend_tutorials.demo.solid_principles.utilities;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class OllamaManager {
    private static final Duration CACHE_TTL = Duration.ofMinutes(30);
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private final HttpClient httpClient;
    private final String baseUrl;
    private final ExecutorService executorService;
    private final Map<String, CacheEntry> responseCache = new ConcurrentHashMap<>();

    public OllamaManager() {
        this(HttpClient.newHttpClient(), AiModel.DEFAULT_OLLAMA_URL, Executors.newFixedThreadPool(4));
    }

    public OllamaManager(HttpClient httpClient, String baseUrl) {
        this(httpClient, baseUrl, Executors.newFixedThreadPool(4));
    }

    public OllamaManager(HttpClient httpClient, String baseUrl, ExecutorService executorService) {
        this.httpClient = httpClient;
        this.baseUrl = baseUrl;
        this.executorService = executorService;
    }

    public String generateResponse(String prompt) {
        return generateResponse("codeqwen:latest", prompt);
    }

    public String generateResponse(String model, String prompt) {
        String cleanModel = safeModel(model);
        String cleanPrompt = prompt == null ? "" : prompt.trim();
        String cacheKey = cleanModel + "::" + cleanPrompt;

        CacheEntry cached = responseCache.get(cacheKey);
        if (cached != null && !cached.isExpired()) {
            return cached.value();
        }

        String response = sendChatRequest(cleanModel, cleanPrompt);
        responseCache.put(cacheKey, new CacheEntry(response, System.currentTimeMillis()));
        return response;
    }

    public List<String> generateResponses(List<String> selectedModels, String prompt) {
        if (selectedModels == null || selectedModels.isEmpty()) {
            return List.of();
        }

        List<String> validModels = new ArrayList<>();
        for (String model : selectedModels) {
            if (model != null && !model.isBlank()) {
                validModels.add(model.trim());
            }
        }

        if (validModels.isEmpty()) {
            return List.of();
        }

        List<CompletableFuture<String>> futures = new ArrayList<>();
        for (String model : validModels) {
            futures.add(CompletableFuture.supplyAsync(() -> generateResponse(model, prompt), executorService));
        }

        return futures.stream().map(CompletableFuture::join).toList();
    }

    public String generateCombinedResponse(List<String> selectedModels, String prompt) {
        List<String> models = selectedModels == null || selectedModels.isEmpty()
                ? List.of("codeqwen:latest")
                : selectedModels.stream().filter(Objects::nonNull).map(String::trim).filter(s -> !s.isBlank()).toList();

        if (models.isEmpty()) {
            return "No AI model available for processing.";
        }

        List<CompletableFuture<String>> futures = new ArrayList<>();
        for (String model : models) {
            futures.add(CompletableFuture.supplyAsync(() -> generateResponse(model, prompt), executorService));
        }

        return futures.stream().map(CompletableFuture::join).filter(Objects::nonNull).collect(java.util.stream.Collectors.joining("\n\n---\n\n"));
    }

    private String sendChatRequest(String model, String prompt) {
        try {
            String escapedModel = escapeJson(model);
            String escapedPrompt = escapeJson(prompt);
            String body = "{\"model\":\"" + escapedModel + "\",\"messages\":[{\"role\":\"user\",\"content\":\"" + escapedPrompt + "\"}],\"stream\":false}";

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(baseUrl + "/api/chat"))
                    .header("Content-Type", "application/json")
                    .header("Accept", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(body))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() >= 400) {
                return "Ollama request failed with status " + response.statusCode() + ": " + response.body();
            }

            String responseBody = response.body();
            if (responseBody == null || responseBody.isBlank()) {
                return "No response from Ollama model: " + model;
            }

            return extractMessageContent(responseBody, model);
        } catch (Exception ex) {
            return "Ollama is not available at " + baseUrl + " for model " + model + ": " + ex.getMessage();
        }
    }

    private String escapeJson(String input) {
        if (input == null) {
            return "";
        }

        StringBuilder output = new StringBuilder();
        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            switch (c) {
                case '"':
                    output.append("\\\"");
                    break;
                case '\\':
                    output.append("\\\\");
                    break;
                case '\b':
                    output.append("\\b");
                    break;
                case '\f':
                    output.append("\\f");
                    break;
                case '\n':
                    output.append("\\n");
                    break;
                case '\r':
                    output.append("\\r");
                    break;
                case '\t':
                    output.append("\\t");
                    break;
                default:
                    if (c < 32 || c >= 127) {
                        output.append(String.format("\\u%04x", (int) c));
                    } else {
                        output.append(c);
                    }
            }
        }
        return output.toString();
    }

    private String extractMessageContent(String jsonBody, String model) {
        if (jsonBody == null || jsonBody.isBlank()) {
            return "No response from model " + model;
        }

        try {
            JsonNode content = OBJECT_MAPPER.readTree(jsonBody).path("message").path("content");
            if (content.isTextual()) {
                return content.textValue();
            }
        } catch (Exception ignored) {
            // Return the raw response when the provider sends non-JSON output.
        }

        return jsonBody;
    }

    private String safeModel(String model) {
        if (model == null || model.isBlank()) {
            return "codeqwen:latest";
        }
        return model.trim();
    }

    private record CacheEntry(String value, long createdAt) {
        boolean isExpired() {
            return System.currentTimeMillis() - createdAt > CACHE_TTL.toMillis();
        }
    }
}
