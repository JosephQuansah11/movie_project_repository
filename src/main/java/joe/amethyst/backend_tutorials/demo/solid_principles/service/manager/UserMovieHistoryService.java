package joe.amethyst.backend_tutorials.demo.solid_principles.service.manager;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import joe.amethyst.backend_tutorials.demo.solid_principles.domain.UserMovieHistoryItem;
import joe.amethyst.backend_tutorials.demo.solid_principles.domain.UserMovieHistoryStatus;
import joe.amethyst.backend_tutorials.demo.solid_principles.repository.manager.UserMovieHistoryRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class UserMovieHistoryService {

    private final UserMovieHistoryRepository historyRepository;

    public UserMovieHistoryItem addWatchedMovie(Long userId, String movieTitle, String genre, String watchUrl) {
        return saveOrUpdate(userId, movieTitle, genre, watchUrl, UserMovieHistoryStatus.WATCHED);
    }

    public UserMovieHistoryItem addToWatchMovie(Long userId, String movieTitle, String genre, String watchUrl) {
        return saveOrUpdate(userId, movieTitle, genre, watchUrl, UserMovieHistoryStatus.TO_WATCH);
    }

    public List<UserMovieHistoryItem> getHistory(Long userId) {
        return historyRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    public List<UserMovieHistoryItem> getWatchedMovies(Long userId) {
        return historyRepository.findByUserIdAndStatusOrderByCreatedAtDesc(userId, UserMovieHistoryStatus.WATCHED);
    }

    public List<UserMovieHistoryItem> getToWatchMovies(Long userId) {
        return historyRepository.findByUserIdAndStatusOrderByCreatedAtDesc(userId, UserMovieHistoryStatus.TO_WATCH);
    }

    public List<UserMovieHistoryItem> archiveStaleToWatchMovies(Long userId) {
        Instant cutoff = ZonedDateTime.now(ZoneOffset.UTC).minusMonths(2).toInstant();
        List<UserMovieHistoryItem> stale = historyRepository.findByUserIdAndStatusAndLastWatchedAtBefore(userId, UserMovieHistoryStatus.TO_WATCH, cutoff);

        List<UserMovieHistoryItem> archived = new ArrayList<>();
        for (UserMovieHistoryItem item : stale) {
            item.setStatus(UserMovieHistoryStatus.ARCHIVED);
            item.setArchivedAt(Instant.now());
            archived.add(historyRepository.save(item));
        }
        return archived;
    }

    public void removeFromHistory(Long userId, Long historyId) {
        historyRepository.findById(historyId)
                .filter(item -> item.getUserId().equals(userId))
                .ifPresent(historyRepository::delete);
    }

    public void markMovieWatched(Long userId, String movieTitle) {
        historyRepository.findByUserIdAndMovieTitle(userId, movieTitle)
                .ifPresent(item -> {
                    item.setStatus(UserMovieHistoryStatus.WATCHED);
                    item.setLastWatchedAt(Instant.now());
                    historyRepository.save(item);
                });
    }

    private UserMovieHistoryItem saveOrUpdate(Long userId, String movieTitle, String genre, String watchUrl, UserMovieHistoryStatus status) {
        if (movieTitle == null || movieTitle.isBlank()) {
            throw new IllegalArgumentException("Movie title is required");
        }

        return historyRepository.findByUserIdAndMovieTitle(userId, movieTitle)
                .map(existing -> {
                    existing.setGenre(genre);
                    existing.setWatchUrl(watchUrl);
                    existing.setStatus(status);
                    existing.setLastWatchedAt(Instant.now());
                    return historyRepository.save(existing);
                })
                .orElseGet(() -> historyRepository.save(new UserMovieHistoryItem(userId, movieTitle, genre, watchUrl, status)));
    }
}
