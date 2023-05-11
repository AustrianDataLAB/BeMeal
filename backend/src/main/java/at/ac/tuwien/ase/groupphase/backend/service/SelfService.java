package at.ac.tuwien.ase.groupphase.backend.service;

import at.ac.tuwien.ase.groupphase.backend.controller.UserEndpoint;
import at.ac.tuwien.ase.groupphase.backend.dto.Registration;
import at.ac.tuwien.ase.groupphase.backend.exception.UserAlreadyExistsException;
import at.ac.tuwien.ase.groupphase.backend.mapper.RegistrationMapper;
import at.ac.tuwien.ase.groupphase.backend.repository.ParticipantRepository;
import at.ac.tuwien.ase.groupphase.backend.repository.UserRepository;
import at.ac.tuwien.ase.groupphase.backend.validator.PostalCodeValidator;
import jakarta.validation.ValidationException;
import jakarta.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SelfService {

    private final Logger logger = LoggerFactory.getLogger(UserEndpoint.class);
    private final UserRepository userRepository;
    private final ParticipantRepository participantRepository;
    private final RegistrationMapper registrationMapper;
    private final ParticipantService participantService;
    private final PostalCodeValidator postalCodeValidator;
    private final LeagueService leagueService;

    @Autowired
    @NotNull
    public SelfService(final UserRepository userRepository, final ParticipantRepository participantRepository,
            ParticipantService participantService, final RegistrationMapper registrationMapper,
            PostalCodeValidator postalCodeValidator, LeagueService leagueService) {
        this.userRepository = userRepository;
        this.participantRepository = participantRepository;
        this.registrationMapper = registrationMapper;
        this.participantService = participantService;
        this.postalCodeValidator = postalCodeValidator;
        this.leagueService = leagueService;
    }

    public void register(Registration registration) throws ValidationException {
        if (this.userRepository.exists(registration.email(), registration.password())) {
            throw new UserAlreadyExistsException(registration.email(), registration.username());
        }
        // check postal code
        if (!this.postalCodeValidator.isPostalCodeValid(registration.postalCode())) {
            throw new ValidationException("Invalid postal code: " + registration.postalCode());
        }
        final var participant = this.registrationMapper.registrationToParticipant(registration);
        this.participantRepository.save(participant);
        logger.info("Registered participant with id: '{}' email: '{}' username: '{}'", participant.getId(),
                participant.getEmail(), participant.getUsername());
        // add user to regional league:
        this.leagueService.joinRegionalLeague(participant.getUsername(), participant.getRegion());
    }
}
