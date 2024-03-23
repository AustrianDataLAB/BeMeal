package at.ac.tuwien.ase.groupphase.backend.exception;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class MissingPictureException extends RuntimeException {

    @Override
    public String getMessage() {
        return "Picture is missing";
    }
}
