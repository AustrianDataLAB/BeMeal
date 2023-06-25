package at.ac.tuwien.ase.groupphase.backend.security;

import at.ac.tuwien.ase.groupphase.backend.entity.PlatformUser;
import at.ac.tuwien.ase.groupphase.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserDetailsManager implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(final String username) {
        if (username == null) {
            throw new UsernameNotFoundException("username was null");
        }
        final var opt = this.userRepository.findByUsername(username);
        if (opt != null) {
            return toUser(opt);
        } else {
            throw new UsernameNotFoundException("no user with name '$username' found");
        }
    }

    private User toUser(final PlatformUser platformUser) {
        return new User(platformUser.getUsername(), new String(platformUser.getPassword()),
                List.of(new SimpleGrantedAuthority(
                        Boolean.TRUE.equals(platformUser.getIsAdmin()) ? "GAMEMASTER" : "PARTICIPANT")));
    }
}