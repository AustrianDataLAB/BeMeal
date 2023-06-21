package at.ac.tuwien.ase.groupphase.backend.mapper;

import at.ac.tuwien.ase.groupphase.backend.dto.RegistrationDto;
import at.ac.tuwien.ase.groupphase.backend.entity.Participant;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * Contains mapping methods for {@link RegistrationDto}.
 */
@Service
@RequiredArgsConstructor
public class RegistrationMapper {

    private final PasswordEncoder passwordEncoder;

    /**
     * Map a registration dto to a participant entity. This method will map all the given attributes.
     * <p>
     * The password within the registration is assumed to be in clear-text. However, only its hash will be mapped to the
     * participants attribute.
     * <p>
     * The admin state will be set to 'false' and the creation date will be set to the current date of this machine.
     *
     * @param registrationDto
     *            the registration to map
     *
     * @return the mapped participant
     */
    public Participant registrationToParticipant(@NotNull final RegistrationDto registrationDto) {
        final var participant = new Participant();
        participant.setEmail(registrationDto.email());
        participant.setUsername(registrationDto.username());
        participant.setPostalCode(registrationDto.postalCode());
        participant.setIsAdmin(false);
        participant.setRegion(registrationDto.region());
        participant.setRegistered(LocalDateTime.now());
        participant.setPassword(this.passwordEncoder.encode(registrationDto.password()).getBytes());
        return participant;
    }
}
