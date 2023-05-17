package at.ac.tuwien.ase.groupphase.backend.dto;

public class LeaderboardDto implements Comparable<LeaderboardDto> {
    private String username;
    private Long points;
    private int position;

    public LeaderboardDto(String username, Long points) {
        this.username = username;
        this.points = points;
        this.position = 0;
    }


    public String getUsername() {
        return username;
    }

    public Long getPoints() {
        return points;
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
        return other.getPoints().compareTo(this.getPoints());
    }
}