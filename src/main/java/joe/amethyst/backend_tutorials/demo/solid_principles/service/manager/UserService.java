package joe.amethyst.backend_tutorials.demo.solid_principles.service.manager;

import java.util.List;

import org.keycloak.admin.client.CreatedResponseUtil;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.ws.rs.core.Response;
import joe.amethyst.backend_tutorials.demo.solid_principles.domain.Role;
import joe.amethyst.backend_tutorials.demo.solid_principles.domain.User;
import joe.amethyst.backend_tutorials.demo.solid_principles.domain.UserRegistrationRequest;
import joe.amethyst.backend_tutorials.demo.solid_principles.repository.manager.UsersRepositoryDB;
import joe.amethyst.backend_tutorials.demo.solid_principles.service.UserServiceImplementation;

@Service
@Transactional
public class UserService implements UserServiceImplementation {

    private final UsersRepositoryDB usersRepository;
    private final Keycloak keycloakAdminClient;
    private final String keycloakRealm;

    public UserService(
            UsersRepositoryDB usersRepository,
            Keycloak keycloakAdminClient,
            @Value("${keycloak.realm}") String keycloakRealm) {
        this.usersRepository = usersRepository;
        this.keycloakAdminClient = keycloakAdminClient;
        this.keycloakRealm = keycloakRealm;
    }

    @Override
    public User register(UserRegistrationRequest request) {
        UserRepresentation representation = new UserRepresentation();
        representation.setUsername(request.getUsername());
        representation.setEmail(request.getEmail());
        representation.setFirstName(request.getFirstName());
        representation.setLastName(request.getLastName());
        representation.setEmailVerified(true);
        representation.setEnabled(true);

        CredentialRepresentation credential = new CredentialRepresentation();
        credential.setType(CredentialRepresentation.PASSWORD);
        credential.setValue(request.getPassword());
        credential.setTemporary(false);
        representation.setCredentials(List.of(credential));

        String keycloakSubject;
        try (Response response = keycloakAdminClient.realm(keycloakRealm).users().create(representation)) {
            if (response.getStatus() != Response.Status.CREATED.getStatusCode()) {
                throw new IllegalStateException("Keycloak user creation failed with status " + response.getStatus());
            }
            keycloakSubject = CreatedResponseUtil.getCreatedId(response);
        }

        try {
                User user = new User(keycloakSubject, request.getUsername(), request.getEmail(),
                    request.getFirstName(), request.getLastName());
            assignKeycloakRole(keycloakSubject, Role.USER);
            return usersRepository.save(user);
        } catch (RuntimeException exception) {
            keycloakAdminClient.realm(keycloakRealm).users().delete(keycloakSubject);
            throw exception;
        }
    }

    @Override
    public User findOrCreate(String keycloakSubject, String username, String email) {
        User user = usersRepository.findByKeycloakSubject(keycloakSubject)
                .orElseGet(() -> new User(keycloakSubject, username, email));

        user.setUsername(username);
        user.setEmail(email);
        return usersRepository.save(user);
    }

    @Override
    @Transactional(readOnly = true)
    public User getByKeycloakSubject(String keycloakSubject) {
        return usersRepository.findByKeycloakSubject(keycloakSubject).orElse(null);
    }

    @Override
    public User addRole(String keycloakSubject, Role role) {
        User user = requireUser(keycloakSubject);
        assignKeycloakRole(keycloakSubject, role);
        user.getRoles().add(role);
        return usersRepository.save(user);
    }

    @Override
    public User removeRole(String keycloakSubject, Role role) {
        User user = requireUser(keycloakSubject);
        keycloakAdminClient.realm(keycloakRealm).users().get(keycloakSubject).roles().realmLevel()
                .remove(java.util.List.of(getKeycloakRole(role)));
        user.getRoles().remove(role);
        return usersRepository.save(user);
    }

    private void assignKeycloakRole(String keycloakSubject, Role role) {
        keycloakAdminClient.realm(keycloakRealm).users().get(keycloakSubject).roles().realmLevel()
                .add(java.util.List.of(getKeycloakRole(role)));
    }

    private RoleRepresentation getKeycloakRole(Role role) {
        try {
            return keycloakAdminClient.realm(keycloakRealm).roles().get(role.name()).toRepresentation();
        } catch (RuntimeException exception) {
            RoleRepresentation representation = new RoleRepresentation();
            representation.setName(role.name());
            representation.setDescription("Application role " + role.name());
            try {
                keycloakAdminClient.realm(keycloakRealm).roles().create(representation);
            } catch (jakarta.ws.rs.WebApplicationException roleCreationException) {
                if (roleCreationException.getResponse().getStatus() != Response.Status.CONFLICT.getStatusCode()) {
                    throw roleCreationException;
                }
            }
            return keycloakAdminClient.realm(keycloakRealm).roles().get(role.name()).toRepresentation();
        }
    }

    private User requireUser(String keycloakSubject) {
        return usersRepository.findByKeycloakSubject(keycloakSubject)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + keycloakSubject));
    }
}
