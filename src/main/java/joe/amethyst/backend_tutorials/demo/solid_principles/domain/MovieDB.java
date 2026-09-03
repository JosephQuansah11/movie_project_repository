package joe.amethyst.backend_tutorials.demo.solid_principles.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Entity
public class MovieDB {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String genre;
    private String language;
    private String director;
    private String description;
    private boolean isSeasonal;
    private int rating;
    private int releaseYear;
    private double price;
    private double duration;
    private String playUrl;

    public MovieDB(String title, String genre, String language, String director, String description,
                 boolean isSeasonal, int rating, int releaseYear, double price, double duration) {
        this.title = title;
        this.genre = genre;
        this.language = language;
        this.director = director;
        this.description = description;
        this.isSeasonal = isSeasonal;
        this.rating = rating;
        this.releaseYear = releaseYear;
        this.price = price;
        this.duration = duration;
    }
}
