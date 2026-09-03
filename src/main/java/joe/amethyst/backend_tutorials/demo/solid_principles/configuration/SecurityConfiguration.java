package joe.amethyst.backend_tutorials.demo.solid_principles.configuration;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableMethodSecurity
public class SecurityConfiguration {

	@Value("${FRONTEND_ORIGIN:http://localhost:5173}")
	private String frontendOrigin;

	@Bean
	SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http
			.cors(cors -> cors.configurationSource(corsConfigurationSource()))
			.csrf(csrf -> csrf.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse()))
			.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
			.authorizeHttpRequests(authorize -> authorize
				.requestMatchers(HttpMethod.GET, "/movies", "/movies/**").permitAll()
				.requestMatchers(HttpMethod.GET, "/users/csrf").permitAll()
				.requestMatchers(HttpMethod.GET, "/users/me").hasAnyRole("USER", "ADMIN")
				.requestMatchers(HttpMethod.GET, "/actuator/health").permitAll()
				.requestMatchers(HttpMethod.POST, "/users").permitAll()
				.requestMatchers(HttpMethod.POST, "/movies").hasRole("ADMIN")
				.requestMatchers(HttpMethod.PUT, "/movies/**").hasRole("ADMIN")
				.requestMatchers("/admin/**").hasRole("ADMIN")
				.requestMatchers("/users/me/**").hasAnyRole("USER", "ADMIN")
				.requestMatchers("/users/*/history/**", "/users/*/history").hasRole("ADMIN")
				.requestMatchers("/movie-assistant/**").hasAnyRole("USER", "ADMIN")
				.requestMatchers("/h2-console/**").permitAll()
				// .requestMatchers(HttpMethod.GET, "http://localhost:11434/api/chat").permitAll()
				.anyRequest().authenticated())
			.oauth2ResourceServer(oauth2 -> oauth2.jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter())));

		return http.build();
	}

	@Bean
	KeycloakJwtAuthenticationConverter jwtAuthenticationConverter() {
		return new KeycloakJwtAuthenticationConverter();
	}

	@Bean
	CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration configuration = new CorsConfiguration();
		configuration.setAllowedOrigins(List.of(frontendOrigin, "http://localhost:3000", "http://localhost:5173", "http://localhost:5174", "http://localhost:11434/api/chat"));
		configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
		configuration.setAllowedHeaders(List.of(HttpHeaders.AUTHORIZATION, HttpHeaders.CONTENT_TYPE, "X-XSRF-TOKEN"));
		configuration.setAllowCredentials(true);

		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", configuration);
		return source;
	}
}
