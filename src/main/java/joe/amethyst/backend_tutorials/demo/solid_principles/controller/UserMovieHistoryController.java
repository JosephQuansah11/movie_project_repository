package joe.amethyst.backend_tutorials.demo.solid_principles.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import joe.amethyst.backend_tutorials.demo.solid_principles.domain.User;
import joe.amethyst.backend_tutorials.demo.solid_principles.domain.UserMovieHistoryItem;
import joe.amethyst.backend_tutorials.demo.solid_principles.domain.UserMovieHistoryResponse;
import joe.amethyst.backend_tutorials.demo.solid_principles.service.manager.UserMovieHistoryService;
import joe.amethyst.backend_tutorials.demo.solid_principles.service.manager.UserService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserMovieHistoryController {

    private final UserMovieHistoryService userMovieHistoryService;
    private final UserService userService;

    @GetMapping("/me/history")
    public ResponseEntity<UserMovieHistoryResponse> getMyHistory(@AuthenticationPrincipal Jwt jwt) {
        Long userId = resolveCurrentUserId(jwt);
        return ResponseEntity.ok(UserMovieHistoryResponse.success(userId, "User history retrieved successfully.", userMovieHistoryService.getHistory(userId)));
    }

    @PostMapping("/me/history/watched")
    public ResponseEntity<UserMovieHistoryResponse> addWatchedMovieForCurrentUser(
            @AuthenticationPrincipal Jwt jwt,
            @RequestParam String movieTitle,
            @RequestParam(required = false) String genre,
            @RequestParam(required = false) String watchUrl) {
        Long userId = resolveCurrentUserId(jwt);
        UserMovieHistoryItem item = userMovieHistoryService.addWatchedMovie(userId, movieTitle, genre, watchUrl);
        return ResponseEntity.ok(UserMovieHistoryResponse.success(userId, "Movie marked as watched.", List.of(item)));
    }

    @PostMapping("/me/history/to-watch")
    public ResponseEntity<UserMovieHistoryResponse> addToWatchMovieForCurrentUser(
            @AuthenticationPrincipal Jwt jwt,
            @RequestParam String movieTitle,
            @RequestParam(required = false) String genre,
            @RequestParam(required = false) String watchUrl) {
        Long userId = resolveCurrentUserId(jwt);
        UserMovieHistoryItem item = userMovieHistoryService.addToWatchMovie(userId, movieTitle, genre, watchUrl);
        return ResponseEntity.ok(UserMovieHistoryResponse.success(userId, "Movie added to watch list.", List.of(item)));
    }

    @PostMapping("/me/history/archive")
    public ResponseEntity<UserMovieHistoryResponse> archiveStaleMoviesForCurrentUser(@AuthenticationPrincipal Jwt jwt) {
        Long userId = resolveCurrentUserId(jwt);
        List<UserMovieHistoryItem> archived = userMovieHistoryService.archiveStaleToWatchMovies(userId);
        return ResponseEntity.ok(UserMovieHistoryResponse.archived(userId, "Stale watch list items archived successfully.", archived));
    }

    @DeleteMapping("/me/history/{historyId}")
    public ResponseEntity<UserMovieHistoryResponse> removeFromMyHistory(@AuthenticationPrincipal Jwt jwt, @PathVariable Long historyId) {
        Long userId = resolveCurrentUserId(jwt);
        userMovieHistoryService.removeFromHistory(userId, historyId);
        return ResponseEntity.ok(UserMovieHistoryResponse.deleted(userId, "Movie removed from history.", historyId));
    }

    @GetMapping("/{userId}/history")
    public ResponseEntity<List<UserMovieHistoryItem>> getHistory(@PathVariable Long userId) {
        return ResponseEntity.ok(userMovieHistoryService.getHistory(userId));
    }

    @PostMapping("/{userId}/history/watched")
    public ResponseEntity<UserMovieHistoryItem> addWatchedMovie(
            @PathVariable Long userId,
            @RequestParam String movieTitle,
            @RequestParam(required = false) String genre,
            @RequestParam(required = false) String watchUrl) {
        return ResponseEntity.ok(userMovieHistoryService.addWatchedMovie(userId, movieTitle, genre, watchUrl));
    }

    @PostMapping("/{userId}/history/to-watch")
    public ResponseEntity<UserMovieHistoryItem> addToWatchMovie(
            @PathVariable Long userId,
            @RequestParam String movieTitle,
            @RequestParam(required = false) String genre,
            @RequestParam(required = false) String watchUrl) {
        return ResponseEntity.ok(userMovieHistoryService.addToWatchMovie(userId, movieTitle, genre, watchUrl));
    }

    @PostMapping("/{userId}/history/archive")
    public ResponseEntity<List<UserMovieHistoryItem>> archiveStaleMovies(@PathVariable Long userId) {
        return ResponseEntity.ok(userMovieHistoryService.archiveStaleToWatchMovies(userId));
    }

    @DeleteMapping("/{userId}/history/{historyId}")
    public ResponseEntity<Void> removeFromHistory(@PathVariable Long userId, @PathVariable Long historyId) {
        userMovieHistoryService.removeFromHistory(userId, historyId);
        return ResponseEntity.noContent().build();
    }

    private Long resolveCurrentUserId(Jwt jwt) {
        if (jwt == null || jwt.getSubject() == null || jwt.getSubject().isBlank()) {
            throw new IllegalStateException("Authenticated user not found");
        }

        User currentUser = userService.getByKeycloakSubject(jwt.getSubject());
        if (currentUser == null || currentUser.getId() == null) {
            throw new IllegalStateException("User profile not synchronized with Keycloak");
        }
        return currentUser.getId();
    }
}
