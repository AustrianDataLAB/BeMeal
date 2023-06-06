package at.ac.tuwien.ase.groupphase.backend.exception;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class AlreadyJoinedException extends RuntimeException {

    @Override
    public String getMessage() {
        return "The participant has already joined this league";
    }
}
