package at.ac.tuwien.ase.groupphase.backend.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.Base64;

public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    public static final String BEARER_PREFIX = "Bearer ";
    static final String AUTH_HEADER_KEY = "Authorization";
    private static final String BASIC_AUTH_PREFIX = "Basic ";
    private final Logger hlogger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    private final AuthenticationManager authenticationManager;
    private final TokenManager tokenManager;

    public JwtAuthenticationFilter(final AuthenticationManager authenticationManager, final TokenManager tokenManager) {
        setFilterProcessesUrl("/api/v1/self-service/login");
        this.authenticationManager = authenticationManager;
        this.tokenManager = tokenManager;
    }

    @Override
    protected void successfulAuthentication(final HttpServletRequest request, final HttpServletResponse response,
            final FilterChain chain, final Authentication authResult) {
        final var user = authResult.getPrincipal();
        if (user instanceof User) {
            response.addHeader(AUTH_HEADER_KEY, BEARER_PREFIX + this.tokenManager.generateToken((UserDetails) user));
            hlogger.info("Login was successful");
        } else {
            hlogger.error("Cannot generate token");
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }

    @Override
    public Authentication attemptAuthentication(final HttpServletRequest request, final HttpServletResponse response) {
        try {
            final var header = request.getHeader(AUTH_HEADER_KEY);
            if (header == null || !header.startsWith(BASIC_AUTH_PREFIX)) {
                hlogger.error("Received login request without 'Basic' prefix");
                response.setStatus(HttpStatus.BAD_REQUEST.value());
                throw new BadCredentialsException("not a basic auth");
            }
            final var basicHeader = new String(Base64.getDecoder()
                    .decode(request.getHeader(AUTH_HEADER_KEY).replaceFirst("^" + BASIC_AUTH_PREFIX, "")));

            final var authData = basicHeader.split(":", 2);
            if (authData.length != 2) {
                hlogger.error("Received bad login request");
                response.setStatus(HttpStatus.BAD_REQUEST.value());
                throw new BadCredentialsException("no colon provided");
            }
            final var username = authData[0];
            final var password = authData[1];
            return authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        } catch (IllegalArgumentException e) {
            hlogger.error("Received login with invalid base64");
            throw new BadCredentialsException("invalid base64", e);
        }
    }
}
