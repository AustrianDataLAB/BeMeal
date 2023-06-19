package at.ac.tuwien.ase.groupphase.backend.service;

import at.ac.tuwien.ase.groupphase.backend.dto.PasswordResetDto;
import at.ac.tuwien.ase.groupphase.backend.endpoint.UserEndpoint;
import at.ac.tuwien.ase.groupphase.backend.dto.RegistrationDto;
import at.ac.tuwien.ase.groupphase.backend.exception.UserAlreadyExistsException;
import at.ac.tuwien.ase.groupphase.backend.mapper.RegistrationMapper;
import at.ac.tuwien.ase.groupphase.backend.repository.ParticipantRepository;
import at.ac.tuwien.ase.groupphase.backend.repository.UserRepository;
import at.ac.tuwien.ase.groupphase.backend.validator.PostalCodeValidator;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SelfService {

    private final Logger logger = LoggerFactory.getLogger(UserEndpoint.class);
    private final UserRepository userRepository;
    private final ParticipantRepository participantRepository;
    private final RegistrationMapper registrationMapper;
    private final PostalCodeValidator postalCodeValidator;
    private final LeagueService leagueService;
    private final PasswordEncoder passwordEncoder;

    public void register(RegistrationDto registrationDto) throws ValidationException {
        if (this.userRepository.exists(registrationDto.email(), registrationDto.password())) {
            throw new UserAlreadyExistsException(registrationDto.email(), registrationDto.username());
        }
        // check postal code
        if (!this.postalCodeValidator.isPostalCodeValid(registrationDto.postalCode())) {
            throw new ValidationException("Invalid postal code: " + registrationDto.postalCode());
        }
        final var participant = this.registrationMapper.registrationToParticipant(registrationDto);
        this.participantRepository.save(participant);
        logger.trace("Registered participant with id: '{}' email: '{}' username: '{}'", participant.getId(),
                participant.getEmail(), participant.getUsername());
        // add user to regional league:
        this.leagueService.joinRegionalLeague(participant.getUsername(), participant.getRegion());
    }

    public UUID getPasswordResetTokenForUser(String email) {
        final var user = this.userRepository.findByEmail(email);
        if (user == null) {
            logger.warn("No user with email '{}' exists, do not send an email", email);
            return null;
        }
        final var passwordResetToken = UUID.randomUUID();
        user.setPasswordResetToken(passwordResetToken);
        this.userRepository.save(user);

        logger.trace("Generated password reset token for user '{}'", user.getUsername());

        return passwordResetToken;
    }

    public void resetPassword(UUID passwordResetToken, PasswordResetDto passwordResetDto) {
        final var user = this.userRepository.findByPasswordResetToken(passwordResetToken);
        if (user == null) {
            logger.warn("No user which currently is registered to the reset token '{}'", passwordResetToken);
            return;
        }
        user.setPasswordResetToken(null);
        user.setPassword(this.passwordEncoder.encode(passwordResetDto.password()).getBytes());
        this.userRepository.save(user);

        logger.trace("Updated password for user '{}'", user.getUsername());
    }

}
