package at.ac.tuwien.ase.groupphase.backend.exception;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.CONFLICT, reason = "User with that email address already exists")
@AllArgsConstructor
public class UserAlreadyExistsException extends Exception {

    private final String email;
    private final String username;

    @Override
    public String getMessage() {
        return "A user with the email address '" + this.email + "' or username '" + this.username + "' already exists";
    }
}
