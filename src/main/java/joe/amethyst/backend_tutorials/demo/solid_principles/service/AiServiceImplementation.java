package joe.amethyst.backend_tutorials.demo.solid_principles.service;

import java.util.List;

import org.springframework.stereotype.Component;

import joe.amethyst.backend_tutorials.demo.solid_principles.service.manager.AiService;
import joe.amethyst.backend_tutorials.demo.solid_principles.utilities.AiModel;
import joe.amethyst.backend_tutorials.demo.solid_principles.utilities.OllamaManager;

@Component
public class AiServiceImplementation implements AiService {
    private final AiModel aiModel;
    private final OllamaManager ollamaManager;

    public AiServiceImplementation(OllamaManager ollamaManager) {
        this.aiModel = new AiModel();
        this.aiModel.configure(List.of("codeqwen:latest", "", "mistral:latest", "deepseek-r1:latest"));
        this.aiModel.setSelectedModels(List.of("codeqwen:latest", ""));
        this.ollamaManager = ollamaManager;
    }

    @Override
    public String processRequest(String userRequest) {
        String request = userRequest == null ? "" : userRequest.trim();
        if (request.isBlank()) {
            return "Please provide a movie request.";
        }
        return ollamaManager.generateCombinedResponse(aiModel.getSelectedModels(), request);
    }

    @Override
    public List<String> getRecommendations(String userPrompt) {
        String prompt = userPrompt == null ? "" : userPrompt.trim();
        return  List.of(ollamaManager.generateResponse(aiModel.getDefaultModel(), "Recommend movies for: " + prompt));
    }

    @Override
    public String getWatchLinks(String userPrompt) {
        String prompt = userPrompt == null ? "" : userPrompt.trim();
        return ollamaManager.generateResponse(aiModel.getDefaultModel(), "Provide free watch links for: " + prompt);
    }

    @Override
    public String getFollowUpQuestions(String userPrompt) {
        String prompt = userPrompt == null ? "" : userPrompt.trim();
        return ollamaManager.generateResponse(aiModel.getDefaultModel(), "Provide follow-up questions for: " + prompt);
    }


}

