package at.ac.tuwien.ase.groupphase.backend.service;

import at.ac.tuwien.ase.groupphase.backend.dto.RecipeDto;
import at.ac.tuwien.ase.groupphase.backend.dto.SuggestionDto;
import at.ac.tuwien.ase.groupphase.backend.entity.Ingredient;
import at.ac.tuwien.ase.groupphase.backend.entity.Recipe;
import at.ac.tuwien.ase.groupphase.backend.mapper.RecipeMapper;
import at.ac.tuwien.ase.groupphase.backend.repository.RecipeRepository;
import jakarta.validation.constraints.NotNull;
import org.hibernate.type.descriptor.java.DoubleJavaType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

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

    public List<RecipeDto> getMultipleRandomRecipes(int amount) {
        logger.trace("Getting " + amount + " random recipes");
        Optional<List<Recipe>> temp = this.recipeRepository.findAnyRecipeWithPictureWithIngredients(amount);
        List<RecipeDto> ret = temp.map(this.recipeMapper::recipeListToRecipeDtoList).orElse(null);
        return ret;
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

    public Page<RecipeDto> findRecipesBySearchString(String searchString, List<String> skillLevel, Integer maxTime,
            List<String> dietTypes, int page, int size) {
        logger.trace("Searching for recipes which contain the string: " + searchString);
        var collections = recipeRepository
                .findRecipesBySearchString(searchString, skillLevel, maxTime, dietTypes, PageRequest.of(page, size))
                .map(recipeMapper::recipeToRecipeDto);
        if (collections.isEmpty()) {
            throw new NoSuchElementException("No recipes found");
        }
        return collections;
    }

    public SuggestionDto getSuggestions(List<String> ids) {
        logger.trace("Getting suggestions from the given input");
        Optional<List<Recipe>> temp = this.recipeRepository.findMutlipleRecipesWithId(ids);
        List<Recipe> input = temp.orElse(null);

        // get all ingredients
        List<Ingredient> allIngredients = new ArrayList<>();
        input.forEach(x -> allIngredients.addAll(x.getIngredients()));

        // preprocess ingredients
        // change some to regex expression
        // remove duplicates
        List<Ingredient> ingredientsWithoutDuplicates = allIngredients.stream().distinct().toList();
        List<String> names = new ArrayList<>();
        ingredientsWithoutDuplicates.forEach(x -> names.add(x.getName()));
        // TODO

        // fetch similar meals
        Optional<List<Recipe>> temp_suggestions = this.recipeRepository.getSimilarMeals(names, ids);
        List<Recipe> similarMeals = temp_suggestions.orElse(null);
        // TODO

        // calculate weights of ingredients
        Map<String, Double> weightMap = new HashMap<>();
        populateWeightMap(weightMap, allIngredients);
        // TODO

        // sort meals according to their total weight
        // !! DONT FORGET to normalize ingredients of similarMeals (we searched for .*garlic.*, got garlic clove
        // and need to apply .*garlic.* weight to this ingredients and not 'garlic clove' weight
        Map<Recipe, Double> resultMap = new HashMap<>();
        similarMeals.forEach(x -> resultMap.put(x, calculateWeightOfRecipe(weightMap, x)));
        System.out.println("hello");
        // TODO

        // return top 20 suggestions
        // remove recipes that were given as input#
        // source: https://stackoverflow.com/questions/109383/sort-a-mapkey-value-by-values
        Map<Recipe, Double> ret = resultMap.entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder())).limit(20)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
        // TODO

        // ingredients vereinheitlichen damit man besser weighten kann?
        // e.g garlic clove vs garlic bulb, red wine vinegar
        // sugar, bacon, vinegar, cheese, potatoe, tomato, salt,
        return new SuggestionDto(this.recipeMapper.recipeListToRecipeDtoList(input),
                this.recipeMapper.recipeListToRecipeDtoList(ret.keySet().stream().toList()));
    }

    private void populateWeightMap(Map<String, Double> weightMap, List<Ingredient> allIngredients) {
        List<String> names = new ArrayList<>();
        allIngredients.forEach(x -> names.add(x.getName()));

        for (String n : names) {
            weightMap.putIfAbsent(n, 1D);
            weightMap.put(n, weightMap.get(n) + 0.1);
        }
    }

    private Double calculateWeightOfRecipe(Map<String, Double> weightMap, Recipe recipe) {
        Double ret = 0D;
        for (Ingredient i : recipe.getIngredients()) {
            if (weightMap.containsKey(i.getName())) {
                ret += weightMap.get(i.getName());
            }
        }
        return ret;
    }
}
