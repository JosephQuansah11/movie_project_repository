package joe.amethyst.backend_tutorials.demo;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

import joe.amethyst.backend_tutorials.demo.solid_principles.service.DocumentRagService;

class DocumentRagServiceTest {

    @Test
    void ragServiceCanReadTextAndAnswerQuestionsFromDocument() {
        DocumentRagService ragService = new DocumentRagService();

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "movie-guide.txt",
                "text/plain",
                ("The Matrix is a 1999 science fiction film about Neo, a hacker who learns reality is a simulation. " +
                 "Inception is a 2010 film about dream layers and espionage. " +
                 "The Godfather is a crime classic about a powerful family.")
                        .getBytes());

        String answer = ragService.answerQuestion(file, "What is the movie The Matrix about?");

        assertNotNull(answer);
        assertTrue(answer.toLowerCase().contains("matrix") || answer.toLowerCase().contains("simulation") || answer.toLowerCase().contains("neo"));
    }
}
