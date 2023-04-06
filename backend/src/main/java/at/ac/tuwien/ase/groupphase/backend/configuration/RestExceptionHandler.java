package at.ac.tuwien.ase.groupphase.backend.configuration;

import at.ac.tuwien.ase.groupphase.backend.dto.ErrorData;
import at.ac.tuwien.ase.groupphase.backend.exception.UserAlreadyExistsException;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class RestExceptionHandler {

    private final Logger logger = LoggerFactory.getLogger(RestExceptionHandler.class);

    @ExceptionHandler(Exception.class)
    public void logException(final Exception exception) {
        logger.error("Exception from a RestController", exception);
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    @ApiResponse(responseCode = "409", description = "When a user with that email/username already exists")
    public ResponseEntity<ErrorData> handleUserAlreadyExistsException(
            final UserAlreadyExistsException userAlreadyExistsException) {

        return ResponseEntity.status(HttpStatus.CONFLICT).body(new ErrorData(userAlreadyExistsException.getMessage()));
    }

}
