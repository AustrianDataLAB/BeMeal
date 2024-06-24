package at.ac.tuwien.ase.groupphase.backend.mapper;

import at.ac.tuwien.ase.groupphase.backend.dto.IngredientDto;
import at.ac.tuwien.ase.groupphase.backend.dto.RecipeDietTypeDto;
import at.ac.tuwien.ase.groupphase.backend.dto.RecipeDto;
import at.ac.tuwien.ase.groupphase.backend.entity.Recipe;
import at.ac.tuwien.ase.groupphase.backend.entity.RecipeSkillLevel;
import at.ac.tuwien.ase.groupphase.backend.exception.MissingPictureException;
import at.ac.tuwien.ase.groupphase.backend.service.AzureStorageService;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

@Component
public class RecipeMapper {
    private final IngredientMapper ingredientMapper;
    private final RecipeDietTypeMapper dietTypeMapper;
    private final AzureStorageService azureStorageService;

    public RecipeMapper(AzureStorageService azureStorageService) {
        this.ingredientMapper = new IngredientMapper();
        this.dietTypeMapper = new RecipeDietTypeMapper();
        this.azureStorageService = azureStorageService;
    }

    public RecipeDto recipeToRecipeDto(Recipe recipe) {
        List<IngredientDto> ingredients = recipe.getIngredients().stream()
                .map(ingredientMapper::ingredientToIngredientDto).toList();
        // convert uuid to base64
        var base64 = uuidToBase64Converter(recipe.getPictureUUID());
        List<RecipeDietTypeDto> dietTypes = recipe.getDietTypes().stream().map(dietTypeMapper::dietTypeToDietTypeDto)
                .toList();

        return new RecipeDto(recipe.getRecipeId(), recipe.getName(), recipe.getDescription(),
                recipe.getPreparationTime(), recipe.getCookingTime(), stringToRecipeSkillLevel(recipe.getSkillLevel()),
                ingredients, base64, dietTypes);
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

    public String uuidToBase64Converter(String uuid) {
        if (uuid == null) {
            return null;
        }
        try (final var stream = azureStorageService.getFile(uuid).getInputStream()) {
            byte[] bytes = StreamUtils.copyToByteArray(stream);
            return Base64.getEncoder().withoutPadding().encodeToString(bytes);
        } catch (IOException e) {
            throw new MissingPictureException();
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
