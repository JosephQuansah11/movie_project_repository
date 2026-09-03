package joe.amethyst.backend_tutorials.demo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import joe.amethyst.backend_tutorials.demo.solid_principles.domain.Movie;
import joe.amethyst.backend_tutorials.demo.solid_principles.domain.MovieAssistantResponse;
import joe.amethyst.backend_tutorials.demo.solid_principles.domain.Movies;
import joe.amethyst.backend_tutorials.demo.solid_principles.service.manager.MovieService;

@SpringBootTest
class DemoApplicationTests {

	@Autowired
	private MovieService movieService;

	@Test
	void contextLoads() {
	}

	@Test
	void getMovieByTitleReturnsMatchingMovie() {
		Movies movies = movieService.getMovieByTitle("Inception");

		assertFalse(movies.isEmpty());
		assertEquals("Inception", movies.get(0).getTitle());
	}

	@Test
	void updateMovieReturnsUpdatedMovie() {
		Movie movie = new Movie();
		movie.setTitle("The Prestige");
		movie.setGenre("Mystery");
		movie.setLanguage("English");
		movie.setDirector("Christopher Nolan");
		movie.setDescription("Two magicians compete.");
		movie.setSeasonal(false);
		movie.setRating(8);
		movie.setReleaseYear(2006);
		movie.setPrice(9.99);
		movie.setDuration(130);
		movie.setPlayUrl("https://stream.example.com/the-prestige");
		movieService.addMovie(movie);

		Long movieId = movieService.getAllMovies().stream()
				.filter(existingMovie -> "The Prestige".equals(existingMovie.getTitle()))
				.findFirst()
				.map(Movie::getId)
				.orElseThrow(() -> new AssertionError("The new movie was not persisted"));

		Movie updatedMovie = movieService.updateMovie(movieId, movie);

		assertEquals("The Prestige", updatedMovie.getTitle());
		assertEquals("https://stream.example.com/the-prestige", updatedMovie.getPlayUrl());
	}

	@Test
	void assistantLookupFindsExistingMovieForWatching() {
		MovieAssistantResponse response = movieService.findMovieForWatching("Inception");

		assertFalse(response.getMovies().isEmpty());
		assertEquals(true, response.isMovieFound());
		assertEquals("Inception", response.getMovies().get(0).getTitle());
	}

	@Test
	void assistantChatAsksForMoreDetailsWhenTheRequestIsTooVague() {
		MovieAssistantResponse response = movieService.chatWithMovieAssistant("I want something fun");

		assertTrue(response.isNeedsMoreInfo());
		assertFalse(response.getFollowUpQuestions().isEmpty());
		assertFalse(response.getRecommendedMovies().isEmpty());
	}

}
