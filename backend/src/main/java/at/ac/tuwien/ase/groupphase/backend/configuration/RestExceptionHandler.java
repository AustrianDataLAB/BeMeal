package at.ac.tuwien.ase.groupphase.backend.configuration;

import at.ac.tuwien.ase.groupphase.backend.dto.ErrorData;
import at.ac.tuwien.ase.groupphase.backend.exception.*;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.ValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.NoSuchElementException;

@RestControllerAdvice
public class RestExceptionHandler {

    private final Logger logger = LoggerFactory.getLogger(RestExceptionHandler.class);

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorData> logException(final Exception exception) {
        logger.error("Exception from a RestController", exception);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorData("An error has occurred. Please try again later"));
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    @ApiResponse(responseCode = "409", description = "When a user with that email/username already exists")
    public ResponseEntity<ErrorData> handleUserAlreadyExistsException(
            final UserAlreadyExistsException userAlreadyExistsException) {

        return ResponseEntity.status(HttpStatus.CONFLICT).body(new ErrorData(userAlreadyExistsException.getMessage()));
    }

    @ExceptionHandler(ValidationException.class)
    @ApiResponse(responseCode = "422", description = "When the dto contains malformed data")
    public ResponseEntity<ErrorData> handleValidationError(final ValidationException validationException) {

        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                .body(new ErrorData(validationException.getMessage()));
    }

    @ExceptionHandler(NotCreatorOfException.class)
    @ApiResponse(responseCode = "403", description = "When a user wants to operate on a league which requires ownership which they are not")
    public ResponseEntity<ErrorData> handleNotCreatorOfException(final NotCreatorOfException notCreatorOfException) {

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ErrorData(notCreatorOfException.getMessage()));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ApiResponse(responseCode = "409", description = "Illegal Argument")
    public ResponseEntity<ErrorData> handleIllegalArgumentException(
            final IllegalArgumentException illegalArgumentException) {

        return ResponseEntity.status(HttpStatus.CONFLICT).body(new ErrorData(illegalArgumentException.getMessage()));
    }

    @ExceptionHandler(NoChallengeException.class)
    @ApiResponse(responseCode = "409", description = "No challenge found")
    public ResponseEntity<ErrorData> handleNoChallengeException(final NoChallengeException noChallengeException) {

        return ResponseEntity.status(HttpStatus.CONFLICT).body(new ErrorData(noChallengeException.getMessage()));
    }

    @ExceptionHandler(NoLatestChallengeException.class)
    @ApiResponse(responseCode = "409", description = "No latest ending challenge found")
    public ResponseEntity<ErrorData> handleNoLatestChallengeException(
            final NoLatestChallengeException noLatestChallengeException) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(new ErrorData(noLatestChallengeException.getMessage()));
    }

    @ExceptionHandler(NoSuchElementException.class)
    @ApiResponse(responseCode = "404", description = "No such element")
    public ResponseEntity<ErrorData> handleNoSuchElementException(final NoSuchElementException noSuchElementException) {

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorData(noSuchElementException.getMessage()));
    }

    @ExceptionHandler(ForbiddenAccessException.class)
    @ApiResponse(responseCode = "404", description = "No access allowed")
    public ResponseEntity<ErrorData> handleForbiddenAccessException(
            final ForbiddenAccessException forbiddenAccessException) {

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ErrorData(forbiddenAccessException.getMessage()));
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    @ApiResponse(responseCode = "404", description = "No user found with this username")
    public ResponseEntity<ErrorData> handleNoSuchElementException(
            final UsernameNotFoundException usernameNotFoundException) {

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorData(usernameNotFoundException.getMessage()));
    }

    @ExceptionHandler(AlreadyJoinedException.class)
    @ApiResponse(responseCode = "409", description = "The user already joined this league")
    public ResponseEntity<ErrorData> handleAlreadyJoinedException(final AlreadyJoinedException alreadyJoinedException) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(new ErrorData(alreadyJoinedException.getMessage()));
    }
}
