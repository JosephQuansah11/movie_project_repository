package joe.amethyst.backend_tutorials.demo.solid_principles.domain;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Movies extends ArrayList<Movie> {
    public Movies() {
        super();
    }

    public void addMovie(Movie movie){
        this.add(movie);
    } 

    public void removeMovie(String title){
        this.removeIf(movie -> movie.getTitle().equals(title));
    }

    public List<Movie> getAllMovies(){
        return this;
    }
    
}
