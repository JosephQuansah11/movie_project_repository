package joe.amethyst.backend_tutorials.demo.solid_principles.service.manager;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import joe.amethyst.backend_tutorials.demo.solid_principles.domain.MovieAssistantAiResponse;
import joe.amethyst.backend_tutorials.demo.solid_principles.utilities.OllamaManager;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class MovieAssistantAiService {
    private static final List<String> ALLOWED_HOSTS = List.of("kisskh");
    private final AiService aiService;
    private final OllamaManager ollamaManager;

    public MovieAssistantAiResponse processRequest(String userRequest) {
        String request = userRequest == null ? "" : userRequest.trim();

        List<String> recommendations = buildRecommendations(request);
        List<String> watchLinks = new ArrayList<>();
        String followUpQuestions = askAIforFollowUpQuestions(request);

        StringBuilder replyBuilder = new StringBuilder();
        replyBuilder.append("I found a few movies you might enjoy, and I included free watch links where available:\n\n");

        for (int i = 0; i < recommendations.size(); i++) {
            String title = recommendations.get(i);
            String link = askAIforWatchLinks(title);
            if (isSafeWatchLink(link)) {
                watchLinks.add(link);
                replyBuilder.append(i + 1).append(". ").append(title).append(" - Watch here: ").append(link).append("\n");
            } else {
                replyBuilder.append(i + 1).append(". ").append(title).append(" - No safe watch link available.\n");
            }
        }

        if (watchLinks.isEmpty()) {
            replyBuilder.append("No safe watch links are available at the moment. Please try another request.");
        }

        String aiReply = ollamaManager.generateCombinedResponse(List.of("codeqwen:latest"), request);
        if (aiReply != null && !aiReply.isBlank()) {
            replyBuilder.append("AI suggestion:").append(aiReply);
        }

        return new MovieAssistantAiResponse(replyBuilder.toString(), recommendations, watchLinks, followUpQuestions);
    }

    public boolean isSafeWatchLink(String url) {
        if (url == null || url.isBlank()) {
            return false;
        }

        String normalizedUrl = url.trim();
        if (normalizedUrl.toLowerCase().startsWith("javascript:")
                || normalizedUrl.toLowerCase().startsWith("data:")
                || normalizedUrl.toLowerCase().startsWith("vbscript:")) {
            return false;
        }

        try {
            URI uri = new URI(normalizedUrl);
            String scheme = uri.getScheme();
            String host = uri.getHost();

            if (scheme == null || !scheme.equalsIgnoreCase("https")) {
                return false;
            }

            if (host == null || host.isBlank()) {
                return false;
            }

                return ALLOWED_HOSTS.stream().anyMatch(host::equalsIgnoreCase)
                    || host.equalsIgnoreCase("kisskh.buzz")
                    || host.endsWith(".kisskh.buzz");
        } catch (URISyntaxException e) {
            return false;
        }
    }

    private List<String> buildRecommendations(String request) {
        if (request == null || request.isBlank()) {
            return List.of("Inception", "The Matrix", "The Dark Knight");
        }

        // String lowerRequest = request.toLowerCase();
        // if (lowerRequest.contains("romance") || lowerRequest.contains("love")) {
        //     return List.of("The Notebook", "La La Land", "Before Sunrise");
        // }
        // if (lowerRequest.contains("action") || lowerRequest.contains("adventure")) {
        //     return List.of("Mad Max: Fury Road", "The Matrix", "John Wick");
        // }
        // if (lowerRequest.contains("scary") || lowerRequest.contains("horror") || lowerRequest.contains("thriller")) {
        //     return List.of("Get Out", "The Conjuring", "Parasite");
        // }
        // if (lowerRequest.contains("comedy") || lowerRequest.contains("fun")) {
        //     return List.of("The Hangover", "Superbad", "Crazy Rich Asians");
        // }

        return askAIforRecommendations(request);
    }

    public List<String> askAIforRecommendations(String userPrompt) {
        return aiService.getRecommendations("provide user recommendations for: " + userPrompt);
    }

    public String askAIforWatchLinks(String userPrompt) {
        return aiService.getWatchLinks("provide watch links for: " + userPrompt +" from kisskh");
    }

    public String askAIforFollowUpQuestions(String userPrompt) {
        return aiService.getFollowUpQuestions("provide follow-up questions for: " + userPrompt);
    }
}

