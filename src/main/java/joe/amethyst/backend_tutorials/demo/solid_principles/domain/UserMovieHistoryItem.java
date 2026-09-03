package joe.amethyst.backend_tutorials.demo.solid_principles.domain;

import java.time.Instant;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "user_movie_history")
public class UserMovieHistoryItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;
    private String movieTitle;
    private String genre;
    private String watchUrl;

    @Enumerated(EnumType.STRING)
    private UserMovieHistoryStatus status;

    private Instant createdAt;
    private Instant lastWatchedAt;
    private Instant archivedAt;

    public UserMovieHistoryItem(Long userId, String movieTitle, String genre, String watchUrl, UserMovieHistoryStatus status) {
        this.userId = userId;
        this.movieTitle = movieTitle;
        this.genre = genre;
        this.watchUrl = watchUrl;
        this.status = status;
        this.createdAt = Instant.now();
        this.lastWatchedAt = Instant.now();
    }
}
