package at.ac.tuwien.ase.groupphase.backend.unittests;

import at.ac.tuwien.ase.groupphase.backend.dto.RecipeDto;
import at.ac.tuwien.ase.groupphase.backend.service.RecipeService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class RecipeServiceTest {

    RecipeService recipeService = new RecipeService();

    @Test
    @Timeout(5)
    public void givenRunningDatabase_testNeo4jDatabaseConnectionByCreatingSession() {
        boolean isSessionCreated = recipeService.testSession();
        assertTrue(isSessionCreated);
    }

    @Test
    public void givenData_searchForFoodReturnsList() {
        List<RecipeDto> recipes = recipeService.searchRecipeByName("cheesecake");
        assertFalse(recipes.isEmpty());
    }

    @Test
    public void givenData_searchForNonExistentFoodReturnsEmptyList() {
        List<RecipeDto> recipes = recipeService.searchRecipeByName("nonexistent");
        assertTrue(recipes.isEmpty());
    }

    @Test
    public void givenData_getRecipeByIdReturnsRecipe() {
        RecipeDto recipe = recipeService.getRecipeById(101233L);
        assertNotNull(recipe);
        assertEquals(101233L, recipe.getId());
        assertFalse(recipe.getIngredients().isEmpty());
    }
}
