package joe.amethyst.backend_tutorials.demo.solid_principles.domain;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MovieAssistantAiResponse {
    private String reply;
    private List<String> recommendations = new ArrayList<>();
    private List<String> watchLinks = new ArrayList<>();
    private String followUpQuestions = "";
}
