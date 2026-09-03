package joe.amethyst.backend_tutorials.demo.solid_principles.service;

import joe.amethyst.backend_tutorials.demo.solid_principles.domain.Role;
import joe.amethyst.backend_tutorials.demo.solid_principles.domain.User;
import joe.amethyst.backend_tutorials.demo.solid_principles.domain.UserRegistrationRequest;

public interface UserServiceImplementation {

    User register(UserRegistrationRequest request);

    User findOrCreate(String keycloakSubject, String username, String email);

    User getByKeycloakSubject(String keycloakSubject);

    User addRole(String keycloakSubject, Role role);

    User removeRole(String keycloakSubject, Role role);
}
