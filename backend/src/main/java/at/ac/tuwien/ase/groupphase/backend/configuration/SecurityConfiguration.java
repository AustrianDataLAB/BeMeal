package at.ac.tuwien.ase.groupphase.backend.configuration;

import at.ac.tuwien.ase.groupphase.backend.repository.UserRepository;
import at.ac.tuwien.ase.groupphase.backend.security.GitHubAuthenticationHandler;
import at.ac.tuwien.ase.groupphase.backend.security.JwtAuthenticationFilter;
import at.ac.tuwien.ase.groupphase.backend.security.JwtAuthorizationFilter;
import at.ac.tuwien.ase.groupphase.backend.security.TokenManager;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import static org.springframework.boot.autoconfigure.security.servlet.PathRequest.toH2Console;

// todo update me
@Configuration
@EnableMethodSecurity
@EnableScheduling
@RequiredArgsConstructor
public class SecurityConfiguration {

    private final UserDetailsService userDetailsService;
    private final UserRepository userRepository;
    private final OAuth2AuthorizedClientService authAuthorizedClientService;

    private static final String[] AUTH_WHITELIST = { "/error", "/api/v1/self-service/registration/participant",
            "/api/v1/self-service/password-token/**", "/api/v1/self-service/password/**", "/v3/api-docs/**",
            "/v3/api-docs.yaml", "/swagger-ui/**", "/swagger-ui.html", "/api/v1/recipe/**",
            "/api/v1/community-identification/reload", "/actuator/**" };

    private static final String[] OAUTH_WHITELIST = { "/api/v1/self-service/ssologin", "/oauth2/authorization/github", "/login/oauth2/code/github"};

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    @Order(1)
    public SecurityFilterChain ssoLogin(final HttpSecurity http) throws Exception {
        return http
            .cors().and().csrf().disable()
            .securityMatcher(OAUTH_WHITELIST)
            .authorizeHttpRequests(authorize -> authorize.anyRequest().authenticated())
            .oauth2Login(oauth2 -> oauth2.successHandler(oAuth2LoginSuccessHandler()).authorizedClientService(authAuthorizedClientService))
            .build();
    }

    @Bean
    @Order(2)
    public SecurityFilterChain securityFilterChain(final HttpSecurity http,
            final AuthenticationConfiguration authenticationConfiguration) throws Exception {

        final var authenticationManager = authenticationConfiguration.getAuthenticationManager();
        final var authorizationFilter = authorizationFilter(authenticationManager);
        final var authenticationFilter = authenticationFilter(authenticationManager);
        // todo Customizer.withDefaults() excludes preflight requests from auth) - new syntax, otherwise you have to
        // allow options it cors-config
        return http.cors(Customizer.withDefaults()).csrf().disable().headers().frameOptions().disable().and()
                .addFilter(authenticationFilter).addFilter(authorizationFilter).sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS).and().authorizeHttpRequests()
                .requestMatchers(AUTH_WHITELIST).permitAll().requestMatchers(toH2Console()).permitAll().anyRequest()
                .authenticated().and().build();
    }

    private JwtAuthorizationFilter authorizationFilter(final AuthenticationManager authenticationManager) {
        return new JwtAuthorizationFilter(authenticationManager, tokenManager());
    }

    public JwtAuthenticationFilter authenticationFilter(final AuthenticationManager authenticationManager) {
        return new JwtAuthenticationFilter(authenticationManager, tokenManager());
    }

    private TokenManager tokenManager() {
        return new TokenManager(this.userDetailsService);
    }

    private GitHubAuthenticationHandler oAuth2LoginSuccessHandler() {
        return new GitHubAuthenticationHandler(this.userDetailsService, tokenManager(), this.userRepository, this.authAuthorizedClientService);
    }

    // fix cors issues and allow "Authorization" header to be exposed.
    // Tells browsers the header is safe and to process it
    @Configuration
    public class CorsConfig {
        @Bean
        public WebMvcConfigurer corsConfigurer() {
            return new WebMvcConfigurer() {
                @Override
                public void addCorsMappings(CorsRegistry registry) {
                    registry.addMapping("/**").allowedOrigins("*").allowedMethods("GET", "POST", "PUT", "DELETE")
                            .allowedHeaders("*").exposedHeaders("Authorization");
                    ;
                }
            };
        }
    }

}
