package at.ac.tuwien.ase.groupphase.backend.dto;

import at.ac.tuwien.ase.groupphase.backend.entity.RecipeSkillLevel;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class RecipeDto {
    private String recipeId;
    private String name;
    private String description;
    private Integer preparationTime;
    private Integer cookingTime;
    private RecipeSkillLevel skillLevel;
    private List<IngredientDto> ingredients;
    // base64
    private String picture;
    private List<RecipeDietTypeDto> dietTypes;
}