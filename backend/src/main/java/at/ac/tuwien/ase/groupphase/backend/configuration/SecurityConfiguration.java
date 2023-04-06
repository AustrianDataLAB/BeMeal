package at.ac.tuwien.ase.groupphase.backend.configuration;

import at.ac.tuwien.ase.groupphase.backend.security.BeMealUserDetailsService;
import at.ac.tuwien.ase.groupphase.backend.security.JwtAuthenticationFilter;
import at.ac.tuwien.ase.groupphase.backend.security.JwtAuthorizationFilter;
import at.ac.tuwien.ase.groupphase.backend.security.TokenManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableMethodSecurity
public class SecurityConfiguration {

    private final UserDetailsService userDetailsService;

    @Autowired
    public SecurityConfiguration(final BeMealUserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    private static final String[] AUTH_WHITELIST = { "/error", "/api/v1/self-service/registration/participant",
            "/v3/api-docs/**", "/v3/api-docs.yaml", "/swagger-ui/**", "/swagger-ui.html" };

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(final HttpSecurity http,
            final JwtAuthenticationFilter authenticationFilter, final JwtAuthorizationFilter authorizationFilter)
            throws Exception {
        return http.csrf().disable().headers().frameOptions().disable().and().addFilter(authenticationFilter)
                .addFilter(authorizationFilter).sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS).and().authorizeHttpRequests()
                .requestMatchers(AUTH_WHITELIST).permitAll().anyRequest().authenticated().and().build();
    }

    @Bean
    public AuthenticationManager authenticationManager(final AuthenticationConfiguration authenticationConfiguration)
            throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public JwtAuthorizationFilter authorizationFilter(final AuthenticationManager authenticationManager) {
        return new JwtAuthorizationFilter(authenticationManager, tokenManager());
    }

    @Bean
    public JwtAuthenticationFilter authenticationFilter(final AuthenticationManager authenticationManager) {
        return new JwtAuthenticationFilter(authenticationManager, tokenManager());
    }

    private TokenManager tokenManager() {
        return new TokenManager(this.userDetailsService);
    }
}
