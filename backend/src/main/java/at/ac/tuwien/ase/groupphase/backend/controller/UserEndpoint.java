package at.ac.tuwien.ase.groupphase.backend.controller;

import at.ac.tuwien.ase.groupphase.backend.dto.ParticipantDto;
import at.ac.tuwien.ase.groupphase.backend.dto.Registration;
import at.ac.tuwien.ase.groupphase.backend.exception.UserAlreadyExistsException;
import at.ac.tuwien.ase.groupphase.backend.mapper.RegistrationMapper;
import at.ac.tuwien.ase.groupphase.backend.repository.ParticipantRepository;
import at.ac.tuwien.ase.groupphase.backend.repository.UserRepository;
import at.ac.tuwien.ase.groupphase.backend.service.ParticipantService;
import at.ac.tuwien.ase.groupphase.backend.service.SelfService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.ValidationException;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.apache.catalina.connector.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/self-service")
/**
 * Responsible for all request a user might want to perform on its own profile. Examples for that are, registration,
 * login,....
 */
public class UserEndpoint {

    private final Logger logger = LoggerFactory.getLogger(UserEndpoint.class);

    private final UserRepository userRepository;
    private final ParticipantRepository participantRepository;
    private final RegistrationMapper registrationMapper;
    private final SelfService selfService;
    private final ParticipantService participantService;

    @Autowired
    @NotNull
    public UserEndpoint(final UserRepository userRepository, final ParticipantRepository participantRepository,
            ParticipantService participantService, final RegistrationMapper registrationMapper,
            SelfService selfService) {
        this.userRepository = userRepository;
        this.participantRepository = participantRepository;
        this.registrationMapper = registrationMapper;
        this.participantService = participantService;
        this.selfService = selfService;
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
        this.selfService.register(registration);
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
}
