package at.ac.tuwien.ase.groupphase.backend.service;

import at.ac.tuwien.ase.groupphase.backend.dto.RecipeDto;
import at.ac.tuwien.ase.groupphase.backend.entity.Recipe;
import at.ac.tuwien.ase.groupphase.backend.mapper.RecipeMapper;
import at.ac.tuwien.ase.groupphase.backend.repository.RecipeRepository;
import jakarta.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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

    public RecipeDto getRecipeById(String id) {
        logger.trace("Getting recipe with id " + id);
        Optional<Recipe> recipe = recipeRepository.findById(id);
        if (recipe.isEmpty()) {
            throw new NoSuchElementException("No recipe with id " + id);
        }
        return recipeMapper.recipeToRecipeDto(recipe.get());
    }

    public Page<RecipeDto> getRecipesFromCollections(List<String> names, int page, int size) {
        logger.trace("Getting recipes for {} collections", names.size());
        var recipes = recipeRepository.getRecipesFromCollection(names, PageRequest.of(page, size))
                .map(recipeMapper::recipeToRecipeDto);
        if (recipes.isEmpty()) {
            throw new NoSuchElementException("No recipes from the union collections with the size " + names.size());
        }
        return recipes;
    }

    public Page<RecipeDto> findRecipesBySearchString(String searchString, int page, int size) {
        logger.trace("Searching for recipes which contain the string: " + searchString);
        var collections = recipeRepository.findRecipesBySearchString(searchString, PageRequest.of(page, size))
                .map(recipeMapper::recipeToRecipeDto);
        if (collections.isEmpty()) {
            throw new NoSuchElementException("No recipe collections found");
        }
        return collections;
    }

    public List<RecipeDto> getSuggestions(List<String> ids) {
        logger.trace("Getting suggestions from the given input");
        System.out.println(ids.toString());
        return null;
    }
}
