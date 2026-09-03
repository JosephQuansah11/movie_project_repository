package joe.amethyst.backend_tutorials.demo.solid_principles.repository;

import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import joe.amethyst.backend_tutorials.demo.solid_principles.domain.Movie;
import joe.amethyst.backend_tutorials.demo.solid_principles.domain.MovieDB;
import joe.amethyst.backend_tutorials.demo.solid_principles.domain.MovieMapper;
import joe.amethyst.backend_tutorials.demo.solid_principles.domain.Movies;
import joe.amethyst.backend_tutorials.demo.solid_principles.repository.manager.MoviesRepositoryDB;
import joe.amethyst.backend_tutorials.demo.solid_principles.repository.manager.MoviesRepository;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class MoviesRepositoryImpl implements MoviesRepository {
    private final MoviesRepositoryDB moviesRepository;
    private final MovieMapper movieMapper;

    @Override
    public void initializeRepository() {
        if (moviesRepository.count() > 0) {
            return;
        }

        moviesRepository.save(new MovieDB("Inception", "Sci-Fi", "English", "Christopher Nolan", "A mind-bending thriller.", false, 9, 2010, 14.99, 148));
        moviesRepository.save(new MovieDB("The Dark Knight", "Action", "English", "Christopher Nolan", "Batman faces the Joker.", false, 9, 2008, 12.99, 152));
        moviesRepository.save(new MovieDB("Interstellar", "Sci-Fi", "English", "Christopher Nolan", "A journey through space and time.", false, 8, 2014, 15.99, 169));
        moviesRepository.save(new MovieDB("The Matrix", "Sci-Fi", "English", "The Wachowskis", "A hacker discovers reality is a simulation.", false, 9, 1999, 11.99, 136));
        moviesRepository.save(new MovieDB("Pulp Fiction", "Crime", "English", "Quentin Tarantino", "Interwoven stories of crime and redemption.", false, 9, 1994, 10.99, 154));
    }


    @Override
    public void removeMovie(Long id) {
        moviesRepository.deleteById(id);
    }

    @Override
    public Movies getMovieByTitle(String title) {
        return moviesRepository.findByTitle(title).stream()
                .map(movieMapper::toDomain)
                .collect(Collectors.toCollection(Movies::new));
    }

    @Override
    public Movies getMovieByDirector(String director) {
        return moviesRepository.findByDirector(director).stream()
                .map(movieMapper::toDomain)
                .collect(Collectors.toCollection(Movies::new));
    }

    @Override
    public Movies getMovieByGenre(String genre) {
        return moviesRepository.findByGenre(genre).stream()
                .map(movieMapper::toDomain)
                .collect(Collectors.toCollection(Movies::new));
    }

    @Override
    public Movies getMovieByDescription(String description) {
        return moviesRepository.findByDescription(description).stream()
                .map(movieMapper::toDomain)
                .collect(Collectors.toCollection(Movies::new));
    }

    @Override
    public void addMovie(Movie movie) {
        if (movie == null) {
            return;
        }
        moviesRepository.save(movieMapper.toEntity(movie));
    }

    @Override
    public Movie updateMovie(Long id, Movie movie) {
        if (movie == null || id == null) {
            return null;
        }

        Optional<MovieDB> existing = moviesRepository.findById(id);
        if (existing.isEmpty()) {
            return null;
        }

        MovieDB entity = existing.get();
        entity.setTitle(movie.getTitle() != null ? movie.getTitle() : entity.getTitle());
        entity.setGenre(movie.getGenre() != null ? movie.getGenre() : entity.getGenre());
        entity.setLanguage(movie.getLanguage() != null ? movie.getLanguage() : entity.getLanguage());
        entity.setDirector(movie.getDirector() != null ? movie.getDirector() : entity.getDirector());
        entity.setDescription(movie.getDescription() != null ? movie.getDescription() : entity.getDescription());
        entity.setSeasonal(movie.isSeasonal());
        entity.setRating(movie.getRating());
        entity.setReleaseYear(movie.getReleaseYear());
        entity.setPrice(movie.getPrice());
        entity.setDuration(movie.getDuration());
        entity.setPlayUrl(movie.getPlayUrl() != null ? movie.getPlayUrl() : entity.getPlayUrl());

        return movieMapper.toDomain(moviesRepository.save(entity));
    }

    @Override
    public Movies searchMovieByTitle(String title) {
        return moviesRepository.findByTitle(title).stream()
                .map(movieMapper::toDomain)
                .collect(Collectors.toCollection(Movies::new));
    }

    @Override
    public Movies searchMovieByDirector(String director) {
        return moviesRepository.findByDirector(director).stream()
                .map(movieMapper::toDomain)
                .collect(Collectors.toCollection(Movies::new));
    }

    @Override
    public Movies searchMovieByGenre(String genre) {
        return moviesRepository.findByGenre(genre).stream()
                .map(movieMapper::toDomain)
                .collect(Collectors.toCollection(Movies::new));
    }

    @Override
    public Movies searchMovieByDescription(String description) {
      return moviesRepository.findByDescription(description).stream()
                .map(movieMapper::toDomain)
                .collect(Collectors.toCollection(Movies::new));
    }

    @Override
    public Movies searchMovieByYear(int releaseYear) {
        return moviesRepository.findByReleaseYear(releaseYear).stream()
                .map(movieMapper::toDomain)
                .collect(Collectors.toCollection(Movies::new));
    }

    @Override
    public Movies filterMovieByRating(int rating) {
       return moviesRepository.findAll()
                .stream()
                .map(movieMapper::toDomain)
                .filter(movie -> movie.getRating() == rating)
                .collect(Collectors.toCollection(Movies::new));
    }

    @Override
    public Movies filterMovieByPrice(double price) {
        return moviesRepository.findAll()
                .stream()
                .map(movieMapper::toDomain)
                .filter(movie -> movie.getPrice() == price)
                .collect(Collectors.toCollection(Movies::new));
    }

    @Override
    public Movies filterMovieByDuration(double duration) {
        return moviesRepository.findAll()
                .stream()
                .map(movieMapper::toDomain)
                .filter(movie -> movie.getDuration() == duration)
                .collect(Collectors.toCollection(Movies::new));
    }

    @Override
    public Movies filterMovieByLanguage(String language) {
        return moviesRepository.findAll()
                .stream()
                .map(movieMapper::toDomain)
                    .filter(movie -> movie.getLanguage().equals(language))
                    .collect(Collectors.toCollection(Movies::new));
    }

    @Override
    public Movies filterMovieBySeasonal(boolean isSeasonal) {
        return moviesRepository.findAll()
                .stream()
                .map(movieMapper::toDomain)
                .filter(movie -> movie.isSeasonal() == isSeasonal)
                .collect(Collectors.toCollection(Movies::new));
    }


    @Override
    public Movies getAllMovies() {
        return moviesRepository.findAll()
                .stream()
                .map(movieMapper::toDomain)
                .collect(Collectors.toCollection(Movies::new));
    }

}
