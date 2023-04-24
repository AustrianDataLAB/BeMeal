package at.ac.tuwien.ase.groupphase.backend.dto;

import at.ac.tuwien.ase.groupphase.backend.entity.RecipeSkillLevel;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

/*
public record RecipeDto(Long id, String name, String description, int preparationTime, int cookingTime,
        RecipeSkillLevel skillLevel, List<IngredientDto> ingredients) {
}
*/

@Data
@AllArgsConstructor
public class RecipeDto {
    private Long id;
    private String name;
    private String description;
    private int preparationTime;
    private int cookingTime;
    private RecipeSkillLevel skillLevel;
    private List<IngredientDto> ingredients;

}