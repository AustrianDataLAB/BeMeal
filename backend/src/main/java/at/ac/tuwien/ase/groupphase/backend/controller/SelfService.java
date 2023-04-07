package at.ac.tuwien.ase.groupphase.backend.controller;

import at.ac.tuwien.ase.groupphase.backend.dto.Registration;
import at.ac.tuwien.ase.groupphase.backend.exception.UserAlreadyExistsException;
import at.ac.tuwien.ase.groupphase.backend.mapper.RegistrationMapper;
import at.ac.tuwien.ase.groupphase.backend.repository.ParticipantRepository;
import at.ac.tuwien.ase.groupphase.backend.repository.UserRepository;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/self-service")
/**
 * Responsible for all request a user might want to perform on its own profile. Examples for that are, registration,
 * login,....
 */
public class SelfService {

    private final Logger logger = LoggerFactory.getLogger(SelfService.class);

    private final UserRepository userRepository;
    private final ParticipantRepository participantRepository;
    private final RegistrationMapper registrationMapper;

    @Autowired
    @NotNull
    public SelfService(final UserRepository userRepository, final ParticipantRepository participantRepository,
            final RegistrationMapper registrationMapper) {
        this.userRepository = userRepository;
        this.participantRepository = participantRepository;
        this.registrationMapper = registrationMapper;
    }

    /**
     * Register a new participant to the backend system. This endpoint only allows the registration of a participant -
     * not a gamemaster. Note that the unique attributes of a user such as the email or the username are mutually
     * exclusive to either a participant or a gamemaster.
     *
     * @param registration
     *            the data required for the participant registration
     *
     * @throws UserAlreadyExistsException
     *             when a user with the email/username already exists
     */
    @PostMapping("/registration/participant")
    @ResponseStatus(HttpStatus.CREATED)
    public void registerParticipant(@NotNull @RequestBody final Registration registration) {
        logger.trace("registerParticipant(...)");
        if (this.userRepository.exists(registration.email(), registration.password())) {
            throw new UserAlreadyExistsException(registration.email(), registration.username());
        }
        final var participant = this.registrationMapper.registrationToParticipant(registration);
        this.participantRepository.save(participant);
        logger.info("Registered participant with id: '{}' email: '{}' username: '{}'", participant.getId(),
                participant.getEmail(), participant.getUsername());
    }

    /**
     * Authenticate the user against the backend. Just use Basic Auth here to authenticate. The backend will then return
     * the auth token in the HTTP authorization header.
     */
    @GetMapping("/login")
    @ResponseStatus(HttpStatus.OK)
    @SecurityRequirement(name = "credentials")
    public void login() {
        throw new RuntimeException();
    }

    @GetMapping("/test")
    @SecurityRequirement(name = "bearerToken")
    @ResponseStatus(HttpStatus.OK)
    public String test() {
        return "junge";
    }
}
