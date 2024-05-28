package at.ac.tuwien.ase.groupphase.backend.security;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class GitHubAuthenticationHandler implements AuthenticationSuccessHandler {

    private final UserDetailsService userDetailsService;
    private final TokenManager tokenManager;
    private final Logger hlogger = LoggerFactory.getLogger(GitHubAuthenticationHandler.class);

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
            Authentication authentication) throws IOException, ServletException {
        try {
            OAuth2AuthenticationToken oAuth2Token = (OAuth2AuthenticationToken) authentication;
            String username = oAuth2Token.getPrincipal().getAttribute("login");
            UserDetails userDetails = (UserDetails) userDetailsService.loadUserByUsername(username);
            String jwtToken = tokenManager.generateToken(userDetails);
            response.addHeader(JwtAuthenticationFilter.AUTH_HEADER_KEY,
                    JwtAuthenticationFilter.BEARER_PREFIX + this.tokenManager.generateToken(userDetails));
            hlogger.info("OAuth Login was successful");
            response.addHeader("Authorization", "Bearer " + jwtToken);
            response.setStatus(HttpServletResponse.SC_OK);
        } catch (Exception e) {
            hlogger.info("OAuth Login failed");
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

}
