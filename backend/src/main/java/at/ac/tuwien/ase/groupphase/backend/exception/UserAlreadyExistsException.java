package at.ac.tuwien.ase.groupphase.backend.exception;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class UserAlreadyExistsException extends RuntimeException {

    private final String email;
    private final String username;

    @Override
    public String getMessage() {
        return "A user with the email address '" + this.email + "' or username '" + this.username + "' already exists";
    }
}
