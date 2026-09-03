package joe.amethyst.backend_tutorials.demo.solid_principles.repository.manager;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import joe.amethyst.backend_tutorials.demo.solid_principles.domain.User;

public interface UsersRepositoryDB extends JpaRepository<User, Long> {

    Optional<User> findByKeycloakSubject(String keycloakSubject);
}
