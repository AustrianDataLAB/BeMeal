package at.ac.tuwien.ase.groupphase.backend.mapper;

import at.ac.tuwien.ase.groupphase.backend.dto.IngredientDto;
import at.ac.tuwien.ase.groupphase.backend.dto.RecipeDto;
import at.ac.tuwien.ase.groupphase.backend.entity.Recipe;
import at.ac.tuwien.ase.groupphase.backend.entity.RecipeSkillLevel;
import at.ac.tuwien.ase.groupphase.backend.exception.MissingPictureException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

public class RecipeMapper {
    private static final String IMAGE_FORMAT = "jpg";
    private static final String IMAGE_PATH = "src/main/resources/recipes/";
    private final IngredientMapper ingredientMapper;

    public RecipeMapper() {
        this.ingredientMapper = new IngredientMapper();
    }

    public RecipeDto recipeToRecipeDto(Recipe recipe) {
        List<IngredientDto> ingredients = recipe.getIngredients().stream()
                .map(ingredientMapper::ingredientToIngredientDto).toList();
        // convert uuid to base64
        var base64 = uuidToBase64Converter(recipe.getPictureUUID());

        return new RecipeDto(recipe.getRecipeId(), recipe.getName(), recipe.getDescription(),
                recipe.getPreparationTime(), recipe.getCookingTime(), stringToRecipeSkillLevel(recipe.getSkillLevel()),
                ingredients, base64);
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

    private String uuidToBase64Converter(String uuid) {
        if (uuid == null) {
            return null;
        }
        var path = Paths.get(IMAGE_PATH, uuid + "." + IMAGE_FORMAT);
        if (!Files.exists(path))
            throw new MissingPictureException();
        try {
            byte[] bytes = Files.readAllBytes(path);
            return Base64.getEncoder().withoutPadding().encodeToString(bytes);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public List<RecipeDto> recipeListToRecipeDtoList(List<Recipe> list) {
        List<RecipeDto> ret = new ArrayList<>();
        for (Recipe recipe : list) {
            ret.add(recipeToRecipeDto(recipe));
        }
        return ret;
    }
}
