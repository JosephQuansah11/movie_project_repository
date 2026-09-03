package joe.amethyst.backend_tutorials.demo.solid_principles.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import joe.amethyst.backend_tutorials.demo.solid_principles.domain.User;
import joe.amethyst.backend_tutorials.demo.solid_principles.domain.UserRegistrationRequest;
import joe.amethyst.backend_tutorials.demo.solid_principles.service.manager.UserService;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    public ResponseEntity<User> currentUser(@AuthenticationPrincipal Jwt jwt) {
        if (jwt == null || jwt.getSubject() == null || jwt.getSubject().isBlank()) {
            return ResponseEntity.status(401).build();
        }

        String username = jwt.getClaimAsString("preferred_username");
        String email = jwt.getClaimAsString("email");
        return ResponseEntity.ok(userService.findOrCreate(jwt.getSubject(), username, email));
    }

    @GetMapping("/csrf")
    public CsrfToken csrf(CsrfToken token) {
        return token;
    }

    @PostMapping
    public ResponseEntity<User> register(@Valid @RequestBody UserRegistrationRequest request) {
        return ResponseEntity.ok(userService.register(request));
    }
}