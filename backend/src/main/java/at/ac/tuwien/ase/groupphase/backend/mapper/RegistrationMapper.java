package at.ac.tuwien.ase.groupphase.backend.mapper;

import at.ac.tuwien.ase.groupphase.backend.dto.Registration;
import at.ac.tuwien.ase.groupphase.backend.entity.Participant;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * Contains mapping methods for {@link Registration}.
 */
@Service
public class RegistrationMapper {

    private final PasswordEncoder passwordEncoder;

    @Autowired
    @NotNull
    public RegistrationMapper(final PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Map a registration dto to a participant entity. This method will map all the given attributes.
     * <p>
     * The password within the registration is assumed to be in clear-text. However, only its hash will be mapped to the
     * participants attribute.
     * <p>
     * The admin state will be set to 'false' and the creation date will be set to the current date of this machine.
     *
     * @param registration
     *            the registration to map
     *
     * @return the mapped participant
     */
    public Participant registrationToParticipant(@NotNull final Registration registration) {
        final var participant = new Participant();
        participant.setEmail(registration.email());
        participant.setUsername(registration.username());
        participant.setPostalCode(registration.postalCode());
        participant.setIsAdmin(false);
        participant.setRegion(registration.region());
        participant.setRegistered(LocalDateTime.now());
        participant.setPassword(this.passwordEncoder.encode(registration.password()).getBytes());
        return participant;
    }
}
