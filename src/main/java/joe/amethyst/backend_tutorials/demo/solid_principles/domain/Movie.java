package joe.amethyst.backend_tutorials.demo.solid_principles.domain;

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
public class Movie {
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

}
