package joe.amethyst.backend_tutorials.demo.solid_principles.repository.manager;

import joe.amethyst.backend_tutorials.demo.solid_principles.domain.Movie;
import joe.amethyst.backend_tutorials.demo.solid_principles.domain.Movies;

public interface MoviesRepository {

    Movies getAllMovies();

    Movies getMovieByTitle(String title);

    Movies getMovieByDirector(String director);

    Movies getMovieByGenre(String genre);

    Movies getMovieByDescription(String description);

    void addMovie(Movie movie);

    Movie updateMovie(Long id, Movie movie);

    void removeMovie(Long id);

    Movies searchMovieByTitle(String title);

    Movies searchMovieByDirector(String director);

    Movies searchMovieByGenre(String genre);

    Movies searchMovieByDescription(String description);

    Movies searchMovieByYear(int releaseYear);

    Movies filterMovieByRating(int rating);

    Movies filterMovieByPrice(double price);

    Movies filterMovieByDuration(double duration);

    Movies filterMovieByLanguage(String language);

    Movies filterMovieBySeasonal(boolean isSeasonal);

    void initializeRepository();

}
