package at.ac.tuwien.ase.groupphase.backend.dto;

import at.ac.tuwien.ase.groupphase.backend.entity.GameMode;

import java.util.List;

public record SuggestionDto(List<RecipeDto> given, List<RecipeDto> suggestions) {
}
