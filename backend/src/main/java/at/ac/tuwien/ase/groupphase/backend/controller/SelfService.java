package at.ac.tuwien.ase.groupphase.backend.controller;

import at.ac.tuwien.ase.groupphase.backend.dto.Registration;
import at.ac.tuwien.ase.groupphase.backend.entity.PlatformUser;
import at.ac.tuwien.ase.groupphase.backend.event.RequestPasswordResetEvent;
import at.ac.tuwien.ase.groupphase.backend.exception.UserAlreadyExistsException;
import at.ac.tuwien.ase.groupphase.backend.mapper.RegistrationMapper;
import at.ac.tuwien.ase.groupphase.backend.repository.ParticipantRepository;
import at.ac.tuwien.ase.groupphase.backend.repository.UserRepository;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * Responsible for all request a user might want to perform on its own profile. Examples for that are, registration,
 * login,....
 */
@RestController
@RequestMapping("/api/v1/self-service")
public class SelfService {

    private final Logger logger = LoggerFactory.getLogger(SelfService.class);

    private final UserRepository userRepository;
    private final ParticipantRepository participantRepository;
    private final RegistrationMapper registrationMapper;
    private final ApplicationEventPublisher eventPublisher;

    @Autowired
    @NotNull
    public SelfService(final UserRepository userRepository, final ParticipantRepository participantRepository,
            final RegistrationMapper registrationMapper, final ApplicationEventPublisher eventPublisher) {
        this.userRepository = userRepository;
        this.participantRepository = participantRepository;
        this.registrationMapper = registrationMapper;
        this.eventPublisher = eventPublisher;
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
        // This is only a placeholder for openApi
    }

    /**
     * Generate a password reset token for a {@link PlatformUser} with the provided email. In case, a user with such id
     * exists, this method will persist a random generated {@link UUID} and an email with a reset link will be sent to
     * the user. After this procedure, it is still possible to login. Only the latest password reset token per user
     * remains valid, on multiple invocations, any old tokens will be replaced and therefore invalidated.
     *
     * @param email
     *            the email address of the user to reset the password for - may be null
     */
    @PutMapping("/password-token/{email}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void requestPasswordReset(@NotNull @PathVariable final String email) {
        logger.trace("requestPasswordReset({})", email);
        final var user = this.userRepository.findByEmail(email);
        if (user == null) {
            logger.warn("No user with email '{}' exists, do not send an email", email);
            return;
        }
        final var passwordResetToken = UUID.randomUUID();
        user.setPasswordResetToken(passwordResetToken);
        this.userRepository.save(user);
        this.eventPublisher.publishEvent(new RequestPasswordResetEvent(email, passwordResetToken));
        logger.info("Generated password reset token for user '{}'", user.getUsername());
    }
}
