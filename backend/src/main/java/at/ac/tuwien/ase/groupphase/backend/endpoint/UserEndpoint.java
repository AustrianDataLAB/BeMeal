package at.ac.tuwien.ase.groupphase.backend.endpoint;

import at.ac.tuwien.ase.groupphase.backend.dto.ParticipantDto;
import at.ac.tuwien.ase.groupphase.backend.dto.PasswordResetDto;
import at.ac.tuwien.ase.groupphase.backend.dto.RegistrationDto;
import at.ac.tuwien.ase.groupphase.backend.entity.PlatformUser;
import at.ac.tuwien.ase.groupphase.backend.event.RequestPasswordResetEvent;
import at.ac.tuwien.ase.groupphase.backend.exception.UserAlreadyExistsException;
import at.ac.tuwien.ase.groupphase.backend.service.ParticipantService;
import at.ac.tuwien.ase.groupphase.backend.service.SelfService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/self-service")
/**
 * Responsible for all request a user might want to perform on its own profile. Examples for that are, registration,
 * login,....
 */
public class UserEndpoint {

    private final Logger logger = LoggerFactory.getLogger(UserEndpoint.class);

    private final SelfService selfService;
    private final ParticipantService participantService;
    private final ApplicationEventPublisher eventPublisher;

    /**
     * Register a new participant to the backend system. This endpoint only allows the registration of a participant -
     * not a gamemaster. Note that the unique attributes of a user such as the email or the username are mutually
     * exclusive to either a participant or a gamemaster.
     *
     * @param registrationDto
     *            the data required for the participant registration
     *
     * @throws UserAlreadyExistsException
     *             when a user with the email/username already exists
     */
    @PostMapping("/registration/participant")
    @ResponseStatus(HttpStatus.CREATED)
    public void registerParticipant(@NotNull @RequestBody final RegistrationDto registrationDto) {
        logger.trace("registerParticipant(...)");
        this.selfService.register(registrationDto);
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

    /**
     * Retrieves the profile of the authenticated participant.
     *
     * @return the ParticipantDto of the authenticated participant.
     */
    @GetMapping("/profile")
    @SecurityRequirement(name = "bearerToken")
    @ResponseStatus(HttpStatus.OK)
    public ParticipantDto viewProfile() {
        return this.participantService.getParticipantDto();
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
        var passwordResetToken = selfService.getPasswordResetTokenForUser(email);
        this.eventPublisher.publishEvent(new RequestPasswordResetEvent(email, passwordResetToken));
    }

    /**
     * Reset the password of a user. For the rest process, a valid password reset token must be generated first. This
     * can be done by using {@link SelfService#resetPassword(UUID, PasswordResetDto)}. On success, the password reset
     * token will be invalidated.
     *
     * @param passwordResetToken
     *            the token to identify the underlying user
     * @param passwordResetDto
     *            the container for the new user password in clear-text
     */
    @PutMapping("/password/{passwordResetToken}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void resetPassword(@NotNull @PathVariable final UUID passwordResetToken,
            @NotNull @RequestBody final PasswordResetDto passwordResetDto) {
        logger.trace("resetPassword({})", passwordResetDto);
        selfService.resetPassword(passwordResetToken, passwordResetDto);
    }
}
