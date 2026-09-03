package joe.amethyst.backend_tutorials.demo;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import joe.amethyst.backend_tutorials.demo.solid_principles.domain.MovieAssistantAiResponse;
import joe.amethyst.backend_tutorials.demo.solid_principles.service.manager.MovieAssistantAiService;

@SpringBootTest
class MovieAssistantAiServiceTest {

    @Autowired
    private MovieAssistantAiService movieAssistantAiService;

    @Test
    void assistantCanRecommendMoviesAndWatchLinks() {
        MovieAssistantAiResponse response = movieAssistantAiService.processRequest("Find me a fun movie to watch for free");

        assertNotNull(response);
        assertFalse(response.getRecommendations().isEmpty());
        assertTrue(response.getWatchLinks().stream().allMatch(movieAssistantAiService::isSafeWatchLink));
    }

    @Test
    void onlyAllowsTrustedWatchLinks() {
        assertTrue(movieAssistantAiService.isSafeWatchLink("https://kisskh.buzz/2026/06/20/inception/?episode=6"));
        assertFalse(movieAssistantAiService.isSafeWatchLink("http://evil.com/steal"));
        assertFalse(movieAssistantAiService.isSafeWatchLink("javascript:alert(1)"));
    }
}
