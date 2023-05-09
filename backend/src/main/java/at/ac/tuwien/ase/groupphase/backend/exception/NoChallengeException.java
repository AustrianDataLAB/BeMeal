package at.ac.tuwien.ase.groupphase.backend.exception;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class NoChallengeException extends RuntimeException {

    @Override
    public String getMessage() {
        return "No Challenge for this league";
    }
}
