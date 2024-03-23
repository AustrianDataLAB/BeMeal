package at.ac.tuwien.ase.groupphase.backend.security;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import java.io.IOException;

import static at.ac.tuwien.ase.groupphase.backend.security.JwtAuthenticationFilter.AUTH_HEADER_KEY;
import static at.ac.tuwien.ase.groupphase.backend.security.JwtAuthenticationFilter.BEARER_PREFIX;

public class JwtAuthorizationFilter extends BasicAuthenticationFilter {
    private final TokenManager tokenManager;

    public JwtAuthorizationFilter(final AuthenticationManager authenticationManager, final TokenManager tokenManager) {
        super(authenticationManager);
        this.tokenManager = tokenManager;
    }

    @Override
    public void doFilterInternal(final HttpServletRequest request, final HttpServletResponse response,
            final FilterChain chain) throws ServletException, IOException {
        try {
            final var header = request.getHeader(AUTH_HEADER_KEY);
            if (header == null || !header.startsWith(BEARER_PREFIX)) {
                chain.doFilter(request, response);
                return;
            }
            final var authentication = getAuthentication(request);
            SecurityContextHolder.getContext().setAuthentication(toAuthenticationToken(authentication));
            chain.doFilter(request, response);
        } catch (AccessDeniedException e) {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
        }
    }

    private UserDetails getAuthentication(final HttpServletRequest request) {
        final var header = request.getHeader(AUTH_HEADER_KEY);
        if (header != null && header.startsWith(BEARER_PREFIX)) {
            String errorMessage;
            try {
                return tokenManager.userDetailsFromToken(header.replaceFirst("^" + BEARER_PREFIX, ""));
            } catch (ExpiredJwtException e) {
                errorMessage = "Expired token";
            } catch (UnsupportedJwtException e) {
                errorMessage = "Unsupported token";
            } catch (MalformedJwtException e) {
                errorMessage = "Malformed token";
            } catch (Exception e) {
                errorMessage = "Token not resolved";
            }
            throw new AccessDeniedException(errorMessage);
        } else {
            throw new AccessDeniedException("No token received");
        }
    }

    private UsernamePasswordAuthenticationToken toAuthenticationToken(final UserDetails userDetails) {
        return new UsernamePasswordAuthenticationToken(userDetails.getUsername(), userDetails.getPassword(),
                userDetails.getAuthorities());
    }
}