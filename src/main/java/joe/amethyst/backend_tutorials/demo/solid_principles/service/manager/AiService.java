package joe.amethyst.backend_tutorials.demo.solid_principles.service.manager;

import java.util.List;

public interface AiService {
    List<String> getRecommendations(String userPrompt);
    String getWatchLinks(String userPrompt);
    String getFollowUpQuestions(String userPrompt);
    String processRequest(String userRequest);
}
