package at.ac.tuwien.ase.groupphase.backend.mapper;

import at.ac.tuwien.ase.groupphase.backend.dto.IngredientDto;
import at.ac.tuwien.ase.groupphase.backend.entity.Ingredient;

public class IngredientMapper {

    public IngredientDto ingredientToIngredientDto(Ingredient ingredient) {
        return new IngredientDto(ingredient.getName());
    }
}
