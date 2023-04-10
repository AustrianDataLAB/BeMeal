package at.ac.tuwien.ase.groupphase.backend.controller;

import at.ac.tuwien.ase.groupphase.backend.dto.PasswordReset;
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
import org.springframework.security.crypto.password.PasswordEncoder;
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
    private final PasswordEncoder passwordEncoder;

    @Autowired
    @NotNull
    public SelfService(final UserRepository userRepository, final ParticipantRepository participantRepository,
            final RegistrationMapper registrationMapper, final ApplicationEventPublisher eventPublisher,
            final PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.participantRepository = participantRepository;
        this.registrationMapper = registrationMapper;
        this.eventPublisher = eventPublisher;
        this.passwordEncoder = passwordEncoder;
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

    /**
     * Reset the password of a user. For the rest process, a valid password reset token must be generated first. This
     * can be done by using {@link SelfService#requestPasswordReset(String)}. On success, the password reset token will
     * be invalidated.
     *
     * @param passwordResetToken
     *            the token to identify the underlying user
     * @param passwordReset
     *            the container for the new user password in clear-text
     */
    @PutMapping("/password/{passwordResetToken}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void resetPassword(@NotNull @PathVariable final UUID passwordResetToken,
            @NotNull @RequestBody final PasswordReset passwordReset) {
        logger.trace("resetPassword({})", passwordReset);
        final var user = this.userRepository.findByPasswordResetToken(passwordResetToken);
        if (user == null) {
            logger.warn("No user which currently is registered to the reset token '{}'", passwordResetToken);
            return;
        }
        user.setPasswordResetToken(null);
        user.setPassword(this.passwordEncoder.encode(passwordReset.password()).getBytes());
        this.userRepository.save(user);
        logger.info("Updated password for user '{}'", user.getUsername());
    }
}
