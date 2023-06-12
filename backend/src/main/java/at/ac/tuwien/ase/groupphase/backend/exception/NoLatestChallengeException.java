package at.ac.tuwien.ase.groupphase.backend.exception;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class NoLatestChallengeException extends RuntimeException {

    @Override
    public String getMessage() {
        return "No latest ending challenge for this league";
    }
}
