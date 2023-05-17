package at.ac.tuwien.ase.groupphase.backend.service;

import at.ac.tuwien.ase.groupphase.backend.dto.RecipeDto;
import at.ac.tuwien.ase.groupphase.backend.entity.Recipe;
import at.ac.tuwien.ase.groupphase.backend.mapper.RecipeMapper;
import at.ac.tuwien.ase.groupphase.backend.repository.RecipeRepository;
import jakarta.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class RecipeService {
    private final Logger logger = LoggerFactory.getLogger(RecipeService.class);
    private final RecipeRepository recipeRepository;
    private final RecipeMapper recipeMapper;

    @Autowired
    @NotNull
    public RecipeService(RecipeRepository recipeRepository) {
        this.recipeRepository = recipeRepository;
        recipeMapper = new RecipeMapper();
    }

    public List<RecipeDto> searchRecipeByName(String name) {
        logger.trace("Searching for recipe with name " + name);
        return recipeRepository.findByNameIsLike(name).stream().map(recipeMapper::recipeToRecipeDto).toList();
    }

    public RecipeDto getRecipeById(String id) {
        logger.trace("Getting recipe with id " + id);
        Optional<Recipe> recipe = recipeRepository.findById(id);
        if (recipe.isEmpty()) {
            throw new NoSuchElementException("No recipe with id " + id);
        }
        return recipeMapper.recipeToRecipeDto(recipe.get());
    }

    public List<RecipeDto> getRecipesFromCollections(List<String> names) {
        logger.trace("Getting recipes for {} collections", names.size());
        var recipes = recipeRepository.getRecipesFromCollection(names).stream().map(recipeMapper::recipeToRecipeDto)
                .toList();
        if (recipes.isEmpty()) {
            throw new NoSuchElementException("No recipes from the union collections with the size " + names.size());
        }
        return recipes;
    }
}
