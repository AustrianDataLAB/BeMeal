package at.ac.tuwien.ase.groupphase.backend.dto;

import org.springframework.lang.Nullable;

import java.util.UUID;

public record LeagueSecrets(@Nullable UUID hiddenIdentifier) {
}
