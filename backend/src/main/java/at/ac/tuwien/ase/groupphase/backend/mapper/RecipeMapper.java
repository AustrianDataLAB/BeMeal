package at.ac.tuwien.ase.groupphase.backend.mapper;

import at.ac.tuwien.ase.groupphase.backend.dto.IngredientDto;
import at.ac.tuwien.ase.groupphase.backend.dto.RecipeDto;
import at.ac.tuwien.ase.groupphase.backend.entity.Recipe;
import at.ac.tuwien.ase.groupphase.backend.entity.RecipeSkillLevel;

import java.util.List;

public class RecipeMapper {
    private final IngredientMapper ingredientMapper;

    public RecipeMapper() {
        this.ingredientMapper = new IngredientMapper();
    }

    public RecipeDto recipeToRecipeDto(Recipe recipe) {
        List<IngredientDto> ingredients = recipe.getIngredients().stream().map(ingredientMapper::ingredientToIngredientDto).toList();
        return new RecipeDto(recipe.getRecipeId(), recipe.getName(), recipe.getDescription(), recipe.getPreparationTime(), recipe.getCookingTime(), stringToRecipeSkillLevel(recipe.getSkillLevel()), ingredients);
    }

    private RecipeSkillLevel stringToRecipeSkillLevel(String s) {
        if (s.equalsIgnoreCase("Easy")) {
            return RecipeSkillLevel.EASY;
        } else if (s.equalsIgnoreCase("More effort")) {
            return RecipeSkillLevel.MORE_EFFORT;
        } else if (s.equalsIgnoreCase("A challenge")) {
            return RecipeSkillLevel.A_CHALLENGE;
        } else {
            return null;
        }

    }
}
