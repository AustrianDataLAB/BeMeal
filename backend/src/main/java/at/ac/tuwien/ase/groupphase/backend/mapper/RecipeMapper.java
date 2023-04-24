package at.ac.tuwien.ase.groupphase.backend.mapper;

import at.ac.tuwien.ase.groupphase.backend.dto.IngredientDto;
import at.ac.tuwien.ase.groupphase.backend.entity.RecipeSkillLevel;
import at.ac.tuwien.ase.groupphase.backend.dto.RecipeDto;
import org.neo4j.driver.Record;

import java.util.List;

public class RecipeMapper {

    private final IngredientMapper ingredientMapper;

    public RecipeMapper() {
        this.ingredientMapper = new IngredientMapper();
    }

    public RecipeDto recordToRecipeDto(Record record) {
        var node = record.get("recipe");

        // System.out.println(record.get("ingredient").get("name"));
        // ToDo: Add ingredients
        List<IngredientDto> ingredients = null;
        if (!record.get("ingredient").asString().equalsIgnoreCase("null")) {
            ingredients = record.get("ingredient").asList(ingredientMapper::recordToIngredientDto);
        }

        return new RecipeDto(Long.parseLong(node.get("id").asString()), node.get("name").asString(),
                node.get("description").asString(), node.get("preparationTime").asInt(),
                node.get("cookingTime").asInt(), stringToRecipeSkillLevel(node.get("skillLevel").asString()),
                ingredients);
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
