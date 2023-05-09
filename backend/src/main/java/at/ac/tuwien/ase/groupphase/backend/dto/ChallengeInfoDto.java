package at.ac.tuwien.ase.groupphase.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
public class ChallengeInfoDto {

    private String name;
    private String description;
    private List<IngredientDto> ingredients;
    private LocalDate endDate;
    private Long challengeId;

    public ChallengeInfoDto() {
    }
}
