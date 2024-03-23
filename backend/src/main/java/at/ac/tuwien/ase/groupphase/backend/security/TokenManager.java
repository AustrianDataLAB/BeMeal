package at.ac.tuwien.ase.groupphase.backend.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.ZonedDateTime;
import java.util.Date;

@Component
@RequiredArgsConstructor
public class TokenManager {

    private final UserDetailsService userDetailsService;
    private static final SecretKey secretKey = Keys.secretKeyFor(SignatureAlgorithm.HS512);

    public String generateToken(final UserDetails user) {
        return Jwts.builder().setIssuedAt(Date.from(ZonedDateTime.now().toInstant())).setIssuer("BeMeal")
                .setSubject(user.getUsername()).setExpiration(Date.from(ZonedDateTime.now().plusWeeks(1).toInstant()))
                .signWith(secretKey).compact();
    }

    public UserDetails userDetailsFromToken(final String token) {
        return userDetailsService.loadUserByUsername(parseToken(token));
    }

    public String parseToken(final String token) {
        return Jwts.parserBuilder().setSigningKey(secretKey).build()
                .parseClaimsJws(token.replace(JwtAuthenticationFilter.BEARER_PREFIX, "")).getBody().getSubject();
    }
}
