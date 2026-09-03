package joe.amethyst.backend_tutorials.demo.solid_principles.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;

import joe.amethyst.backend_tutorials.demo.solid_principles.domain.Movie;
import joe.amethyst.backend_tutorials.demo.solid_principles.domain.MovieAssistantResponse;
import joe.amethyst.backend_tutorials.demo.solid_principles.domain.Movies;
import joe.amethyst.backend_tutorials.demo.solid_principles.service.manager.MovieService;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/movies")
@AllArgsConstructor
public class MovieController {
    // Controller methods for handling movie-related requests
    private final MovieService movieService;

    @GetMapping("")
    public ResponseEntity<Movies> getAllMovies() {
        return ResponseEntity.ok(movieService.getAllMovies());
    }

    @GetMapping("/title/{title}")
    public ResponseEntity<Movies> getMovieByTitle(@PathVariable("title") String title) {
        return ResponseEntity.ok(movieService.getMovieByTitle(title));
    }

    @GetMapping("/director/{director}")
    public ResponseEntity<Movies> getMovieByDirector(@PathVariable("director") String director) {
        return ResponseEntity.ok(movieService.getMovieByDirector(director));
    }

    @GetMapping("/genre/{genre}")
    public ResponseEntity<Movies> getMovieByGenre(@PathVariable("genre") String genre) {
        return ResponseEntity.ok(movieService.getMovieByGenre(genre));
    }

    @GetMapping("/description/{description}")
    public ResponseEntity<Movies> getMovieByDescription(@PathVariable String description) {
        return ResponseEntity.ok(movieService.getMovieByDescription(description));
    }

    @PostMapping("")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> addMovie(@RequestBody Movie movie) {
        movieService.addMovie(movie);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Movie> updateMovie(@PathVariable Long id, @RequestBody Movie movie) {
        Movie updatedMovie = movieService.updateMovie(id, movie);
        if (updatedMovie == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(updatedMovie);
    }

    @GetMapping("/assistant/{title}")
    public ResponseEntity<MovieAssistantResponse> findMovieForWatching(@PathVariable String title) {
        return ResponseEntity.ok(movieService.findMovieForWatching(title));
    }

    @PostMapping("/assistant")
    public ResponseEntity<MovieAssistantResponse> chatWithMovieAssistant(@RequestBody String userRequest) {
        return ResponseEntity.ok(movieService.chatWithMovieAssistant(userRequest));
    }

}
