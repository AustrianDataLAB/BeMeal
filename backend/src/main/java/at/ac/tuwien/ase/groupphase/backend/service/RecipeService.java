package at.ac.tuwien.ase.groupphase.backend.service;

import at.ac.tuwien.ase.groupphase.backend.dto.IngredientDto;
import at.ac.tuwien.ase.groupphase.backend.dto.RecipeDto;
import at.ac.tuwien.ase.groupphase.backend.entity.Ingredient;
import at.ac.tuwien.ase.groupphase.backend.mapper.IngredientMapper;
import at.ac.tuwien.ase.groupphase.backend.mapper.RecipeMapper;
import jakarta.persistence.criteria.CriteriaBuilder;
import org.neo4j.driver.*;
import org.neo4j.driver.Record;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.NoSuchElementException;

import static org.neo4j.driver.Values.parameters;

@Service
public class RecipeService implements AutoCloseable {

    private final Logger logger = LoggerFactory.getLogger(RecipeService.class);
    private final Driver driver;
    private final RecipeMapper recipeMapper;
    private final IngredientMapper ingredientMapper;

    public RecipeService() {
        // ToDo: Edit credential config
        driver = GraphDatabase.driver("bolt://localhost:7687", AuthTokens.basic("neo4j", "password"));
        recipeMapper = new RecipeMapper();
        ingredientMapper = new IngredientMapper();
    }

    @Override
    public void close() throws RuntimeException {
        logger.trace("Closing neo4j database connection");
        driver.close();
    }

    public boolean testSession() {
        logger.trace("Testing neo4j database session");
        try {
            Session session = driver.session();
            session.close();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // ToDo: Add ingredients support
    public List<RecipeDto> searchRecipeByName(String name) {
        logger.trace("Searching for recipe with name " + name);
        try (var session = driver.session()) {
            return session.executeRead(tx -> {
                var query = new Query(
                        // "MATCH(recipe: Recipe WHERE recipe.name =~ $searchTerm)--(ingredient: Ingredient) return
                        // recipe, ingredient",
                        "MATCH(recipe: Recipe WHERE recipe.name =~ $searchTerm) return recipe",
                        parameters("searchTerm", ".*" + name + ".*"));
                var result = tx.run(query);
                return result.list(recipeMapper::recordToRecipeDto);
            });
        }
    }

    public RecipeDto getRecipeById(Long id) {
        logger.trace("Getting recipe with id " + id);
        try (var session = driver.session()) {
            return session.executeRead(tx -> {
                var query = new Query("MATCH(recipe: Recipe WHERE recipe.id = $id) return recipe",
                        parameters("id", id.toString()));
                var result = tx.run(query);
                RecipeDto dto = recipeMapper.recordToRecipeDto(result.single());

                if (dto == null) {
                    throw new NoSuchElementException("No recipe with id " + id);
                }

                dto.setIngredients(getIngredientsToRecipe(dto.getId()));

                return dto;
            });
        }
    }

    public List<IngredientDto> getIngredientsToRecipe(Long id) {
        logger.trace("Getting ingredients for recipe with id " + id);
        try (var session = driver.session()) {
            return session.executeRead(tx -> {
                var query = new Query(
                        "MATCH(recipe: Recipe WHERE recipe.id = $id)-[:CONTAINS_INGREDIENT]-(ingredient: Ingredient) return ingredient",
                        parameters("id", id.toString()));
                var result = tx.run(query);
                return result.list(ingredientMapper::recordToIngredientDto);
            });
        }
    }
}
