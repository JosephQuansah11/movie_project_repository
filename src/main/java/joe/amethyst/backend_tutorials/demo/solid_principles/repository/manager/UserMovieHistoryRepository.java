package joe.amethyst.backend_tutorials.demo.solid_principles.repository.manager;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import joe.amethyst.backend_tutorials.demo.solid_principles.domain.UserMovieHistoryItem;
import joe.amethyst.backend_tutorials.demo.solid_principles.domain.UserMovieHistoryStatus;

public interface UserMovieHistoryRepository extends JpaRepository<UserMovieHistoryItem, Long> {
    List<UserMovieHistoryItem> findByUserIdOrderByCreatedAtDesc(Long userId);
    List<UserMovieHistoryItem> findByUserIdAndStatusOrderByCreatedAtDesc(Long userId, UserMovieHistoryStatus status);
    Optional<UserMovieHistoryItem> findByUserIdAndMovieTitle(Long userId, String movieTitle);
    List<UserMovieHistoryItem> findByUserIdAndStatusAndLastWatchedAtBefore(Long userId, UserMovieHistoryStatus status, Instant cutoff);
}
