package joe.amethyst.backend_tutorials.demo.solid_principles.service.manager;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import joe.amethyst.backend_tutorials.demo.solid_principles.domain.Movie;
import joe.amethyst.backend_tutorials.demo.solid_principles.domain.MovieAssistantResponse;
import joe.amethyst.backend_tutorials.demo.solid_principles.domain.Movies;
import joe.amethyst.backend_tutorials.demo.solid_principles.repository.manager.MoviesRepository;
import joe.amethyst.backend_tutorials.demo.solid_principles.service.MovieServiceImplementation;


@Service
public class MovieService implements MovieServiceImplementation {
    // we need to have attributes for dependency injection, for example, a repository to manage movies
    // first let's create a repository interface for movies, then we can inject it here and use it to implement the methods
    private final MoviesRepository movieRepository;

    public MovieService(MoviesRepository movieRepository) {
        this.movieRepository = movieRepository;
        this.movieRepository.initializeRepository();
    }

    @Override
    public void addMovie(Movie movie) {
        this.movieRepository.addMovie(movie);
    }

    @Override
    public Movie updateMovie(Long id, Movie movie) {
        return this.movieRepository.updateMovie(id, movie);
    }

    @Override
    public Movies getAllMovies() {
        // Implementation for retrieving all movies
        return movieRepository.getAllMovies();
    }

    @Override
    public Movies getMovieByTitle(String title) {
        // Implementation for retrieving a movie by title
        return this.movieRepository.getMovieByTitle(title);
    }   

    @Override
    public Movies getMovieByDirector(String director) {
        // Implementation for retrieving a movie by director
        return this.movieRepository.getMovieByDirector(director);
    }

    @Override
    public Movies getMovieByGenre(String genre) {
        // Implementation for retrieving a movie by genre
        return this.movieRepository.getMovieByGenre(genre);
    }

    @Override
    public Movies getMovieByDescription(String description) {
        // Implementation for retrieving a movie by description
        return this.movieRepository.getMovieByDescription(description);
    }

    @Override
    public void removeMovie(Long id) {
        movieRepository.removeMovie(id);
    }

    @Override
    public MovieAssistantResponse findMovieForWatching(String title) {
        Movies foundMovies = this.movieRepository.getMovieByTitle(title);
        if (foundMovies == null || foundMovies.isEmpty()) {
            return new MovieAssistantResponse(false, "No movie found in the backend for this title.", new Movies());
        }

        Movie matchingMovie = foundMovies.get(0);
        String message = matchingMovie.getPlayUrl() != null && !matchingMovie.getPlayUrl().isBlank()
                ? "Movie exists and is ready to play online."
                : "Movie exists in the backend, but no play URL has been configured yet.";
        return new MovieAssistantResponse(true, message, foundMovies);
    }

    @Override
    public MovieAssistantResponse chatWithMovieAssistant(String userRequest) {
        if (userRequest == null || userRequest.isBlank()) {
            return buildNeedsMoreInfoResponse("I can help you find a movie to watch. What kind of movie are you in the mood for?");
        }

        String request = userRequest.toLowerCase();
        if (request.contains("genre") || request.contains("director") || request.contains("language") || request.contains("year") || request.contains("rating") || request.contains("season")) {
            Movies filteredMovies = movieRepository.getAllMovies();
            MovieAssistantResponse response = new MovieAssistantResponse();
            response.setMovieFound(!filteredMovies.isEmpty());
            response.setMessage("I found some movies that match your request.");
            response.setMovies(filteredMovies);
            response.setRecommendedMovies(filteredMovies);
            return response;
        }

        if (request.contains("title") || request.contains("movie") || request.contains("watch") || request.contains("play") || request.contains("recommend")) {
            Movies allMovies = movieRepository.getAllMovies();
            if (allMovies == null || allMovies.isEmpty()) {
                return buildNeedsMoreInfoResponse("I could not find any movies in the backend right now. Please add some first.");
            }

            String titleHint = extractTitleHint(userRequest);
            if (titleHint != null) {
                Movies foundMovies = movieRepository.getMovieByTitle(titleHint);
                if (!foundMovies.isEmpty()) {
                    MovieAssistantResponse response = new MovieAssistantResponse();
                    response.setMovieFound(true);
                    response.setMessage("I found a movie that matches your request.");
                    response.setMovies(foundMovies);
                    response.setRecommendedMovies(foundMovies);
                    return response;
                }
            }

            MovieAssistantResponse response = new MovieAssistantResponse();
            response.setNeedsMoreInfo(true);
            response.setMessage("I can recommend a movie, but I need a little more detail. Here are a few options to start with:");
            response.setFollowUpQuestions(List.of(
                    "Do you want a comedy, action, or sci-fi movie?",
                    "Would you prefer a recent movie or an older classic?",
                    "Do you want something short or something longer?"
            ));
            response.setRecommendedMovies(allMovies);
            return response;
        }

        return buildNeedsMoreInfoResponse("I can help you find a movie to watch. What genre, title, or vibe are you looking for?");
    }

    private MovieAssistantResponse buildNeedsMoreInfoResponse(String message) {
        MovieAssistantResponse response = new MovieAssistantResponse();
        response.setNeedsMoreInfo(true);
        response.setMessage(message);
        response.setFollowUpQuestions(List.of(
                "What genre do you want?",
                "Do you want something funny, dramatic, or action-packed?",
                "Do you have a movie title in mind?"
        ));
        response.setRecommendedMovies(movieRepository.getAllMovies());
        return response;
    }

    private String extractTitleHint(String userRequest) {
        String[] tokens = userRequest.split("\\s+");
        List<String> titleTokens = new ArrayList<>();
        for (String token : tokens) {
            if (!token.contains("movie") && !token.contains("watch") && !token.contains("play") && !token.contains("recommend") && !token.contains("want") && !token.contains("something") && !token.contains("for") && !token.contains("the") && !token.contains("a") && !token.contains("an") && !token.contains("i")) {
                titleTokens.add(token);
            }
        }
        return titleTokens.isEmpty() ? null : String.join(" ", titleTokens);
    }
}
