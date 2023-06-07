package at.ac.tuwien.ase.groupphase.backend.unittests;

import at.ac.tuwien.ase.groupphase.backend.dto.RecipeDto;
import at.ac.tuwien.ase.groupphase.backend.service.RecipeService;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@Disabled
public class RecipeServiceTest {
    @Autowired
    RecipeService recipeService;

    @Test
    public void givenData_searchForFoodReturnsList() {
        Page<RecipeDto> recipes = recipeService.findRecipesBySearchString("eesecak", null, null, null, 0, 5);
        assertEquals(5, recipes.getNumberOfElements());
    }

    @Test
    public void givenData_searchForNonExistentFoodReturnsEmptyList() {
        assertThrows(NoSuchElementException.class,
                () -> recipeService.findRecipesBySearchString("!nonexistent!", null, null, null, 0, 5));
    }

    @Test
    public void givenData_getRecipeByIdReturnsRecipe() {
        RecipeDto recipe = recipeService.getRecipeById("101233");
        assertNotNull(recipe);
        assertEquals("101233", recipe.getRecipeId());
        assertEquals("Tomato & mozzarella couscous salad", recipe.getName());
        assertEquals(recipe.getIngredients().size(), 8);
    }
}
