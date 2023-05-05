package at.ac.tuwien.ase.groupphase.backend.exception;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class ForbiddenAccessException extends RuntimeException {

    private final String message;

    @Override
    public String getMessage() {
        return this.message;
    }
}
