package joe.amethyst.backend_tutorials.demo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import joe.amethyst.backend_tutorials.demo.solid_principles.domain.UserMovieHistoryItem;
import joe.amethyst.backend_tutorials.demo.solid_principles.domain.UserMovieHistoryStatus;
import joe.amethyst.backend_tutorials.demo.solid_principles.repository.manager.UserMovieHistoryRepository;
import joe.amethyst.backend_tutorials.demo.solid_principles.service.manager.UserMovieHistoryService;

@SpringBootTest
class UserMovieHistoryServiceTest {

    @Autowired
    private UserMovieHistoryService userMovieHistoryService;

    @Autowired
    private UserMovieHistoryRepository repository;

    @Test
    void userCanTrackWatchedMoviesAndArchiveStaleOnes() {
        Long userId = 101L;

        UserMovieHistoryItem watched = userMovieHistoryService.addWatchedMovie(userId, "Inception", "Sci-Fi", "https://example.com/inception");
        UserMovieHistoryItem toWatch = userMovieHistoryService.addToWatchMovie(userId, "The Matrix", "Sci-Fi", "https://example.com/matrix");

        toWatch.setLastWatchedAt(Instant.now().minus(90, ChronoUnit.DAYS));
        repository.save(toWatch);

        List<UserMovieHistoryItem> history = userMovieHistoryService.getHistory(userId);
        assertEquals(2, history.size());
        assertTrue(history.stream().anyMatch(item -> item.getMovieTitle().equals("Inception")));

        List<UserMovieHistoryItem> archived = userMovieHistoryService.archiveStaleToWatchMovies(userId);
        assertFalse(archived.isEmpty());
        assertTrue(archived.stream().anyMatch(item -> item.getMovieTitle().equals("The Matrix")));
        assertTrue(repository.findById(toWatch.getId()).orElseThrow().getStatus() == UserMovieHistoryStatus.ARCHIVED);

        userMovieHistoryService.removeFromHistory(userId, watched.getId());
        assertFalse(userMovieHistoryService.getHistory(userId).stream().anyMatch(item -> item.getId().equals(watched.getId())));
    }
}
