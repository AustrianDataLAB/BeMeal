package at.ac.tuwien.ase.groupphase.backend.dto;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@NoArgsConstructor(force = true)
@AllArgsConstructor
public class LeaderboardDto implements Comparable<LeaderboardDto> {
    private final String username;
    private final Integer wins;
    private int position;

    public LeaderboardDto(String username, Integer points) {
        this.username = username;
        this.wins = points;
        this.position = 0;
    }

    public String getUsername() {
        return username;
    }

    public Integer getWins() {
        return wins;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    @Override
    public int compareTo(LeaderboardDto other) {
        // Compare based on points in descending order
        return Integer.compare(other.getWins(), this.getWins());
    }
}
