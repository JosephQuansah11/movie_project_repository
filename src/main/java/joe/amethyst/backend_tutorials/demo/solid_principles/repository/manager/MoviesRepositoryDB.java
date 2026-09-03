package joe.amethyst.backend_tutorials.demo.solid_principles.repository.manager;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import joe.amethyst.backend_tutorials.demo.solid_principles.domain.MovieDB;

public interface MoviesRepositoryDB extends JpaRepository<MovieDB, Long> {

    List<MovieDB> findByTitle(String title);
    // Define custom query methods if needed

    List<MovieDB> findByDirector(String director);

    List<MovieDB> findByGenre(String genre);

    List<MovieDB> findByDescription(String description);

    List<MovieDB> findByReleaseYear(int releaseYear);

}
