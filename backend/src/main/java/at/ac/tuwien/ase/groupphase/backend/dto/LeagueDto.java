package at.ac.tuwien.ase.groupphase.backend.dto;

import at.ac.tuwien.ase.groupphase.backend.entity.GameMode;

import java.util.List;

public record LeagueDto(Long id, String name, GameMode gameMode, Integer challengeDuration, List<String> lastWinners) {

}
