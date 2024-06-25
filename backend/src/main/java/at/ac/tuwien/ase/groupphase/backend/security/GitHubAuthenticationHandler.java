package at.ac.tuwien.ase.groupphase.backend.security;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.codec.json.Jackson2JsonDecoder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import at.ac.tuwien.ase.groupphase.backend.entity.PlatformUser;
import at.ac.tuwien.ase.groupphase.backend.repository.UserRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class GitHubAuthenticationHandler implements AuthenticationSuccessHandler {

    private final UserDetailsService userDetailsService;
    private final TokenManager tokenManager;
    private final UserRepository userRepository;
    private final OAuth2AuthorizedClientService authAuthorizedClientService;
    private final Logger hlogger = LoggerFactory.getLogger(GitHubAuthenticationHandler.class);

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
            Authentication authentication) throws IOException, ServletException {
        try {
            String redirect_url = request.getHeader("X-Redirect-Url");
            if (redirect_url == null) {
                hlogger.error("No redirect URL provided");
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                return;
            }
            OAuth2AuthenticationToken oAuth2Token = (OAuth2AuthenticationToken) authentication;
            String username = oAuth2Token.getPrincipal().getAttribute("login");
            String email = getUserEmail(oAuth2Token);
            UserDetails userDetails;
            try {
                userDetails = userDetailsService.loadUserByUsername(username);
            } catch (UsernameNotFoundException e) {
                hlogger.info("User not found in database, creating new user");
                PlatformUser pfuser = new PlatformUser();
                pfuser.setEmail(email);
                pfuser.setUsername(username);
                pfuser.setPassword(null);
                pfuser.setIsAdmin(false);
                userRepository.save(pfuser);
                userDetails = userDetailsService.loadUserByUsername(username);
            }
            String jwtToken = tokenManager.generateToken(userDetails);
            response.addHeader(JwtAuthenticationFilter.AUTH_HEADER_KEY,
                    JwtAuthenticationFilter.BEARER_PREFIX + jwtToken);
            hlogger.info("OAuth Login was successful");
            UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(redirect_url).queryParam("auth", jwtToken);
            response.addHeader("Authorization", "Bearer " + jwtToken);
            response.sendRedirect(builder.toUriString());
        } catch (Exception e) {
            hlogger.error("OAuth Login failed");
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    private String getUserEmail(OAuth2AuthenticationToken oAuth2Token) {
        final String URL = "https://api.github.com/user/emails";
        OAuth2AuthorizedClient oAuth2AuthorizedClient = authAuthorizedClientService.loadAuthorizedClient(
                oAuth2Token.getAuthorizedClientRegistrationId(), oAuth2Token.getName());

        final ObjectMapper mapper = new ObjectMapper()
                .findAndRegisterModules()
                .enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT);
        final ExchangeStrategies exchangeStrategies = ExchangeStrategies.builder()
                .codecs(configurer -> configurer.defaultCodecs()
                .jackson2JsonDecoder(new Jackson2JsonDecoder(mapper)))
                .build();

        EmailObj[] response = WebClient.builder().exchangeStrategies(exchangeStrategies).build()
                .get()
                .uri(URL)
                .accept(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + oAuth2AuthorizedClient.getAccessToken().getTokenValue())
                .retrieve().bodyToMono(EmailObj[].class).block();
        if (response == null) {
            throw new RuntimeException("Response from GitHub is null");
        }

        for (EmailObj emailObj : response) {
            if (emailObj.isPrimary()) {
                authAuthorizedClientService.removeAuthorizedClient(
                        oAuth2Token.getAuthorizedClientRegistrationId(), oAuth2Token.getName());
                return emailObj.getEmail();
            }
        }
        return null;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    @Builder
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    private static class EmailObj {

        private String email;
        private boolean primary;
    }

}
