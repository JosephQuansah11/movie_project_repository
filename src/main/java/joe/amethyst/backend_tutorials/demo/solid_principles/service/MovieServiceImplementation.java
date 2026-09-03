package joe.amethyst.backend_tutorials.demo.solid_principles.service;

import joe.amethyst.backend_tutorials.demo.solid_principles.domain.Movie;
import joe.amethyst.backend_tutorials.demo.solid_principles.domain.MovieAssistantResponse;
import joe.amethyst.backend_tutorials.demo.solid_principles.domain.Movies;

public interface MovieServiceImplementation {
    void addMovie(Movie movie);
    Movie updateMovie(Long id, Movie movie);
    void removeMovie(Long id);
    Movies getAllMovies();
    Movies getMovieByTitle(String title);
    Movies getMovieByDirector(String director);
    Movies getMovieByGenre(String genre);
    Movies getMovieByDescription(String description);
    MovieAssistantResponse findMovieForWatching(String title);
    MovieAssistantResponse chatWithMovieAssistant(String userRequest);
}
