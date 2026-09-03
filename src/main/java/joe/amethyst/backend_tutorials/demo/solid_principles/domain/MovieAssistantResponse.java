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
public class MovieAssistantResponse {
    private boolean movieFound;
    private String message;
    private Movies movies;
    private boolean needsMoreInfo;
    private List<String> followUpQuestions = new ArrayList<>();
    private Movies recommendedMovies = new Movies();

    public MovieAssistantResponse(boolean movieFound, String message, Movies movies) {
        this.movieFound = movieFound;
        this.message = message;
        this.movies = movies;
    }

    public boolean isMovieFound() {
        return movieFound;
    }
}
