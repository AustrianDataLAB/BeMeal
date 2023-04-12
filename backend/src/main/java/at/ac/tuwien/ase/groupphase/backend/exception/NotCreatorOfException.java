package at.ac.tuwien.ase.groupphase.backend.exception;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class NotCreatorOfException extends RuntimeException {

    private final String username;
    private final long leagueId;

    @Override
    public String getMessage() {
        return "The user with the username '" + this.username + "' is not the creator of the league with the id '"
                + this.leagueId + "'";
    }
}
