package at.ac.tuwien.ase.groupphase.backend.dto;

import at.ac.tuwien.ase.groupphase.backend.entity.GameMode;
import at.ac.tuwien.ase.groupphase.backend.entity.Region;

public record JoinLeagueDto(Long leagueId, String hiddenIdentifier) {

}
