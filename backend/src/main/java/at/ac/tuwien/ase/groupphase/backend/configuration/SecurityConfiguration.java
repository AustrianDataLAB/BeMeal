package at.ac.tuwien.ase.groupphase.backend.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.scrypt.SCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfiguration {

    private static final String[] AUTH_WHITELIST = {"/api/v1/self-service/registration/participant", "/v3/api-docs/**",
            "/v3/api-docs.yaml", "/swagger-ui/**", "/swagger-ui.html"};

    @Bean
    public PasswordEncoder passwordEncoder() {
        return SCryptPasswordEncoder.defaultsForSpringSecurity_v5_8();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(final HttpSecurity http) throws Exception {
        return http.csrf().disable().authorizeHttpRequests().
                requestMatchers(AUTH_WHITELIST).permitAll().anyRequest().authenticated()
                .and().build();
    }
}
